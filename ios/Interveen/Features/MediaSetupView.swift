import SwiftUI

struct MediaSetupView: View {
    @EnvironmentObject private var session: AppSession
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("media_title")
                    .font(.system(size: 34, weight: .semibold))
                    .foregroundStyle(Palette.crispWhite)
                Text("media_body")
                    .font(.system(size: 16))
                    .foregroundStyle(Palette.softMist)

                QuietCard {
                    VStack(alignment: .leading, spacing: 12) {
                        Text("media_field_title")
                            .font(.system(size: 12, weight: .medium))
                            .foregroundStyle(Palette.quietSlate)
                        TextField(String(localized: "media_hint_title"), text: $session.goalTitle)
                            .foregroundStyle(Palette.crispWhite)
                        Text("media_field_words")
                            .font(.system(size: 12, weight: .medium))
                            .foregroundStyle(Palette.quietSlate)
                        TextField(String(localized: "media_hint_words"), text: $session.goalBody, axis: .vertical)
                            .foregroundStyle(Palette.crispWhite)
                            .lineLimit(3...6)
                    }
                }

                CalmButton(title: String(localized: "media_save_words"), enabled: !session.goalBody.isEmpty) {
                    dismiss()
                }

                Text("media_privacy")
                    .font(.system(size: 14))
                    .foregroundStyle(Palette.quietSlate)
            }
            .padding(24)
        }
        .background(Palette.slateCharcoal.ignoresSafeArea())
        .navigationBarTitleDisplayMode(.inline)
    }
}
