package com.interveen.app.domain.usecase

import com.interveen.app.domain.model.MatchResult
import com.interveen.app.domain.plus.PlusAccess
import com.interveen.app.domain.repository.ContentMatcher
import com.interveen.app.domain.repository.PreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class MatchContentUseCase @Inject constructor(
    private val matcher: ContentMatcher,
    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke(rawInput: String): MatchResult {
        val prefs = preferencesRepository.preferences.first()
        val plus = PlusAccess.hasPlus(
            subscribed = prefs.plusSubscribed,
            trialStartedAtMillis = prefs.plusTrialStartedAtMillis,
            nowMillis = System.currentTimeMillis(),
        )
        if (!plus) return MatchResult.None
        return matcher.evaluate(
            rawInput = rawInput,
            extraKeywords = if (plus) prefs.extraKeywords else emptySet(),
            extraDomains = if (plus) prefs.extraDomains else emptySet(),
        )
    }
}

@Singleton
class RecordInterruptionUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke() {
        preferencesRepository.recordSuccessfulInterruption()
    }
}

@Singleton
class ToggleGuardUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke(enabled: Boolean) {
        preferencesRepository.setGuardEnabled(enabled)
    }
}
