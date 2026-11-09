import Foundation

enum InterveenDefaults {
    static let suiteName = "group.com.interveen.ios.shared"
    static let extensionIdentifier = "com.interveen.appios.Guard"
    static let blockerIdentifier = "com.interveen.appios.Blocker"

    static var suite: UserDefaults {
        UserDefaults(suiteName: suiteName) ?? .standard
    }

    enum Key {
        static let onboardingComplete = "onboarding_complete"
        static let guardEnabled = "guard_enabled"
        static let timesSaved = "times_saved"
        static let goalStreak = "goal_streak"
        static let goalTitle = "goal_title"
        static let goalBody = "goal_body"
        static let languageTag = "language_tag"
        static let lastTriggerAt = "last_trigger_at"
        static let lastEvidence = "last_evidence"
        static let lastStreakDay = "last_streak_day"
        static let pendingGoalOverlay = "pending_goal_overlay"
        static let familySelection = "family_selection"
    }
}

enum GuardStore {
    private static let minGap: TimeInterval = 4
    private static let sameEvidenceCooldown: TimeInterval = 12

    static var isGuardEnabled: Bool {
        let defaults = InterveenDefaults.suite
        if defaults.object(forKey: InterveenDefaults.Key.guardEnabled) == nil {
            return true
        }
        return defaults.bool(forKey: InterveenDefaults.Key.guardEnabled)
    }

    static var isConfigured: Bool {
        InterveenDefaults.suite.object(forKey: InterveenDefaults.Key.guardEnabled) != nil
    }

    static var goalTitle: String {
        InterveenDefaults.suite.string(forKey: InterveenDefaults.Key.goalTitle) ?? ""
    }

    static var goalBody: String {
        InterveenDefaults.suite.string(forKey: InterveenDefaults.Key.goalBody) ?? ""
    }

    static var displayTitle: String {
        goalTitle.isEmpty ? "Remember why you started." : goalTitle
    }

    static var displayBody: String {
        goalBody.isEmpty ? "This pause is the win." : goalBody
    }

    static func markPendingGoalOverlay() {
        let defaults = InterveenDefaults.suite
        defaults.set(true, forKey: InterveenDefaults.Key.pendingGoalOverlay)
        defaults.synchronize()
    }

    @discardableResult
    static func consumePendingGoalOverlay() -> Bool {
        let defaults = InterveenDefaults.suite
        let pending = defaults.bool(forKey: InterveenDefaults.Key.pendingGoalOverlay)
        if pending {
            defaults.set(false, forKey: InterveenDefaults.Key.pendingGoalOverlay)
        }
        return pending
    }

    static func evaluate(_ url: String) -> [String: Any] {
        let match = ContentMatcher.evaluate(url)
        let now = Date().timeIntervalSince1970
        let lastAt = InterveenDefaults.suite.double(forKey: InterveenDefaults.Key.lastTriggerAt)
        let lastEvidence = InterveenDefaults.suite.string(forKey: InterveenDefaults.Key.lastEvidence) ?? ""

        var show = isGuardEnabled && match.matched
        if show, now - lastAt < minGap {
            show = false
        }
        if show, match.evidence == lastEvidence, now - lastAt < sameEvidenceCooldown {
            show = false
        }

        return [
            "show": show,
            "guardEnabled": isGuardEnabled,
            "configured": isConfigured,
            "matched": match.matched,
            "reason": match.reason,
            "evidence": match.evidence,
            "title": goalTitle.isEmpty ? "Remember why you started." : goalTitle,
            "body": goalBody.isEmpty ? "This pause is the win." : goalBody,
            "moment": "A moment.",
            "remember": "I Remember My Goal",
        ]
    }

    static func recordInterruption(evidence: String) {
        let defaults = InterveenDefaults.suite
        let now = Date()
        defaults.set(now.timeIntervalSince1970, forKey: InterveenDefaults.Key.lastTriggerAt)
        defaults.set(evidence, forKey: InterveenDefaults.Key.lastEvidence)
        defaults.set(defaults.integer(forKey: InterveenDefaults.Key.timesSaved) + 1, forKey: InterveenDefaults.Key.timesSaved)

        let day = Calendar.current.startOfDay(for: now)
        let lastDay = Date(timeIntervalSince1970: defaults.double(forKey: InterveenDefaults.Key.lastStreakDay))
        let lastDayStart = Calendar.current.startOfDay(for: lastDay)
        if day == lastDayStart {
            // already counted today
        } else if let yesterday = Calendar.current.date(byAdding: .day, value: -1, to: day), yesterday == lastDayStart {
            defaults.set(defaults.integer(forKey: InterveenDefaults.Key.goalStreak) + 1, forKey: InterveenDefaults.Key.goalStreak)
        } else {
            defaults.set(1, forKey: InterveenDefaults.Key.goalStreak)
        }
        defaults.set(day.timeIntervalSince1970, forKey: InterveenDefaults.Key.lastStreakDay)
        defaults.synchronize()
    }
}
