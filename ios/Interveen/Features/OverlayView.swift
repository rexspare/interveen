import SwiftUI

struct OverlayView: View {
    let title: String
    let bodyText: String
    let onRemembered: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Text("overlay_moment")
                .font(.system(size: 12, weight: .medium))
                .foregroundStyle(Palette.quietSlate)
                .frame(maxWidth: .infinity, alignment: .leading)
            Spacer()
            VStack(alignment: .leading, spacing: 12) {
                Text(title.isEmpty ? String(localized: "overlay_default_title") : title)
                    .font(.system(size: 28, weight: .semibold))
                    .foregroundStyle(Palette.crispWhite)
                Text(bodyText.isEmpty ? String(localized: "overlay_default_body") : bodyText)
                    .font(.system(size: 16))
                    .foregroundStyle(Palette.softMist)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            Spacer()
            CalmButton(title: String(localized: "overlay_action_remember"), action: onRemembered)
        }
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Palette.slateCharcoal.ignoresSafeArea())
    }
}
