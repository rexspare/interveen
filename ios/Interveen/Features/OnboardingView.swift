import SwiftUI

struct OnboardingView: View {
    @EnvironmentObject private var session: AppSession
    @Environment(\.scenePhase) private var scenePhase
    @State private var page = 0
    @State private var safariGranted = false
    @State private var blockerGranted = false
    @State private var familyGranted = false
    @State private var notificationsGranted = false
    @State private var openedSafariSettings = false
    @State private var familyAttempted = false

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("\(page + 1) / 3")
                .font(.system(size: 12, weight: .medium))
                .foregroundStyle(Palette.quietSlate)

            TabView(selection: $page) {
                introPage(
                    titleKey: "onboarding_welcome_title",
                    bodyKey: "onboarding_welcome_body",
                    page: 0
                )
                .tag(0)
                introPage(
                    titleKey: "onboarding_how_title",
                    bodyKey: "onboarding_how_body_ios",
                    page: 1
                )
                .tag(1)
                permissionsPage.tag(2)
            }
            .tabViewStyle(.page(indexDisplayMode: .never))

            HStack(spacing: 8) {
                ForEach(0..<3, id: \.self) { index in
                    Circle()
                        .fill(page == index ? Palette.emerald : Palette.quietSlate)
                        .frame(width: page == index ? 8 : 6, height: page == index ? 8 : 6)
                }
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 4)

            CalmButton(
                title: page < 2
                    ? String(localized: "onboarding_next")
                    : (canBegin
                        ? String(localized: "onboarding_begin")
                        : String(localized: "onboarding_grant_required")),
                enabled: page < 2 || canBegin
            ) {
                if page < 2 {
                    withAnimation { page += 1 }
                } else {
                    session.onboardingComplete = true
                    session.guardEnabled = true
                    FamilyGuard.sync(enabled: true)
                }
            }
        }
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(Palette.slateCharcoal)
        .task { await refreshPermissions() }
        .onChange(of: scenePhase) { _, phase in
            if phase == .active {
                Task { await refreshPermissions() }
            }
        }
    }

    private var canBegin: Bool {
        familyGranted || familyAttempted || blockerGranted || safariGranted || openedSafariSettings
    }

    private func refreshPermissions() async {
        familyGranted = FamilyGuard.isAuthorized
        blockerGranted = await SafariGuard.isBlockerEnabled()
        safariGranted = await SafariGuard.isExtensionEnabled()
        notificationsGranted = await NotificationGuard.isGranted()
        if session.guardEnabled {
            SafariGuard.reloadBlocker()
        }
    }

    private func introPage(titleKey: LocalizedStringKey, bodyKey: LocalizedStringKey, page: Int) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(titleKey)
                .font(.system(size: 34, weight: .semibold))
                .foregroundStyle(Palette.crispWhite)
            Text(bodyKey)
                .font(.system(size: 16))
                .foregroundStyle(Palette.softMist)
            OnboardingGraphic(page: page)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }

    private var permissionsPage: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("onboarding_permissions_title")
                    .font(.system(size: 34, weight: .semibold))
                    .foregroundStyle(Palette.crispWhite)
                Text("onboarding_permissions_body_ios")
                    .font(.system(size: 16))
                    .foregroundStyle(Palette.softMist)
                permissionRow(
                    title: String(localized: "onboarding_family_title"),
                    body: String(localized: "onboarding_family_body"),
                    enabled: familyGranted
                ) {
                    familyAttempted = true
                    Task {
                        familyGranted = await FamilyGuard.requestAuthorization()
                    }
                }
                permissionRow(
                    title: String(localized: "onboarding_safari_title"),
                    body: String(localized: "onboarding_safari_body"),
                    enabled: blockerGranted
                ) {
                    openedSafariSettings = true
                    SafariGuard.openExtensionSettings()
                }
                SafariExtensionHelp()
                permissionRow(
                    title: String(localized: "onboarding_notification_title"),
                    body: String(localized: "onboarding_notification_body_ios"),
                    enabled: notificationsGranted
                ) {
                    Task {
                        notificationsGranted = await NotificationGuard.request()
                    }
                }
            }
        }
    }

    private func permissionRow(
        title: String,
        body: String,
        enabled: Bool,
        action: @escaping () -> Void
    ) -> some View {
        Button(action: action) {
            HStack {
                VStack(alignment: .leading, spacing: 6) {
                    Text(title)
                        .font(.system(size: 16, weight: .medium))
                        .foregroundStyle(Palette.crispWhite)
                    Text(body)
                        .font(.system(size: 14))
                        .foregroundStyle(Palette.softMist)
                        .multilineTextAlignment(.leading)
                }
                Spacer()
                Toggle("", isOn: .constant(enabled))
                    .labelsHidden()
                    .tint(Palette.emerald)
                    .allowsHitTesting(false)
            }
            .padding(20)
            .background(Palette.coolGray, in: RoundedRectangle(cornerRadius: InterveenMetrics.cardRadius, style: .continuous))
        }
        .buttonStyle(.plain)
    }
}

