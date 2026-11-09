import FamilyControls
import SwiftUI

struct DashboardView: View {
    @EnvironmentObject private var session: AppSession
    @Environment(\.scenePhase) private var scenePhase
    @State private var showMedia = false
    @State private var showSettings = false
    @State private var showPreview = false
    @State private var safariGranted = false
    @State private var blockerGranted = false
    @State private var familyGranted = false
    @State private var familyStatus = ""
    @State private var showActivityPicker = false
    @State private var activitySelection = FamilyGuard.loadSelection()

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Text("dashboard_brand")
                        .font(.system(size: 12, weight: .medium))
                        .foregroundStyle(Palette.quietSlate)
                    HStack {
                        Text("dashboard_title")
                            .font(.inter(34, weight: .semibold))
                            .foregroundStyle(Palette.crispWhite)
                        Spacer()
                        Button {
                            showSettings = true
                        } label: {
                            Image(systemName: "gearshape")
                                .font(.system(size: 20, weight: .medium))
                                .foregroundStyle(Palette.softMist)
                        }
                        .accessibilityLabel(String(localized: "dashboard_settings"))
                    }

                    QuietCard {
                        VStack(alignment: .leading, spacing: 8) {
                            HStack(spacing: 10) {
                                Circle()
                                    .fill(session.guardEnabled && isWatching ? Palette.emerald : Palette.quietSlate)
                                    .frame(width: 10, height: 10)
                                Text(session.guardEnabled && isWatching ? "dashboard_status_active" : "dashboard_status_inactive")
                                    .font(.system(size: 20, weight: .medium))
                                    .foregroundStyle(session.guardEnabled && isWatching ? Palette.emerald : Palette.softMist)
                            }
                            Text(statusBody)
                                .font(.system(size: 14))
                                .foregroundStyle(Palette.softMist)
                            if !familyStatus.isEmpty {
                                Text(familyStatus)
                                    .font(.system(size: 14))
                                    .foregroundStyle(Palette.softMist)
                            }
                            if !FamilyGuard.lastError.isEmpty {
                                Text(FamilyGuard.lastError)
                                    .font(.system(size: 13))
                                    .foregroundStyle(Color(red: 248 / 255, green: 113 / 255, blue: 113 / 255))
                            }
                            Text("dashboard_google_app_note")
                                .font(.system(size: 13))
                                .foregroundStyle(Palette.quietSlate)
                            Button(String(localized: "onboarding_family_title")) {
                                Task {
                                    familyGranted = await FamilyGuard.requestAuthorization()
                                    familyStatus = FamilyGuard.statusText
                                    FamilyGuard.sync(enabled: session.guardEnabled)
                                }
                            }
                            .font(.system(size: 14, weight: .medium))
                            .foregroundStyle(Palette.emerald)
                            Button(String(localized: "dashboard_pick_browsers")) {
                                showActivityPicker = true
                            }
                            .font(.system(size: 14, weight: .medium))
                            .foregroundStyle(Palette.emerald)
                            if !blockerGranted {
                                Text("dashboard_needs_blocker")
                                    .font(.system(size: 14))
                                    .foregroundStyle(Color(red: 248 / 255, green: 113 / 255, blue: 113 / 255))
                            }
                            Button(String(localized: "onboarding_safari_title")) {
                                SafariGuard.openExtensionSettings()
                            }
                            .font(.system(size: 14, weight: .medium))
                            .foregroundStyle(Palette.emerald)
                            SafariExtensionHelp()
                        }
                    }

                    QuietCard {
                        HStack {
                            VStack(alignment: .leading, spacing: 6) {
                                Text("dashboard_guard_title")
                                    .font(.system(size: 16, weight: .medium))
                                    .foregroundStyle(Palette.crispWhite)
                                Text(session.guardEnabled ? "dashboard_guard_on_body" : "dashboard_guard_off_body")
                                    .font(.system(size: 14))
                                    .foregroundStyle(Palette.softMist)
                            }
                            Spacer()
                            Toggle("", isOn: $session.guardEnabled)
                                .labelsHidden()
                                .tint(Palette.emerald)
                                .onChange(of: session.guardEnabled) { _, enabled in
                                    FamilyGuard.sync(enabled: enabled)
                                }
                        }
                    }

