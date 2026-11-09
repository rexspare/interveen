package com.interveen.app.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.interveen.app.domain.model.AppPreferences
import com.interveen.app.domain.store.StorePromptPolicy
import com.interveen.app.domain.model.DashboardStats
import com.interveen.app.domain.model.Goal
import com.interveen.app.domain.model.GoalKind
import com.interveen.app.domain.repository.PreferencesRepository
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class InterveenPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : PreferencesRepository {

    override val preferences: Flow<AppPreferences> = dataStore.data.map { prefs ->
        AppPreferences(
            onboardingComplete = prefs[Keys.ONBOARDING] ?: false,
            guardEnabled = prefs[Keys.GUARD_ENABLED] ?: false,
            extraKeywords = prefs[Keys.EXTRA_KEYWORDS].orEmpty(),
            extraDomains = prefs[Keys.EXTRA_DOMAINS].orEmpty(),
            goal = Goal(
                kind = prefs[Keys.GOAL_KIND]?.let { runCatching { GoalKind.valueOf(it) }.getOrNull() }
                    ?: GoalKind.UNSET,
                title = prefs[Keys.GOAL_TITLE].orEmpty(),
                body = prefs[Keys.GOAL_BODY].orEmpty(),
                mediaUri = prefs[Keys.GOAL_MEDIA],
            ),
            stats = DashboardStats(
                timesSaved = prefs[Keys.TIMES_SAVED] ?: 0,
                goalStreak = prefs[Keys.GOAL_STREAK] ?: 0,
                lastTriggerEpochDay = prefs[Keys.LAST_TRIGGER_DAY],
            ),
            languageTag = prefs[Keys.LANGUAGE_TAG].orEmpty(),
            plusSubscribed = prefs[Keys.PLUS_SUBSCRIBED] ?: false,
            plusTrialStartedAtMillis = prefs[Keys.PLUS_TRIAL_STARTED],
            priceLineIndex = prefs[Keys.PRICE_LINE_INDEX] ?: 0,
            playStoreRated = prefs[Keys.PLAY_STORE_RATED] ?: false,
            reviewPromptAtMillis = prefs[Keys.REVIEW_PROMPT_AT],
            updateRemindAtMillis = prefs[Keys.UPDATE_REMIND_AT],
        )
    }

    override suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[Keys.ONBOARDING] = complete }
    }

    override suspend fun setGuardEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.GUARD_ENABLED] = enabled }
    }

    override suspend fun setGoal(goal: Goal) {
        dataStore.edit { prefs ->
            prefs[Keys.GOAL_KIND] = goal.kind.name
            prefs[Keys.GOAL_TITLE] = goal.title
            prefs[Keys.GOAL_BODY] = goal.body
            if (goal.mediaUri.isNullOrBlank()) {
                prefs.remove(Keys.GOAL_MEDIA)
            } else {
                prefs[Keys.GOAL_MEDIA] = goal.mediaUri
            }
        }
    }

    override suspend fun recordSuccessfulInterruption() {
        val today = LocalDate.now(ZoneId.systemDefault()).toEpochDay()
        dataStore.edit { prefs ->
            val lastDay = prefs[Keys.LAST_TRIGGER_DAY]
            val currentStreak = prefs[Keys.GOAL_STREAK] ?: 0
            prefs[Keys.TIMES_SAVED] = (prefs[Keys.TIMES_SAVED] ?: 0) + 1
            prefs[Keys.GOAL_STREAK] = when {
                lastDay == null -> 1
                lastDay == today -> currentStreak.coerceAtLeast(1)
                lastDay == today - 1 -> currentStreak + 1
                else -> 1
            }
            prefs[Keys.LAST_TRIGGER_DAY] = today
        }
    }

    override suspend fun setExtraKeywords(keywords: Set<String>) {
        dataStore.edit { it[Keys.EXTRA_KEYWORDS] = keywords }
    }

    override suspend fun setExtraDomains(domains: Set<String>) {
        dataStore.edit { it[Keys.EXTRA_DOMAINS] = domains }
    }

    override suspend fun startPlusTrial(nowMillis: Long) {
        dataStore.edit { prefs ->
            if (prefs[Keys.PLUS_TRIAL_STARTED] == null) {
                prefs[Keys.PLUS_TRIAL_STARTED] = nowMillis
            }
        }
    }

    override suspend fun setPlusSubscribed(subscribed: Boolean) {
        dataStore.edit { it[Keys.PLUS_SUBSCRIBED] = subscribed }
    }

    override suspend fun advancePriceLine(): Int {
        var shown = 0
        dataStore.edit { prefs ->
            shown = prefs[Keys.PRICE_LINE_INDEX] ?: 0
            prefs[Keys.PRICE_LINE_INDEX] = shown + 1
        }
        return shown
    }

    override suspend fun noteAppOpened(nowMillis: Long) {
        dataStore.edit { prefs ->
            if (prefs[Keys.REVIEW_PROMPT_AT] == null) {
                prefs[Keys.REVIEW_PROMPT_AT] = StorePromptPolicy.initialReviewAt(nowMillis)
            }
        }
    }

    override suspend fun scheduleReviewPrompt(atMillis: Long) {
        dataStore.edit { it[Keys.REVIEW_PROMPT_AT] = atMillis }
    }

    override suspend fun snoozeUpdatePrompt(atMillis: Long) {
        dataStore.edit { it[Keys.UPDATE_REMIND_AT] = atMillis }
    }

    override suspend fun markPlayStoreRated() {
        dataStore.edit { it[Keys.PLAY_STORE_RATED] = true }
    }

    override suspend fun setLanguageTag(tag: String) {
        dataStore.edit { prefs ->
            if (tag.isBlank()) {
                prefs.remove(Keys.LANGUAGE_TAG)
            } else {
                prefs[Keys.LANGUAGE_TAG] = tag
            }
        }
    }

    private object Keys {
        val ONBOARDING = booleanPreferencesKey("onboarding_complete")
        val GUARD_ENABLED = booleanPreferencesKey("guard_enabled")
        val EXTRA_KEYWORDS = stringSetPreferencesKey("extra_keywords")
        val EXTRA_DOMAINS = stringSetPreferencesKey("extra_domains")
        val GOAL_KIND = stringPreferencesKey("goal_kind")
        val GOAL_TITLE = stringPreferencesKey("goal_title")
        val GOAL_BODY = stringPreferencesKey("goal_body")
        val GOAL_MEDIA = stringPreferencesKey("goal_media_uri")
        val TIMES_SAVED = intPreferencesKey("times_saved")
        val GOAL_STREAK = intPreferencesKey("goal_streak")
        val LAST_TRIGGER_DAY = longPreferencesKey("last_trigger_epoch_day")
        val LANGUAGE_TAG = stringPreferencesKey("language_tag")
        val PLUS_SUBSCRIBED = booleanPreferencesKey("plus_subscribed")
        val PLUS_TRIAL_STARTED = longPreferencesKey("plus_trial_started_at")
        val PRICE_LINE_INDEX = intPreferencesKey("price_line_index")
        val PLAY_STORE_RATED = booleanPreferencesKey("play_store_rated")
        val REVIEW_PROMPT_AT = longPreferencesKey("review_prompt_at")
        val UPDATE_REMIND_AT = longPreferencesKey("update_remind_at")
    }
}
