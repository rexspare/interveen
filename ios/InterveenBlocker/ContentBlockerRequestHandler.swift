import Foundation

final class ContentBlockerRequestHandler: NSObject, NSExtensionRequestHandling {
    func beginRequest(with context: NSExtensionContext) {
        let item = NSExtensionItem()
        let resource = guardEnabled ? "blockerList" : "emptyBlockerList"
        if let url = Bundle.main.url(forResource: resource, withExtension: "json") {
            item.attachments = [NSItemProvider(contentsOf: url)!]
        }
        context.completeRequest(returningItems: [item])
    }

    private var guardEnabled: Bool {
        let defaults = UserDefaults(suiteName: "group.com.interveen.ios.shared")
        if defaults?.object(forKey: "guard_enabled") == nil {
            return true
        }
        return defaults?.bool(forKey: "guard_enabled") ?? true
    }
}
