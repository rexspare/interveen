package com.interveen.app.ui.store

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interveen.app.data.play.PlayUpdateRepository
import com.interveen.app.data.play.UpdateOffer
import com.interveen.app.domain.repository.PreferencesRepository
import com.interveen.app.domain.store.StorePromptPolicy
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class StorePrompt {
    None,
    Update,
    Restart,
    Review,
}

@HiltViewModel
class StorePromptViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val updates: PlayUpdateRepository,
) : ViewModel() {

    private val latched = MutableStateFlow(StorePrompt.None)
    private val skipUntilNextProcess = MutableStateFlow(false)

    val prompt: StateFlow<StorePrompt> = combine(
        preferencesRepository.preferences,
        latched,
        skipUntilNextProcess,
    ) { prefs, current, skip ->
        when {
            skip -> StorePrompt.None
            current == StorePrompt.Review && prefs.playStoreRated -> StorePrompt.None
            else -> current
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StorePrompt.None)

    fun onResume() {
        if (skipUntilNextProcess.value) return
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            preferencesRepository.noteAppOpened(now)
            val prefs = preferencesRepository.preferences.first()
            if (latched.value == StorePrompt.Review && prefs.playStoreRated) {
                latched.value = StorePrompt.None
            }
            if (latched.value != StorePrompt.None) return@launch
            val offer = updates.check()
            if (StorePromptPolicy.updateDue(offer != UpdateOffer.None, prefs.updateRemindAtMillis, now)) {
                latched.value = if (offer == UpdateOffer.ReadyToRestart) StorePrompt.Restart else StorePrompt.Update
                return@launch
            }
            if (StorePromptPolicy.reviewDue(prefs.playStoreRated, prefs.reviewPromptAtMillis, now)) {
                preferencesRepository.scheduleReviewPrompt(StorePromptPolicy.afterReviewShown(now))
                latched.value = StorePrompt.Review
            }
        }
    }

    fun remindLater() {
        val current = latched.value
        latched.value = StorePrompt.None
        val at = StorePromptPolicy.afterRemindLater(System.currentTimeMillis())
        viewModelScope.launch {
            when (current) {
                StorePrompt.Update, StorePrompt.Restart -> preferencesRepository.snoozeUpdatePrompt(at)
                StorePrompt.Review -> preferencesRepository.scheduleReviewPrompt(at)
                StorePrompt.None -> Unit
            }
        }
    }

    fun dismissReview() {
        latched.value = StorePrompt.None
    }

    fun markRated() {
        latched.value = StorePrompt.None
        viewModelScope.launch { preferencesRepository.markPlayStoreRated() }
    }

    fun beginUpdate(launcher: ActivityResultLauncher<IntentSenderRequest>): Boolean {
        skipUntilNextProcess.value = true
        latched.value = StorePrompt.None
        return updates.start(launcher)
    }

    fun restartUpdate() {
        skipUntilNextProcess.value = true
        latched.value = StorePrompt.None
        updates.complete()
    }
}
