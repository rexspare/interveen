package com.interveen.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.interveen.app.R
import com.interveen.app.ui.theme.CoolGray
import com.interveen.app.ui.theme.CoolGrayRaised
import com.interveen.app.ui.theme.CrispWhite
import com.interveen.app.ui.theme.Emerald
import com.interveen.app.ui.theme.EmeraldSoft
import com.interveen.app.ui.theme.QuietSlate
import com.interveen.app.ui.theme.SoftMist

@Composable
fun HowToEnableDialog(
    needsRestrictedSettings: Boolean,
    onWatchVideo: () -> Unit,
    onDismiss: () -> Unit,
) {
    val steps = stringArrayResource(
        if (needsRestrictedSettings) {
            R.array.onboarding_how_to_steps_restricted
        } else {
            R.array.onboarding_how_to_steps
        },
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
        containerColor = CoolGray,
        titleContentColor = CrispWhite,
        textContentColor = SoftMist,
        title = { Text(text = stringResource(R.string.onboarding_how_to_title)) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Text(
                    text = stringResource(R.string.onboarding_how_to_intro),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftMist,
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    steps.forEachIndexed { index, step ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSoft),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Emerald,
                                )
                            }
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium,
                                color = CrispWhite,
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CoolGrayRaised)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_how_to_video_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = Emerald,
                    )
                    Text(
                        text = stringResource(R.string.onboarding_how_to_video_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftMist,
                    )
                    TextButton(onClick = onWatchVideo) {
                        Text(
                            text = stringResource(R.string.onboarding_how_to_watch_video),
                            color = Emerald,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.onboarding_how_to_got_it),
                    color = QuietSlate,
                )
            }
        },
    )
}
