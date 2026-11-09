package com.interveen.app

import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.interveen.app.core.locale.LocaleController
import com.interveen.app.domain.model.AppPreferences
import com.interveen.app.domain.plus.PlusAccess
import com.interveen.app.domain.plus.PlusPhase
import com.interveen.app.domain.repository.PreferencesRepository
import com.interveen.app.ui.navigation.InterveenNavHost
import com.interveen.app.ui.theme.InterveenTheme
import com.interveen.app.ui.theme.SlateCharcoal
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        var keepSystemSplash = true
        splash.setKeepOnScreenCondition { keepSystemSplash }
        splash.setOnExitAnimationListener { provider -> provider.remove() }
        super.onCreate(savedInstanceState)

        var preferences by mutableStateOf<AppPreferences?>(null)
        lifecycleScope.launch {
            val started = SystemClock.elapsedRealtime()
            val first = preferencesRepository.preferences.first()
            if (PlusAccess.shouldStartTrial(first.plusTrialStartedAtMillis)) {
                preferencesRepository.startPlusTrial(System.currentTimeMillis())
            }
            if (first.languageTag.isNotBlank()) {
                LocaleController.apply(first.languageTag)
            }
            preferences = first
            val remaining = 1_050L - (SystemClock.elapsedRealtime() - started)
            if (remaining > 0) delay(remaining)
            keepSystemSplash = false
            preferencesRepository.preferences.collect { preferences = it }
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        setContent {
            val current = preferences
            InterveenTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = SlateCharcoal) {
                    if (current != null) {
                        val subscriptionRequired = PlusAccess.phase(
                            subscribed = current.plusSubscribed,
                            trialStartedAtMillis = current.plusTrialStartedAtMillis,
                            nowMillis = System.currentTimeMillis(),
                        ) == PlusPhase.Expired
                        InterveenNavHost(
                            onboardingComplete = current.onboardingComplete,
                            subscriptionRequired = subscriptionRequired,
                        )
                    }
                }
            }
        }
    }
}
