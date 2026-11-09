package com.interveen.app.core.overlay

import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.interveen.app.core.service.NotificationHelper
import com.interveen.app.domain.model.Goal
import com.interveen.app.domain.repository.PreferencesRepository
import com.interveen.app.ui.overlay.OverlayScreen
import com.interveen.app.ui.theme.InterveenTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@AndroidEntryPoint
class OverlayService : LifecycleService(), SavedStateRegistryOwner, ViewModelStoreOwner {

    @Inject
    lateinit var overlayManager: OverlayManager

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()
    private var windowController: OverlayWindowController? = null
    private var composeView: ComposeView? = null

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override val viewModelStore: ViewModelStore
        get() = store

    private val goalState by lazy {
        preferencesRepository.preferences
            .map { it.goal }
            .stateIn(lifecycleScope, SharingStarted.Eagerly, Goal())
    }

    override fun onCreate() {
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(Bundle())
        super.onCreate()
        promoteToForeground()
        windowController = OverlayWindowController(getSystemService(WINDOW_SERVICE) as WindowManager)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        promoteToForeground()
        when (intent?.action) {
            ACTION_HIDE -> dismiss()
            else -> showOverlay()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        teardown()
        store.clear()
        super.onDestroy()
    }

    private fun showOverlay() {
        if (composeView != null) return
        val view = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@OverlayService)
            setViewTreeViewModelStoreOwner(this@OverlayService)
            setViewTreeSavedStateRegistryOwner(this@OverlayService)
            setContent {
                val goal by goalState.collectAsStateWithLifecycle()
                InterveenTheme {
                    OverlayScreen(
                        goal = goal,
                        onRemembered = { dismiss() },
                    )
                }
            }
        }
        composeView = view
        windowController?.attach(view)
    }

    private fun dismiss() {
        overlayManager.markDismissed()
        teardown()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun teardown() {
        composeView?.disposeComposition()
        windowController?.detach()
        composeView = null
    }

    private fun promoteToForeground() {
        val notification = notificationHelper.buildGuardNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NotificationHelper.OVERLAY_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(NotificationHelper.OVERLAY_NOTIFICATION_ID, notification)
        }
    }

    companion object {
        const val ACTION_SHOW = "com.interveen.app.action.SHOW_OVERLAY"
        const val ACTION_HIDE = "com.interveen.app.action.HIDE_OVERLAY"
    }
}
