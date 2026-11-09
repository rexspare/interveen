import SafariServices

class SafariWebExtensionHandler: NSObject, NSExtensionRequestHandling {
    func beginRequest(with context: NSExtensionContext) {
        let request = context.inputItems.first as? NSExtensionItem
        let message: Any?
        if #available(iOS 15.0, *) {
            message = request?.userInfo?[SFExtensionMessageKey]
        } else {
            message = request?.userInfo?["message"]
        }

        let payload = message as? [String: Any] ?? [:]
        let type = payload["type"] as? String ?? "evaluate"
        let url = payload["url"] as? String ?? ""
        let evidence = payload["evidence"] as? String ?? ""

        let reply: [String: Any]
        switch type {
        case "remembered":
            GuardStore.recordInterruption(evidence: evidence)
            reply = ["ok": true]
        default:
            reply = GuardStore.evaluate(url)
        }

        let response = NSExtensionItem()
        if #available(iOS 15.0, *) {
            response.userInfo = [SFExtensionMessageKey: reply]
        } else {
            response.userInfo = ["message": reply]
        }
        context.completeRequest(returningItems: [response], completionHandler: nil)
    }
}
