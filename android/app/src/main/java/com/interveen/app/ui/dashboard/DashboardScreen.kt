package com.interveen.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.interveen.app.R
import androidx.compose.material3.AlertDialog
import com.interveen.app.domain.model.GoalKind
import com.interveen.app.domain.plus.PlusAccess
import com.interveen.app.domain.plus.PlusPhase
import com.interveen.app.ui.components.CalmButton
import com.interveen.app.ui.components.PermissionDisclosureDialog
import com.interveen.app.ui.components.PermissionToggleRow
import com.interveen.app.ui.components.QuietCard
import com.interveen.app.ui.components.StatTile
import com.interveen.app.ui.components.StatusDot
import com.interveen.app.ui.theme.CoolGray
import com.interveen.app.ui.theme.CrispWhite
import com.interveen.app.ui.theme.Emerald
import com.interveen.app.ui.theme.QuietSlate
import com.interveen.app.ui.theme.SlateCharcoal
import com.interveen.app.ui.theme.SoftMist
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onToggleGuard: (Boolean) -> Unit,
    onOpenMediaSetup: () -> Unit,
    onOpenSettings: () -> Unit,
    onPreviewOverlay: () -> Unit,
    onRefresh: () -> Unit,
    onRequestAccessibility: () -> Unit,
    onRequestOverlay: () -> Unit,
    onOpenAppDetails: () -> Unit,
    onOpenPaywall: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) onRefresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val armed = state.guard.isFullyArmed
    var showAccessibilityDisclosure by remember { mutableStateOf(false) }
    var showOverlayDisclosure by remember { mutableStateOf(false) }
    var showRestrictedDisclosure by remember { mutableStateOf(false) }
    var showHoldOff by remember { mutableStateOf(false) }

    LaunchedEffect(state.plusPhase) {
        if (state.plusPhase == PlusPhase.Expired) onOpenPaywall()
    }

    if (showHoldOff) {
        GuardOffDialog(
            onConfirm = {
                showHoldOff = false
                onToggleGuard(false)
            },
            onKeep = { showHoldOff = false },
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
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.dashboard_brand),
                    style = MaterialTheme.typography.labelMedium,
                    color = QuietSlate,
                )
                Text(text = stringResource(R.string.dashboard_title), style = MaterialTheme.typography.displaySmall)
            }
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.dashboard_settings),
                    tint = SoftMist,
                )
            }
        }

        QuietCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusDot(active = armed)
                Spacer(Modifier.padding(start = 10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(
                            if (armed) R.string.dashboard_status_active else R.string.dashboard_status_inactive,
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        color = if (armed) Emerald else SoftMist,
                    )
                    Text(
                        text = stringResource(
                            if (armed) {
                                R.string.dashboard_status_active_body
                            } else {
                                R.string.dashboard_status_inactive_body
                            },
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftMist,
                    )
                }
            }
        }

        PermissionToggleRow(
            title = stringResource(R.string.dashboard_guard_title),
            body = stringResource(
                if (state.guard.enabled) R.string.dashboard_guard_on_body else R.string.dashboard_guard_off_body,
            ),
            enabled = state.guard.enabled,
            onClick = {
                when {
                    state.guard.enabled && state.plusPhase.grantsPlus() -> showHoldOff = true
                    state.guard.enabled -> onToggleGuard(false)
                    !state.guard.accessibilityGranted -> showAccessibilityDisclosure = true
                    !state.guard.overlayGranted -> showOverlayDisclosure = true
                    else -> onToggleGuard(true)
                }
            },
        )

        if (state.plusPhase == PlusPhase.Trial && !state.trialEndLabel.isNullOrBlank()) {
            QuietCard(modifier = Modifier.fillMaxWidth(), onClick = onOpenPaywall) {
                Text(
                    text = stringResource(R.string.dashboard_plus_trial, state.trialEndLabel),
                    style = MaterialTheme.typography.titleMedium,
                    color = Emerald,
                )
            }
        }
        if (state.plusPhase == PlusPhase.Expired) {
            QuietCard(modifier = Modifier.fillMaxWidth(), onClick = onOpenPaywall) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = stringResource(R.string.dashboard_plus_offer),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = stringResource(R.string.dashboard_plus_offer_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftMist,
                    )
                }
            }
        }

        if (state.guard.needsRestrictedSettings) {
            PermissionToggleRow(
                title = stringResource(R.string.onboarding_restricted_title),
                body = stringResource(R.string.onboarding_restricted_body),
                enabled = state.guard.restrictedSettingsAllowed,
                onClick = { showRestrictedDisclosure = true },
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                label = stringResource(R.string.dashboard_stat_times_saved),
                value = state.timesSaved.toString(),
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = stringResource(R.string.dashboard_stat_goal_streak),
                value = state.goalStreak.toString(),
                modifier = Modifier.weight(1f),
            )
        }

        QuietCard(modifier = Modifier.fillMaxWidth(), onClick = onOpenMediaSetup) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(R.string.dashboard_current_goal),
                    style = MaterialTheme.typography.labelMedium,
                    color = QuietSlate,
                )
                when {
                    state.goal.kind == GoalKind.VIDEO && !state.goal.mediaUri.isNullOrBlank() -> {
                        Text(
                            text = state.goal.title.ifBlank { stringResource(R.string.dashboard_video_reminder) },
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(text = stringResource(R.string.dashboard_video_reminder_body), color = SoftMist)
                    }
                    state.goal.kind == GoalKind.IMAGE && !state.goal.mediaUri.isNullOrBlank() -> {
                        AsyncImage(
                            model = state.goal.mediaUri,
                            contentDescription = state.goal.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentScale = ContentScale.Crop,
                        )
                        Text(
                            text = state.goal.title.ifBlank { stringResource(R.string.dashboard_still_reminder) },
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                    state.goal.kind == GoalKind.TEXT && state.goal.body.isNotBlank() -> {
                        Text(
                            text = state.goal.title.ifBlank { stringResource(R.string.dashboard_written_intention) },
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(text = state.goal.body, color = SoftMist)
                    }
                    else -> {
                        Text(
                            text = stringResource(R.string.dashboard_set_reminder),
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(text = stringResource(R.string.dashboard_set_reminder_body), color = SoftMist)
                    }
                }
            }
        }

        if (state.goal.isConfigured && state.guard.overlayGranted) {
            CalmButton(
                text = stringResource(R.string.dashboard_preview_overlay),
                onClick = onPreviewOverlay,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        CalmButton(
            text = stringResource(
                if (state.goal.isConfigured) R.string.dashboard_edit_goal else R.string.dashboard_create_goal,
            ),
            onClick = onOpenMediaSetup,
            modifier = Modifier.fillMaxWidth(),
        )

        if (!state.guard.accessibilityGranted || !state.guard.overlayGranted) {
            Text(
                text = stringResource(R.string.dashboard_needs_permissions),
                style = MaterialTheme.typography.bodyMedium,
                color = QuietSlate,
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

private fun PlusPhase.grantsPlus(): Boolean {
    return this == PlusPhase.Trial || this == PlusPhase.Subscribed
}

@Composable
private fun GuardOffDialog(
    onConfirm: () -> Unit,
    onKeep: () -> Unit,
) {
    var remaining by remember { mutableStateOf(PlusAccess.GUARD_OFF_HOLD_SECONDS) }
    LaunchedEffect(Unit) {
        while (remaining > 0) {
            delay(1_000)
            remaining -= 1
        }
    }
    AlertDialog(
        onDismissRequest = onKeep,
        containerColor = CoolGray,
        titleContentColor = CrispWhite,
        textContentColor = SoftMist,
        title = { Text(text = stringResource(R.string.guard_off_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = stringResource(R.string.guard_off_body))
                if (remaining > 0) {
                    Text(
                        text = stringResource(R.string.guard_off_wait, remaining),
                        color = Emerald,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = remaining == 0) {
                Text(text = stringResource(R.string.guard_off_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onKeep) {
                Text(text = stringResource(R.string.guard_off_keep))
            }
        },
    )
}