                    HStack(spacing: 12) {
                        statTile(label: String(localized: "dashboard_stat_times_saved"), value: "\(session.timesSaved)")
                        statTile(label: String(localized: "dashboard_stat_goal_streak"), value: "\(session.goalStreak)")
                    }

                    Button {
                        showMedia = true
                    } label: {
                        QuietCard {
                            VStack(alignment: .leading, spacing: 10) {
                                Text("dashboard_current_goal")
                                    .font(.system(size: 12, weight: .medium))
                                    .foregroundStyle(Palette.quietSlate)
                                Text(session.goalTitle.isEmpty ? String(localized: "dashboard_set_reminder") : session.goalTitle)
                                    .font(.system(size: 20, weight: .medium))
                                    .foregroundStyle(Palette.crispWhite)
                                Text(
                                    session.goalBody.isEmpty
                                        ? String(localized: "dashboard_set_reminder_body")
                                        : session.goalBody
                                )
                                .font(.system(size: 14))
                                .foregroundStyle(Palette.softMist)
                            }
                        }
                    }
                    .buttonStyle(.plain)

                    CalmButton(title: String(localized: "dashboard_preview_overlay")) {
                        showPreview = true
                    }
                    CalmButton(
                        title: session.goalBody.isEmpty
                            ? String(localized: "dashboard_create_goal")
                            : String(localized: "dashboard_edit_goal")
                    ) {
                        showMedia = true
                    }
                }
                .padding(24)
            }
            .background(Palette.slateCharcoal.ignoresSafeArea())
            .navigationDestination(isPresented: $showMedia) {
                MediaSetupView()
            }
            .navigationDestination(isPresented: $showSettings) {
                SettingsView()
            }
            .fullScreenCover(isPresented: $showPreview) {
                OverlayView(title: session.goalTitle, bodyText: session.goalBody) {
                    showPreview = false
                }
            }
            .familyActivityPicker(
                headerText: String(localized: "dashboard_pick_browsers"),
                footerText: String(localized: "dashboard_pick_browsers_body"),
                isPresented: $showActivityPicker,
                selection: $activitySelection
            )
            .onChange(of: activitySelection) { _, selection in
                FamilyGuard.saveSelection(selection)
            }
            .task { await refresh() }
            .onChange(of: scenePhase) { _, phase in
                if phase == .active {
                    Task { await refresh() }
                }
            }
        }
    }

    private var isWatching: Bool {
        familyGranted || blockerGranted
    }

    private var statusBody: LocalizedStringKey {
        if !session.guardEnabled {
            return "dashboard_guard_off_body"
        }
        if blockerGranted && familyGranted {
            return safariGranted ? "dashboard_status_hybrid_body" : "dashboard_status_family_body"
        }
        if blockerGranted {
            return "dashboard_status_active_body_ios"
        }
        if familyGranted {
            return "dashboard_needs_blocker"
        }
        return "dashboard_needs_family"
    }

    private func refresh() async {
        familyGranted = FamilyGuard.isAuthorized
        familyStatus = FamilyGuard.statusText
        blockerGranted = await SafariGuard.isBlockerEnabled()
        safariGranted = await SafariGuard.isExtensionEnabled()
        session.reloadStats()
        FamilyGuard.sync(enabled: session.guardEnabled)
    }

    private func statTile(label: String, value: String) -> some View {
        QuietCard {
            VStack(alignment: .leading, spacing: 8) {
                Text(label.uppercased())
                    .font(.system(size: 12, weight: .medium))
                    .foregroundStyle(Palette.quietSlate)
                Text(value)
                    .font(.system(size: 26, weight: .semibold))
                    .foregroundStyle(Palette.crispWhite)
            }
        }
    }
}
