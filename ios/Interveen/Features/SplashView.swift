import SwiftUI

struct SplashView: View {
    var body: some View {
        Image("InterveenLogo")
            .resizable()
            .scaledToFit()
            .frame(maxWidth: 240)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Palette.slateCharcoal.ignoresSafeArea())
    }
}
