package com.interveen.app.domain.model

data class GuardState(
    val enabled: Boolean = false,
    val accessibilityGranted: Boolean = false,
    val overlayGranted: Boolean = false,
    val notificationsGranted: Boolean = true,
    val restrictedSettingsAllowed: Boolean = true,
    val needsRestrictedSettings: Boolean = false,
) {
    val isFullyArmed: Boolean
        get() = enabled && accessibilityGranted && overlayGranted
}

enum class GoalKind {
    VIDEO,
    IMAGE,
    TEXT,
    UNSET,
}

data class Goal(
    val kind: GoalKind = GoalKind.UNSET,
    val title: String = "",
    val body: String = "",
    val mediaUri: String? = null,
) {
    val isConfigured: Boolean
        get() = when (kind) {
            GoalKind.UNSET -> false
            GoalKind.TEXT -> body.isNotBlank()
            GoalKind.VIDEO, GoalKind.IMAGE -> !mediaUri.isNullOrBlank()
        }
}

data class DashboardStats(
    val timesSaved: Int = 0,
    val goalStreak: Int = 0,
    val lastTriggerEpochDay: Long? = null,
)

data class MatchResult(
    val matched: Boolean,
    val reason: MatchReason = MatchReason.NONE,
    val evidence: String = "",
) {
    companion object {
        val None = MatchResult(matched = false)
    }
}

enum class MatchReason {
    NONE,
    DOMAIN,
    KEYWORD,
}

data class AppPreferences(
    val onboardingComplete: Boolean = false,
    val guardEnabled: Boolean = false,
    val extraKeywords: Set<String> = emptySet(),
    val extraDomains: Set<String> = emptySet(),
    val goal: Goal = Goal(),
    val stats: DashboardStats = DashboardStats(),
    val languageTag: String = "",
    val plusSubscribed: Boolean = false,
    val plusTrialStartedAtMillis: Long? = null,
    val priceLineIndex: Int = 0,
    val playStoreRated: Boolean = false,
    val reviewPromptAtMillis: Long? = null,
    val updateRemindAtMillis: Long? = null,
)
