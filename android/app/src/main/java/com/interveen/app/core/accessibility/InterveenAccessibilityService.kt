package com.interveen.app.core.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.interveen.app.core.overlay.OverlayManager
import com.interveen.app.domain.repository.PreferencesRepository
import com.interveen.app.domain.usecase.MatchContentUseCase
import com.interveen.app.domain.usecase.RecordInterruptionUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InterveenAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var overlayManager: OverlayManager

    @Inject
    lateinit var matchContent: MatchContentUseCase

    @Inject
    lateinit var recordInterruption: RecordInterruptionUseCase

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(serviceJob + Dispatchers.Default)
    private var guardEnabled = false
    private var lastTriggerAt = 0L
    private var lastEvidence = ""
    private var pendingMatch: Job? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        BrowserPackages.refresh(this)
        serviceScope.launch {
            preferencesRepository.preferences.collectLatest { prefs ->
                guardEnabled = prefs.guardEnabled
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || !guardEnabled || overlayManager.isShowing) return
        if (event.eventType !in WATCHED_EVENTS) return

        val packageName = event.packageName?.toString().orEmpty()
        if (!BrowserPackages.isMonitored(this, packageName)) return

        val snapshot = NodeTextExtractor.extract(event, rootInActiveWindow)
        if (snapshot.length < 3) return

        pendingMatch?.cancel()
        pendingMatch = serviceScope.launch {
            val result = matchContent(snapshot)
            if (!result.matched) return@launch

            val now = System.currentTimeMillis()
            val sameEvidence = result.evidence == lastEvidence && now - lastTriggerAt < COOLDOWN_MS
            if (sameEvidence || now - lastTriggerAt < MIN_GAP_MS) return@launch

            lastTriggerAt = now
            lastEvidence = result.evidence
            overlayManager.collapseBrowser()
            overlayManager.show()
            recordInterruption()
        }
    }

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        pendingMatch?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val COOLDOWN_MS = 12_000L
        private const val MIN_GAP_MS = 4_000L
        private val WATCHED_EVENTS = setOf(
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED,
        )
    }
}
