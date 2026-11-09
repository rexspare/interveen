package com.interveen.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.interveen.app.core.play.PlayStore
import com.interveen.app.ui.store.StorePromptHost
import com.interveen.app.domain.plus.PlusPhase
import com.interveen.app.ui.dashboard.DashboardScreen
import com.interveen.app.ui.dashboard.DashboardViewModel
import com.interveen.app.ui.media.MediaSetupScreen
import com.interveen.app.ui.media.MediaSetupViewModel
import com.interveen.app.ui.onboarding.OnboardingScreen
import com.interveen.app.ui.onboarding.OnboardingViewModel
import com.interveen.app.ui.paywall.PaywallScreen
import com.interveen.app.ui.paywall.PaywallViewModel
import com.interveen.app.ui.settings.LegalDocument
import com.interveen.app.ui.settings.LegalDocumentScreen
import com.interveen.app.ui.settings.SettingsScreen
import com.interveen.app.ui.settings.SettingsViewModel

object Routes {
    const val ONBOARDING = "onboarding"
    const val DASHBOARD = "dashboard"
    const val MEDIA = "media"
    const val SETTINGS = "settings"
    const val PAYWALL = "paywall"
    const val WEBSITE = "legal/website"
    const val PRIVACY = "legal/privacy"
    const val TERMS = "legal/terms"
}

@Composable
fun InterveenNavHost(onboardingComplete: Boolean, subscriptionRequired: Boolean) {
    val navController = rememberNavController()
    val start = when {
        subscriptionRequired -> Routes.PAYWALL
        onboardingComplete -> Routes.DASHBOARD
        else -> Routes.ONBOARDING
    }

    Box(modifier = Modifier.fillMaxSize()) {
    NavHost(navController = navController, startDestination = start) {
        composable(Routes.ONBOARDING) {
            val viewModel: OnboardingViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            OnboardingScreen(
                state = state,
                onRefreshPermissions = viewModel::refreshPermissions,
                onRequestAccessibility = viewModel::openAccessibilitySettings,
                onRequestOverlay = viewModel::openOverlaySettings,
                onRequestNotifications = viewModel::requestNotifications,
                onOpenAppDetails = viewModel::openAppDetailsSettings,
                onOpenSetupVideo = viewModel::openSetupVideo,
                onContinue = {
                    viewModel.completeOnboarding()
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.DASHBOARD) {
            val viewModel: DashboardViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            DashboardScreen(
                state = state,
                onToggleGuard = viewModel::setGuardEnabled,
                onOpenMediaSetup = { navController.navigate(Routes.MEDIA) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onPreviewOverlay = viewModel::previewOverlay,
                onRefresh = viewModel::refreshPermissions,
                onRequestAccessibility = viewModel::openAccessibilitySettings,
                onRequestOverlay = viewModel::openOverlaySettings,
                onOpenAppDetails = viewModel::openAppDetailsSettings,
                onOpenPaywall = { navController.navigate(Routes.PAYWALL) },
            )
        }
        composable(Routes.MEDIA) {
            val viewModel: MediaSetupViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            MediaSetupScreen(
                state = state,
                onTitleChange = viewModel::onTitleChange,
                onBodyChange = viewModel::onBodyChange,
                onVideoPicked = viewModel::onVideoPicked,
                onImagePicked = viewModel::onImagePicked,
                onSaveTextGoal = viewModel::saveTextGoal,
                onBack = { navController.popBackStack() },
                createCaptureUri = viewModel::createCaptureUri,
            )
        }
        composable(Routes.SETTINGS) {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val context = LocalContext.current
            SettingsScreen(
                state = state,
                onLanguageSelected = viewModel::setLanguage,
                onOpenWebsite = { navController.navigate(Routes.WEBSITE) },
                onOpenPrivacy = { navController.navigate(Routes.PRIVACY) },
                onOpenTerms = { navController.navigate(Routes.TERMS) },
                onOpenPaywall = { navController.navigate(Routes.PAYWALL) },
                onAddDomain = viewModel::addDomain,
                onAddKeyword = viewModel::addKeyword,
                onRemoveDomain = viewModel::removeDomain,
                onRemoveKeyword = viewModel::removeKeyword,
                onRestoreSubscription = viewModel::restoreSubscription,
                onRate = {
                    viewModel.markRated()
                    PlayStore.openListing(context)
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.PAYWALL) {
            val viewModel: PaywallViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(state.phase) {
                if (state.phase == PlusPhase.Subscribed) {
                    val returned = navController.popBackStack()
                    if (!returned) {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.PAYWALL) { inclusive = true }
                        }
                    }
                }
            }
            PaywallScreen(
                state = state,
                onSelectPlan = viewModel::select,
                onPurchase = viewModel::purchase,
                onRestore = viewModel::restore,
                onClose = { navController.popBackStack() },
            )
        }
        composable(Routes.WEBSITE) {
            LegalDocumentScreen(
                document = LegalDocument.HOME,
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.PRIVACY) {
            LegalDocumentScreen(
                document = LegalDocument.PRIVACY,
                onBack = { navController.popBackStack() },
            )
        }
        composable(Routes.TERMS) {
            LegalDocumentScreen(
                document = LegalDocument.TERMS,
                onBack = { navController.popBackStack() },
            )
        }
    }
        if (onboardingComplete && !subscriptionRequired) {
            StorePromptHost()
        }
    }
}
