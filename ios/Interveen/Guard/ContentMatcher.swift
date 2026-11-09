import Foundation

struct MatchResult: Equatable {
    var matched: Bool
    var reason: String
    var evidence: String

    static let none = MatchResult(matched: false, reason: "none", evidence: "")
}

enum ContentMatcher {
    private static let hostPattern = try! NSRegularExpression(
        pattern: #"(?:https?://)?((?:[a-z0-9-]+\.)+[a-z]{2,})(/[^\s]*)?"#,
        options: [.caseInsensitive]
    )

    static func evaluate(_ rawInput: String) -> MatchResult {
        let haystack = normalizeText(rawInput)
        guard !tooShort(haystack) else { return .none }

        let hosts = extractHosts(haystack)
        for host in hosts {
            if let domain = DefaultBlocklist.domains.first(where: { host == $0 || host.hasSuffix(".\($0)") }) {
                return MatchResult(matched: true, reason: "domain", evidence: domain)
            }
            let tld = host.split(separator: ".").last.map(String.init) ?? ""
            if DefaultBlocklist.adultTlds.contains(tld) {
                return MatchResult(matched: true, reason: "domain", evidence: host)
            }
            if hostLooksAdult(host) {
                return MatchResult(matched: true, reason: "domain", evidence: host)
            }
        }

        for keyword in DefaultBlocklist.keywords {
            let needle = normalizeText(keyword)
            guard needle.count >= 2, haystack.contains(needle) else { continue }
            return MatchResult(matched: true, reason: "keyword", evidence: keyword.trimmingCharacters(in: .whitespaces))
        }
        return .none
    }

    private static func normalizeText(_ rawInput: String) -> String {
        let decoded = rawInput.removingPercentEncoding ?? rawInput
        let spaced = decoded.replacingOccurrences(
            of: #"(?<=\p{L}|\p{N})\+(?=\p{L}|\p{N})"#,
            with: " ",
            options: .regularExpression,
        )
        return spaced.precomposedStringWithCompatibilityMapping
            .lowercased(with: Locale(identifier: "en_US_POSIX"))
            .trimmingCharacters(in: .whitespacesAndNewlines)
    }

    private static func tooShort(_ haystack: String) -> Bool {
        if haystack.count < 2 { return true }
        if haystack.count >= 3 { return false }
        return haystack.allSatisfy { $0.isASCII }
    }

    static func extractHosts(_ rawInput: String) -> Set<String> {
        let range = NSRange(rawInput.startIndex..<rawInput.endIndex, in: rawInput)
        let matches = hostPattern.matches(in: rawInput, options: [], range: range)
        return Set(matches.compactMap { match -> String? in
            guard match.numberOfRanges > 1,
                  let swiftRange = Range(match.range(at: 1), in: rawInput) else { return nil }
            let host = normalizeHost(String(rawInput[swiftRange]))
            return host.contains(".") ? host : nil
        })
    }

    private static func normalizeHost(_ value: String) -> String {
        var host = value.lowercased()
        if host.hasPrefix("www.") { host.removeFirst(4) }
        return host.trimmingCharacters(in: CharacterSet(charactersIn: "."))
    }

    static func hostLooksAdult(_ host: String) -> Bool {
        for label in host.split(separator: ".").map(String.init) {
            if label == "jav" || (label.hasPrefix("jav") && !label.hasPrefix("java")) {
                return true
            }
            if DefaultBlocklist.hostTokens.contains(where: { label.contains($0) }) {
                return true
            }
        }
        return false
    }
}
