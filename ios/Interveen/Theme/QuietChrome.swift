import SwiftUI

struct QuietCard<Content: View>: View {
    let content: Content

    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    var body: some View {
        content
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(20)
            .background(Palette.coolGray, in: RoundedRectangle(cornerRadius: InterveenMetrics.cardRadius, style: .continuous))
    }
}

struct CalmButton: View {
    let title: String
    var enabled = true
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.system(size: 16, weight: .medium))
                .frame(maxWidth: .infinity)
                .padding(.vertical, 16)
                .foregroundStyle(enabled ? Palette.slateCharcoal : Palette.quietSlate)
                .background(
                    enabled ? Palette.emerald : Palette.coolGray,
                    in: RoundedRectangle(cornerRadius: 18, style: .continuous)
                )
        }
        .disabled(!enabled)
        .buttonStyle(.plain)
    }
}
