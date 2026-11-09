import SwiftUI
import UserNotifications

@main
struct InterveenApp: App {
    @StateObject private var session = AppSession()

    init() {
        UNUserNotificationCenter.current().delegate = InterveenNotifications.shared
    }

    var body: some Scene {
        WindowGroup {
            RootView()
                .environmentObject(session)
                .preferredColorScheme(.dark)
        }
    }
}

struct RootView: View {
    @EnvironmentObject private var session: AppSession
    @Environment(\.scenePhase) private var scenePhase
    @State private var showSplash = true

    var body: some View {
        Group {
            if showSplash {
                SplashView()
            } else if session.onboardingComplete {
                DashboardView()
            } else {
                OnboardingView()
            }
        }
        .background(Palette.slateCharcoal.ignoresSafeArea())
        .environment(\.locale, session.languageTag.isEmpty ? Locale.autoupdatingCurrent : Locale(identifier: session.languageTag))
        .onOpenURL { url in
            if InterveenDeepLink.isGoal(url) {
                session.presentGoalOverlay()
            }
        }
        .onChange(of: scenePhase) { _, phase in
            if phase == .active {
                session.presentGoalOverlayIfPending()
                FamilyGuard.sync(enabled: session.guardEnabled)
            }
        }
        .onAppear {
            InterveenNotifications.shared.onGoalReminder = {
                session.presentGoalOverlay()
            }
        }
        .fullScreenCover(isPresented: $session.showGoalOverlay) {
            OverlayView(title: session.goalTitle, bodyText: session.goalBody) {
                GuardStore.recordInterruption(evidence: "family-shield")
                session.showGoalOverlay = false
                session.reloadStats()
            }
        }
        .task {
            try? await Task.sleep(for: .milliseconds(1150))
            withAnimation(.easeInOut(duration: 0.35)) {
                showSplash = false
            }
            session.presentGoalOverlayIfPending()
            FamilyGuard.sync(enabled: session.guardEnabled)
        }
    }
}

final class AppSession: ObservableObject {
    private static let store = InterveenDefaults.suite

    @AppStorage(InterveenDefaults.Key.onboardingComplete, store: store) var onboardingComplete = false
    @AppStorage(InterveenDefaults.Key.guardEnabled, store: store) var guardEnabled = true
    @AppStorage(InterveenDefaults.Key.timesSaved, store: store) var timesSaved = 0
    @AppStorage(InterveenDefaults.Key.goalStreak, store: store) var goalStreak = 0
    @AppStorage(InterveenDefaults.Key.goalTitle, store: store) var goalTitle = ""
    @AppStorage(InterveenDefaults.Key.goalBody, store: store) var goalBody = ""
    @AppStorage(InterveenDefaults.Key.languageTag, store: store) var languageTag = ""
    @Published var showGoalOverlay = false

    func reloadStats() {
        timesSaved = InterveenDefaults.suite.integer(forKey: InterveenDefaults.Key.timesSaved)
        goalStreak = InterveenDefaults.suite.integer(forKey: InterveenDefaults.Key.goalStreak)
    }

    func presentGoalOverlay() {
        GuardStore.consumePendingGoalOverlay()
        showGoalOverlay = true
    }

    func presentGoalOverlayIfPending() {
        if GuardStore.consumePendingGoalOverlay() {
            showGoalOverlay = true
        }
    }
}
