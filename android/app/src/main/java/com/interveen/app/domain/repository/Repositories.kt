package com.interveen.app.domain.repository

import com.interveen.app.domain.model.AppPreferences
import com.interveen.app.domain.model.Goal
import com.interveen.app.domain.model.MatchResult
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val preferences: Flow<AppPreferences>
    suspend fun setOnboardingComplete(complete: Boolean)
    suspend fun setGuardEnabled(enabled: Boolean)
    suspend fun setGoal(goal: Goal)
    suspend fun recordSuccessfulInterruption()
    suspend fun setExtraKeywords(keywords: Set<String>)
    suspend fun setExtraDomains(domains: Set<String>)
    suspend fun setLanguageTag(tag: String)
    suspend fun startPlusTrial(nowMillis: Long)
    suspend fun setPlusSubscribed(subscribed: Boolean)
    suspend fun advancePriceLine(): Int
    suspend fun noteAppOpened(nowMillis: Long)
    suspend fun scheduleReviewPrompt(atMillis: Long)
    suspend fun snoozeUpdatePrompt(atMillis: Long)
    suspend fun markPlayStoreRated()
}

interface ContentMatcher {
    fun evaluate(
        rawInput: String,
        extraKeywords: Set<String> = emptySet(),
        extraDomains: Set<String> = emptySet(),
    ): MatchResult
}
