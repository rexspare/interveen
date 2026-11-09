package com.interveen.app.ui.store

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.interveen.app.R
import com.interveen.app.core.play.PlayStore
import com.interveen.app.ui.theme.CoolGray
import com.interveen.app.ui.theme.CrispWhite
import com.interveen.app.ui.theme.Emerald
import com.interveen.app.ui.theme.SoftMist

@Composable
fun StorePromptHost(viewModel: StorePromptViewModel = hiltViewModel()) {
    val prompt by viewModel.prompt.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val updateLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
    ) { }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.onResume()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            viewModel.onResume()
        }
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    when (prompt) {
        StorePrompt.None -> Unit
        StorePrompt.Update -> ChoiceDialog(
            title = stringResource(R.string.update_title),
            body = stringResource(R.string.update_body),
            confirm = stringResource(R.string.update_action),
            onConfirm = {
                val started = viewModel.beginUpdate(updateLauncher)
                if (!started) PlayStore.openListing(context)
            },
            onLater = viewModel::remindLater,
        )
        StorePrompt.Restart -> ChoiceDialog(
            title = stringResource(R.string.update_restart_title),
            body = stringResource(R.string.update_restart_body),
            confirm = stringResource(R.string.update_restart_action),
            onConfirm = viewModel::restartUpdate,
            onLater = viewModel::remindLater,
        )
        StorePrompt.Review -> ChoiceDialog(
            title = stringResource(R.string.review_title),
            body = stringResource(R.string.review_body),
            confirm = stringResource(R.string.review_action),
            onConfirm = {
                viewModel.markRated()
                PlayStore.openListing(context)
            },
            onLater = viewModel::remindLater,
            onDismiss = viewModel::dismissReview,
        )
    }
}

@Composable
private fun ChoiceDialog(
    title: String,
    body: String,
    confirm: String,
    onConfirm: () -> Unit,
    onLater: () -> Unit,
    onDismiss: () -> Unit = onLater,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CoolGray,
        titleContentColor = CrispWhite,
        textContentColor = SoftMist,
        title = { Text(text = title) },
        text = {
            Column {
                Text(text = body)
                Spacer(Modifier.height(4.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirm, color = Emerald)
            }
        },
        dismissButton = {
            TextButton(onClick = onLater) {
                Text(text = stringResource(R.string.prompt_later))
            }
        },
    )
}
