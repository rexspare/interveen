package com.interveen.app.domain.plus

object PlusAccess {
    const val TRIAL_MILLIS: Long = 7L * 24 * 60 * 60 * 1000
    const val GUARD_OFF_HOLD_SECONDS: Int = 60

    fun hasPlus(subscribed: Boolean, trialStartedAtMillis: Long?, nowMillis: Long): Boolean {
        return when (phase(subscribed, trialStartedAtMillis, nowMillis)) {
            PlusPhase.Subscribed, PlusPhase.Trial -> true
            PlusPhase.Inactive, PlusPhase.Expired -> false
        }
    }

    fun phase(subscribed: Boolean, trialStartedAtMillis: Long?, nowMillis: Long): PlusPhase {
        if (subscribed) return PlusPhase.Subscribed
        val started = trialStartedAtMillis ?: return PlusPhase.Inactive
        return if (nowMillis - started < TRIAL_MILLIS) PlusPhase.Trial else PlusPhase.Expired
    }

    fun trialEndsAtMillis(trialStartedAtMillis: Long?): Long? {
        return trialStartedAtMillis?.plus(TRIAL_MILLIS)
    }

    fun shouldStartTrial(trialStartedAtMillis: Long?): Boolean {
        return trialStartedAtMillis == null
    }
}

enum class PlusPhase {
    Inactive,
    Trial,
    Expired,
    Subscribed,
}
