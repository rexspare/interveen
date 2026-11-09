import FamilyControls
import Foundation
import ManagedSettings
import UserNotifications

enum InterveenDeepLink {
    static let scheme = "interveen"
    static let goalHost = "goal"
    static let goalURL = URL(string: "interveen://goal")!

    static func isGoal(_ url: URL) -> Bool {
        url.scheme == scheme && url.host == goalHost
    }
}

enum FamilyGuard {
    private static let adultStore = ManagedSettingsStore()
    private static let domainStore = ManagedSettingsStore(named: .init("interveen.domains"))

    static var lastError: String = ""

    static var isAuthorized: Bool {
        AuthorizationCenter.shared.authorizationStatus == .approved
    }

    static var statusText: String {
        switch AuthorizationCenter.shared.authorizationStatus {
        case .approved:
            return "Screen Time is on. Apple’s adult catalog can pause Safari and Chrome. Safari still needs Interveen Blocker for sites like wow.xxx."
        case .denied:
            return "Screen Time was denied. Delete the app and reinstall, or enable Interveen under Settings → Screen Time."
        default:
            return "Screen Time is not approved yet. Tap Screen Time Guard and choose Allow. The Google app cannot be filtered."
        }
    }

    static func requestAuthorization() async -> Bool {
        lastError = ""
        do {
            try await AuthorizationCenter.shared.requestAuthorization(for: .individual)
            if isAuthorized, GuardStore.isGuardEnabled {
                await MainActor.run { apply() }
            } else if !isAuthorized {
                lastError = "Apple did not approve Screen Time for Interveen."
            }
            return isAuthorized
        } catch {
            lastError = error.localizedDescription
            return false
        }
    }

    static func sync(enabled: Bool) {
        if enabled, GuardStore.isGuardEnabled, isAuthorized {
            apply()
        } else if !enabled || !GuardStore.isGuardEnabled {
            adultStore.clearAllSettings()
            domainStore.clearAllSettings()
        }
        SafariGuard.reloadBlocker()
    }

    static func saveSelection(_ selection: FamilyActivitySelection) {
        if let data = try? JSONEncoder().encode(selection) {
            InterveenDefaults.suite.set(data, forKey: InterveenDefaults.Key.familySelection)
        }
        if GuardStore.isGuardEnabled, isAuthorized {
            apply(selection: selection)
        }
    }

    static func loadSelection() -> FamilyActivitySelection {
        guard let data = InterveenDefaults.suite.data(forKey: InterveenDefaults.Key.familySelection),
              let selection = try? JSONDecoder().decode(FamilyActivitySelection.self, from: data) else {
            return FamilyActivitySelection()
        }
        return selection
    }

    static func apply(selection: FamilyActivitySelection? = nil) {
        var extras = Set(DefaultBlocklist.domains.prefix(50).map { WebDomain(domain: $0) })
        extras.formUnion((selection ?? loadSelection()).webDomains)
        // Apple’s adult catalog. Extra string domains are best-effort; tokens from the
        // site picker are the ones Screen Time actually honors.
        adultStore.webContent.blockedByFilter = .auto(extras)
        let picked = (selection ?? loadSelection()).webDomains
        if !picked.isEmpty {
            domainStore.webContent.blockedByFilter = .specific(picked)
        }
    }

    static func scheduleGoalNotification() {
        let content = UNMutableNotificationContent()
        content.title = "A moment."
        content.body = GuardStore.displayTitle
        content.sound = .default
        content.userInfo = ["deeplink": InterveenDeepLink.goalURL.absoluteString]
        content.categoryIdentifier = "GOAL_REMINDER"
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 0.4, repeats: false)
        let request = UNNotificationRequest(
            identifier: "interveen.goal.\(Int(Date().timeIntervalSince1970))",
            content: content,
            trigger: trigger
        )
        UNUserNotificationCenter.current().add(request, withCompletionHandler: nil)
    }
}

final class InterveenNotifications: NSObject, UNUserNotificationCenterDelegate {
    static let shared = InterveenNotifications()
    var onGoalReminder: (() -> Void)?

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification
    ) async -> UNNotificationPresentationOptions {
        if isGoal(notification) {
            onGoalReminder?()
            return []
        }
        return [.banner, .sound]
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse
    ) async {
        if isGoal(response.notification) {
            onGoalReminder?()
        }
    }

    private func isGoal(_ notification: UNNotification) -> Bool {
        if notification.request.content.categoryIdentifier == "GOAL_REMINDER" {
            return true
        }
        if let raw = notification.request.content.userInfo["deeplink"] as? String,
           let url = URL(string: raw) {
            return InterveenDeepLink.isGoal(url)
        }
        return false
    }
}
