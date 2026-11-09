package com.interveen.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun PermissionDisclosureDialog(
    title: String,
    body: String,
    onAgree: () -> Unit,
    onNotNow: () -> Unit,
    highlightTitle: String? = null,
    highlightBody: String? = null,
    highlightActionLabel: String? = null,
    onHighlightAction: (() -> Unit)? = null,
    confirmLabel: String? = null,
) {
    AlertDialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
        containerColor = CoolGray,
        titleContentColor = CrispWhite,
        textContentColor = SoftMist,
        title = { Text(text = title) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                if (!highlightTitle.isNullOrBlank() && !highlightBody.isNullOrBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CoolGrayRaised)
                            .background(EmeraldSoft)
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = highlightTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = Emerald,
                        )
                        Text(
                            text = highlightBody,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CrispWhite,
                        )
                        if (!highlightActionLabel.isNullOrBlank() && onHighlightAction != null) {
                            TextButton(onClick = onHighlightAction) {
                                Text(text = highlightActionLabel, color = Emerald)
                            }
                        }
                    }
                }
                Text(text = body)
            }
        },
        confirmButton = {
            TextButton(onClick = onAgree) {
                Text(
                    text = confirmLabel ?: stringResource(R.string.accessibility_disclosure_agree),
                    color = Emerald,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onNotNow) {
                Text(
                    text = stringResource(R.string.accessibility_disclosure_not_now),
                    color = QuietSlate,
                )
            }
        },
    )
}
