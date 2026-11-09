package com.interveen.app.ui.dashboard

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interveen.app.core.overlay.OverlayManager
import com.interveen.app.core.permissions.PermissionMonitor
import com.interveen.app.core.permissions.SystemSettingsIntents
import com.interveen.app.core.service.GuardForegroundService
import com.interveen.app.data.billing.PlayBillingRepository
import com.interveen.app.domain.model.Goal
import com.interveen.app.domain.model.GuardState
import com.interveen.app.domain.plus.PlusAccess
import com.interveen.app.domain.plus.PlusPhase
import com.interveen.app.domain.repository.PreferencesRepository
import com.interveen.app.ui.paywall.formatPlusDate
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiState(
    val guard: GuardState = GuardState(),
    val timesSaved: Int = 0,
    val goalStreak: Int = 0,
    val goal: Goal = Goal(),
    val plusPhase: PlusPhase = PlusPhase.Inactive,
    val trialEndLabel: String? = null,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository,
    private val permissionMonitor: PermissionMonitor,
    private val overlayManager: OverlayManager,
    private val billing: PlayBillingRepository,
) : ViewModel() {

    private val permissionsTick = MutableStateFlow(0)

    val uiState: StateFlow<DashboardUiState> = combine(
        preferencesRepository.preferences,
        permissionsTick,
    ) { prefs, _ ->
        DashboardUiState(
            guard = GuardState(
                enabled = prefs.guardEnabled,
                accessibilityGranted = permissionMonitor.isAccessibilityEnabled(),
                overlayGranted = permissionMonitor.canDrawOverlays(),
                notificationsGranted = permissionMonitor.hasNotifications(),
                restrictedSettingsAllowed = permissionMonitor.areRestrictedSettingsAllowed(),
                needsRestrictedSettings = permissionMonitor.needsRestrictedSettings(),
            ),
            timesSaved = prefs.stats.timesSaved,
            goalStreak = prefs.stats.goalStreak,
            goal = prefs.goal,
            plusPhase = PlusAccess.phase(
                subscribed = prefs.plusSubscribed,
                trialStartedAtMillis = prefs.plusTrialStartedAtMillis,
                nowMillis = System.currentTimeMillis(),
            ),
            trialEndLabel = PlusAccess.trialEndsAtMillis(prefs.plusTrialStartedAtMillis)
                ?.let(::formatPlusDate),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    init {
        billing.refresh()
        viewModelScope.launch { maybeStartTrial() }
    }

    fun refreshPermissions() {
        permissionsTick.value += 1
        viewModelScope.launch { maybeStartTrial() }
    }

    private suspend fun maybeStartTrial() {
        val prefs = preferencesRepository.preferences.first()
        if (PlusAccess.shouldStartTrial(prefs.plusTrialStartedAtMillis)) {
            preferencesRepository.startPlusTrial(System.currentTimeMillis())
        }
    }

    fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun openAppDetailsSettings() {
        SystemSettingsIntents.openAppsList(context)
    }

    fun openOverlaySettings() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}"),
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun previewOverlay() {
        overlayManager.show()
    }

    fun setGuardEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setGuardEnabled(enabled)
            if (enabled) {
                GuardForegroundService.start(context)
            } else {
                GuardForegroundService.stop(context)
            }
        }
    }
}