private struct OnboardingGraphic: View {
    let page: Int

    private let raised = Color(red: 51 / 255, green: 65 / 255, blue: 85 / 255)
    private let inset = Color(red: 15 / 255, green: 23 / 255, blue: 42 / 255)

    var body: some View {
        GeometryReader { geo in
            ZStack {
                if page == 0 {
                    welcomeArt(in: geo.size)
                } else {
                    howArt(in: geo.size)
                }
            }
            .frame(width: geo.size.width, height: geo.size.height)
        }
        .background(Palette.coolGray, in: RoundedRectangle(cornerRadius: 24, style: .continuous))
        .clipShape(RoundedRectangle(cornerRadius: 24, style: .continuous))
    }

    private func welcomeArt(in size: CGSize) -> some View {
        let cardW = min(size.width * 0.42, 196)
        let cardH = min(size.height * 0.62, 280)
        return ZStack {
            Circle()
                .fill(Palette.emerald.opacity(0.12))
                .frame(width: min(size.width, size.height) * 0.72)
            Circle()
                .fill(Palette.emerald.opacity(0.08))
                .frame(width: min(size.width, size.height) * 0.48)
            VStack(spacing: 14) {
                RoundedRectangle(cornerRadius: 14, style: .continuous)
                    .fill(Palette.emerald.opacity(0.28))
                    .overlay {
                        Circle()
                            .fill(Palette.emerald)
                            .frame(width: 10, height: 10)
                    }
                    .frame(maxHeight: .infinity)
                Capsule()
                    .fill(Palette.crispWhite.opacity(0.88))
                    .frame(width: cardW * 0.56, height: 8)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.leading, 18)
                Capsule()
                    .fill(Palette.crispWhite.opacity(0.32))
                    .frame(width: cardW * 0.38, height: 7)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.leading, 18)
                    .padding(.bottom, 8)
            }
            .padding(16)
            .frame(width: cardW, height: cardH)
            .background(raised, in: RoundedRectangle(cornerRadius: 22, style: .continuous))
        }
    }

    private func howArt(in size: CGSize) -> some View {
        let cardW = min(size.width * 0.32, 148)
        let cardH = min(size.height * 0.62, 268)
        return HStack(spacing: 12) {
            VStack(alignment: .leading, spacing: 10) {
                HStack(spacing: 8) {
                    Circle()
                        .fill(Color(red: 248 / 255, green: 113 / 255, blue: 113 / 255))
                        .frame(width: 8, height: 8)
                    Capsule()
                        .fill(Palette.quietSlate.opacity(0.45))
                        .frame(height: 8)
                }
                RoundedRectangle(cornerRadius: 10, style: .continuous)
                    .fill(inset)
                    .frame(maxHeight: .infinity)
            }
            .padding(14)
            .frame(width: cardW, height: cardH)
            .background(raised, in: RoundedRectangle(cornerRadius: 18, style: .continuous))

            Image(systemName: "arrow.right")
                .font(.system(size: 16, weight: .bold))
                .foregroundStyle(Palette.emerald)

            VStack(alignment: .leading, spacing: 10) {
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .fill(Palette.emerald.opacity(0.32))
                    .frame(maxHeight: .infinity)
                Capsule()
                    .fill(Palette.emerald)
                    .frame(height: 8)
                Capsule()
                    .fill(Palette.crispWhite.opacity(0.32))
                    .frame(width: cardW * 0.48, height: 7)
            }
            .padding(14)
            .frame(width: cardW, height: cardH)
            .background(Palette.emerald.opacity(0.18), in: RoundedRectangle(cornerRadius: 20, style: .continuous))
        }
    }
}
