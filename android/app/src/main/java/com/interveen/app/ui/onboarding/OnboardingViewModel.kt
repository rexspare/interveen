package com.interveen.app.ui.onboarding

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interveen.app.core.SiteLinks
import com.interveen.app.core.permissions.PermissionMonitor
import com.interveen.app.core.permissions.SystemSettingsIntents
import com.interveen.app.core.service.GuardForegroundService
import com.interveen.app.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val accessibilityGranted: Boolean = false,
    val overlayGranted: Boolean = false,
    val notificationsGranted: Boolean = true,
    val restrictedSettingsAllowed: Boolean = true,
    val needsRestrictedSettings: Boolean = false,
) {
    val canContinue: Boolean
        get() = accessibilityGranted && overlayGranted
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionMonitor: PermissionMonitor,
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        refreshPermissions()
    }

    fun refreshPermissions() {
        _uiState.update {
            OnboardingUiState(
                accessibilityGranted = permissionMonitor.isAccessibilityEnabled(),
                overlayGranted = permissionMonitor.canDrawOverlays(),
                notificationsGranted = permissionMonitor.hasNotifications(),
                restrictedSettingsAllowed = permissionMonitor.areRestrictedSettingsAllowed(),
                needsRestrictedSettings = permissionMonitor.needsRestrictedSettings(),
            )
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

    fun openSetupVideo() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SiteLinks.SETUP_VIDEO)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addCategory(Intent.CATEGORY_BROWSABLE)
        }
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // No browser available; the how-to steps remain on screen.
        }
    }

    fun requestNotifications(launcher: () -> Unit) {
        if (_uiState.value.notificationsGranted) return
        launcher()
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesRepository.setOnboardingComplete(true)
            preferencesRepository.setGuardEnabled(true)
            GuardForegroundService.start(context)
        }
    }
}
