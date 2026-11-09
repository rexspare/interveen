package com.interveen.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interveen.app.core.locale.LocaleController
import com.interveen.app.data.billing.PlayBillingRepository
import com.interveen.app.data.billing.PlusBillingEvent
import com.interveen.app.domain.plus.ListEntries
import com.interveen.app.domain.plus.PlusAccess
import com.interveen.app.domain.plus.PlusPhase
import com.interveen.app.domain.repository.PreferencesRepository
import com.interveen.app.ui.paywall.formatPlusDate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val languageTag: String = "",
    val plusPhase: PlusPhase = PlusPhase.Inactive,
    val trialEndLabel: String? = null,
    val domains: List<String> = emptyList(),
    val keywords: List<String> = emptyList(),
    val restoreNotice: PlusBillingEvent? = null,
    val playStoreRated: Boolean = false,
) {
    val hasPlus: Boolean
        get() = plusPhase == PlusPhase.Trial || plusPhase == PlusPhase.Subscribed
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val billing: PlayBillingRepository,
) : ViewModel() {

    private val notice = MutableStateFlow<PlusBillingEvent?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesRepository.preferences,
        notice,
    ) { prefs, currentNotice ->
        val now = System.currentTimeMillis()
        val phase = PlusAccess.phase(prefs.plusSubscribed, prefs.plusTrialStartedAtMillis, now)
        SettingsUiState(
            languageTag = LocaleController.currentTag().ifBlank { prefs.languageTag },
            plusPhase = phase,
            trialEndLabel = PlusAccess.trialEndsAtMillis(prefs.plusTrialStartedAtMillis)
                ?.let(::formatPlusDate),
            domains = prefs.extraDomains.sorted(),
            keywords = prefs.extraKeywords.sorted(),
            restoreNotice = currentNotice,
            playStoreRated = prefs.playStoreRated,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    init {
        viewModelScope.launch {
            billing.events.collect { event ->
                if (event != PlusBillingEvent.Purchased) notice.value = event
            }
        }
    }

    fun markRated() {
        viewModelScope.launch { preferencesRepository.markPlayStoreRated() }
    }

    fun restoreSubscription() {
        notice.value = null
        billing.restore()
    }

    fun setLanguage(tag: String) {
        viewModelScope.launch {
            preferencesRepository.setLanguageTag(tag)
            LocaleController.apply(tag)
        }
    }

    fun addDomain(raw: String): Boolean {
        val domain = ListEntries.domain(raw) ?: return false
        if (!uiState.value.hasPlus) return false
        viewModelScope.launch {
            val current = preferencesRepository.preferences.first().extraDomains
            preferencesRepository.setExtraDomains(current + domain)
        }
        return true
    }

    fun addKeyword(raw: String): Boolean {
        val keyword = ListEntries.keyword(raw) ?: return false
        if (!uiState.value.hasPlus) return false
        viewModelScope.launch {
            val current = preferencesRepository.preferences.first().extraKeywords
            preferencesRepository.setExtraKeywords(current + keyword)
        }
        return true
    }

    fun removeDomain(domain: String) {
        viewModelScope.launch {
            val current = preferencesRepository.preferences.first().extraDomains
            preferencesRepository.setExtraDomains(current - domain)
        }
    }

    fun removeKeyword(keyword: String) {
        viewModelScope.launch {
            val current = preferencesRepository.preferences.first().extraKeywords
            preferencesRepository.setExtraKeywords(current - keyword)
        }
    }
}
