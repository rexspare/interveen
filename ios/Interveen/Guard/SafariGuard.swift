import SafariServices
import SwiftUI
import UIKit
import UserNotifications

enum SafariGuard {
    static let extensionIdentifier = InterveenDefaults.extensionIdentifier
    static let blockerIdentifier = InterveenDefaults.blockerIdentifier
    private static let openCooldown: TimeInterval = 65
    private static var lastOpenAt: Date?

    static func isExtensionEnabled() async -> Bool {
        if #available(iOS 26.2, *) {
            do {
                let state = try await SFSafariExtensionManager.stateOfExtension(withIdentifier: extensionIdentifier)
                return state.isEnabled
            } catch {
                return false
            }
        }
        return false
    }

    static func isBlockerEnabled() async -> Bool {
        await withCheckedContinuation { continuation in
            SFContentBlockerManager.getStateOfContentBlocker(withIdentifier: blockerIdentifier) { state, _ in
                continuation.resume(returning: state?.isEnabled ?? false)
            }
        }
    }

    static func reloadBlocker() {
        SFContentBlockerManager.reloadContentBlocker(withIdentifier: blockerIdentifier, completionHandler: { _ in })
    }

    @discardableResult
    static func openExtensionSettings() -> Bool {
        if let lastOpenAt, Date().timeIntervalSince(lastOpenAt) < openCooldown {
            return false
        }
        lastOpenAt = Date()
        if #available(iOS 26.2, *) {
            Task { @MainActor in
                do {
                    try await SFSafariSettings.openExtensionsSettings(
                        forIdentifiers: [blockerIdentifier, extensionIdentifier]
                    )
                } catch {
                    // Apple rate-limits this API. Steps on screen are the fallback.
                }
            }
            return true
        }
        if let url = URL(string: UIApplication.openSettingsURLString) {
            UIApplication.shared.open(url)
            return true
        }
        return false
    }
}

enum NotificationGuard {
    static func authorizationStatus() async -> UNAuthorizationStatus {
        await UNUserNotificationCenter.current().notificationSettings().authorizationStatus
    }

    static func isGranted() async -> Bool {
        let status = await authorizationStatus()
        return status == .authorized || status == .provisional || status == .ephemeral
    }

    static func request() async -> Bool {
        do {
            return try await UNUserNotificationCenter.current()
                .requestAuthorization(options: [.alert, .sound, .badge])
        } catch {
            return false
        }
    }
}

struct SafariExtensionHelp: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text("safari_help_title")
                .font(.system(size: 16, weight: .medium))
                .foregroundStyle(Palette.crispWhite)
            Text("safari_help_scope")
                .font(.system(size: 14))
                .foregroundStyle(Palette.softMist)
            step("1", String(localized: "safari_help_step_1"))
            step("2", String(localized: "safari_help_step_2"))
            step("3", String(localized: "safari_help_step_3"))
            step("4", String(localized: "safari_help_step_4"))
            step("5", String(localized: "safari_help_step_5"))
        }
    }

    private func step(_ number: String, _ text: String) -> some View {
        HStack(alignment: .top, spacing: 10) {
            Text(number)
                .font(.system(size: 13, weight: .semibold))
                .foregroundStyle(Palette.emerald)
            Text(text)
                .font(.system(size: 14))
                .foregroundStyle(Palette.crispWhite)
        }
    }
}
