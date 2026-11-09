package com.interveen.app.core.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.interveen.app.core.permissions.PermissionMonitor
import com.interveen.app.domain.repository.PreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var permissionMonitor: PermissionMonitor

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        scope.launch {
            try {
                val prefs = preferencesRepository.preferences.first()
                if (prefs.guardEnabled && permissionMonitor.isAccessibilityEnabled()) {
                    GuardForegroundService.start(context)
                }
            } finally {
                pending.finish()
            }
        }
    }
}
