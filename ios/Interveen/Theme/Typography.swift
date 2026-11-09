import SwiftUI

extension Font {
    static func inter(_ size: CGFloat, weight: Font.Weight = .regular) -> Font {
        let name: String
        switch weight {
        case .bold, .semibold, .heavy, .black:
            name = "Inter-SemiBold"
        case .medium:
            name = "Inter-Medium"
        default:
            name = "Inter-Regular"
        }
        return .custom(name, size: size)
    }
}
