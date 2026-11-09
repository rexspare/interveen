package com.interveen.app.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.interveen.app.R
import com.interveen.app.ui.components.CalmButton
import com.interveen.app.ui.components.HowToEnableDialog
import com.interveen.app.ui.components.PermissionDisclosureDialog
import com.interveen.app.ui.components.PermissionToggleRow
import com.interveen.app.ui.theme.Emerald
import com.interveen.app.ui.theme.QuietSlate
import com.interveen.app.ui.theme.SlateCharcoal
import com.interveen.app.ui.theme.SoftMist
import kotlinx.coroutines.launch

private const val ONBOARDING_PAGES = 3

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    onRefreshPermissions: () -> Unit,
    onRequestAccessibility: () -> Unit,
    onRequestOverlay: () -> Unit,
    onRequestNotifications: (() -> Unit) -> Unit,
    onOpenAppDetails: () -> Unit,
    onOpenSetupVideo: () -> Unit,
    onContinue: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) onRefreshPermissions()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { onRefreshPermissions() }
    val pagerState = rememberPagerState(pageCount = { ONBOARDING_PAGES })
    val scope = rememberCoroutineScope()
    val lastPage = pagerState.currentPage == ONBOARDING_PAGES - 1
    var showAccessibilityDisclosure by remember { mutableStateOf(false) }
    var showOverlayDisclosure by remember { mutableStateOf(false) }
    var showRestrictedDisclosure by remember { mutableStateOf(false) }
    var showHowTo by remember { mutableStateOf(false) }

    if (showHowTo) {
        HowToEnableDialog(
            needsRestrictedSettings = state.needsRestrictedSettings,
            onWatchVideo = onOpenSetupVideo,
            onDismiss = { showHowTo = false },
        )
    }
    if (showRestrictedDisclosure) {
        PermissionDisclosureDialog(
            title = stringResource(R.string.accessibility_restricted_title),
            body = stringResource(R.string.accessibility_restricted_body),
            highlightTitle = stringResource(R.string.accessibility_restricted_highlight_title),
            highlightBody = stringResource(R.string.accessibility_restricted_highlight_body),
            highlightActionLabel = stringResource(R.string.accessibility_restricted_open_settings),
            onHighlightAction = onOpenAppDetails,
            confirmLabel = stringResource(R.string.accessibility_restricted_open_accessibility),
            onAgree = {
                showRestrictedDisclosure = false
                onRequestAccessibility()
            },
            onNotNow = { showRestrictedDisclosure = false },
        )
    }
    if (showAccessibilityDisclosure) {
        PermissionDisclosureDialog(
            title = stringResource(R.string.accessibility_disclosure_title),
            body = stringResource(R.string.accessibility_disclosure_body),
            onAgree = {
                showAccessibilityDisclosure = false
                onRequestAccessibility()
            },
            onNotNow = { showAccessibilityDisclosure = false },
        )
    }
    if (showOverlayDisclosure) {
        PermissionDisclosureDialog(
            title = stringResource(R.string.overlay_disclosure_title),
            body = stringResource(R.string.overlay_disclosure_body),
            onAgree = {
                showOverlayDisclosure = false
                onRequestOverlay()
            },
            onNotNow = { showOverlayDisclosure = false },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateCharcoal)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
    ) {
        Text(
            text = stringResource(
                R.string.onboarding_step_of,
                pagerState.currentPage + 1,
                ONBOARDING_PAGES,
            ),
            style = MaterialTheme.typography.labelMedium,
            color = QuietSlate,
        )
        Spacer(Modifier.height(16.dp))
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = true,
        ) { page ->
            when (page) {
                0 -> IntroPage(
                    page = 0,
                    title = stringResource(R.string.onboarding_welcome_title),
                    body = stringResource(R.string.onboarding_welcome_body),
                )
                1 -> IntroPage(
                    page = 1,
                    title = stringResource(R.string.onboarding_how_title),
                    body = stringResource(R.string.onboarding_how_body),
                )
                else -> PermissionsPage(
                    state = state,
                    onRequestRestrictedSettings = {
                        showRestrictedDisclosure = true
                    },
                    onRequestAccessibility = {
                        if (state.accessibilityGranted) {
                            onRequestAccessibility()
                        } else {
                            showAccessibilityDisclosure = true
                        }
                    },
                    onRequestOverlay = {
                        if (state.overlayGranted) {
                            onRequestOverlay()
                        } else {
                            showOverlayDisclosure = true
                        }
                    },
                    onRequestNotifications = {
                        onRequestNotifications {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    },
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(ONBOARDING_PAGES) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (pagerState.currentPage == index) Emerald else QuietSlate),
                )
            }
        }
        CalmButton(
            text = when {
                !lastPage -> stringResource(R.string.onboarding_next)
                state.canContinue -> stringResource(R.string.onboarding_begin)
                else -> stringResource(R.string.onboarding_how_to)
            },
            onClick = {
                when {
                    !lastPage -> scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                    state.canContinue -> onContinue()
                    else -> showHowTo = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun IntroPage(page: Int, title: String, body: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = title, style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(12.dp))
        Text(text = body, style = MaterialTheme.typography.bodyLarge, color = SoftMist)
        Spacer(Modifier.height(20.dp))
        OnboardingGraphic(
            page = page,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun PermissionsPage(
    state: OnboardingUiState,
    onRequestRestrictedSettings: () -> Unit,
    onRequestAccessibility: () -> Unit,
    onRequestOverlay: () -> Unit,
    onRequestNotifications: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.onboarding_permissions_title),
            style = MaterialTheme.typography.displaySmall,
        )
        Text(
            text = stringResource(
                if (state.needsRestrictedSettings) {
                    R.string.onboarding_permissions_body_restricted
                } else {
                    R.string.onboarding_permissions_body
                },
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = SoftMist,
        )
        if (state.needsRestrictedSettings) {
            PermissionToggleRow(
                title = stringResource(R.string.onboarding_restricted_title),
                body = stringResource(R.string.onboarding_restricted_body),
                enabled = state.restrictedSettingsAllowed,
                onClick = onRequestRestrictedSettings,
            )
        }
        PermissionToggleRow(
            title = stringResource(R.string.onboarding_accessibility_title),
            body = stringResource(R.string.onboarding_accessibility_body),
            enabled = state.accessibilityGranted,
            onClick = onRequestAccessibility,
        )
        PermissionToggleRow(
            title = stringResource(R.string.onboarding_overlay_title),
            body = stringResource(R.string.onboarding_overlay_body),
            enabled = state.overlayGranted,
            onClick = onRequestOverlay,
        )
        PermissionToggleRow(
            title = stringResource(R.string.onboarding_notification_title),
            body = stringResource(R.string.onboarding_notification_body),
            enabled = state.notificationsGranted,
            onClick = onRequestNotifications,
        )
        Text(
            text = stringResource(R.string.onboarding_settings_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = QuietSlate,
        )
    }
}
