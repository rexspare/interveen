package com.interveen.app.ui.media

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.interveen.app.R
import com.interveen.app.domain.model.GoalKind
import com.interveen.app.ui.components.CalmButton
import com.interveen.app.ui.components.CalmButtonStyle
import com.interveen.app.ui.components.QuietCard
import com.interveen.app.ui.theme.CardCorner
import com.interveen.app.ui.theme.AccentInk
import com.interveen.app.ui.theme.CoolGray
import com.interveen.app.ui.theme.CoolGrayRaised
import com.interveen.app.ui.theme.CrispWhite
import com.interveen.app.ui.theme.Emerald
import com.interveen.app.ui.theme.EmeraldSoft
import com.interveen.app.ui.theme.QuietSlate
import com.interveen.app.ui.theme.SlateCharcoal
import com.interveen.app.ui.theme.SoftMist

@Composable
fun MediaSetupScreen(
    state: MediaSetupUiState,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onVideoPicked: (Uri?) -> Unit,
    onImagePicked: (Uri?) -> Unit,
    onSaveTextGoal: () -> Unit,
    onBack: () -> Unit,
    createCaptureUri: (String) -> Uri,
) {
    var captureUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCapture by remember { mutableStateOf<CaptureKind?>(null) }
    var selectedKind by remember {
        mutableStateOf(
            if (state.savedGoal.kind == GoalKind.UNSET) GoalKind.VIDEO else state.savedGoal.kind,
        )
    }

    val recordVideo = rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
        if (success) onVideoPicked(captureUri)
    }
    val takePhoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) onImagePicked(captureUri)
    }
    val pickVideo = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        onVideoPicked(uri)
    }
    val pickImage = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        onImagePicked(uri)
    }
    val cameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { granted ->
        if (!granted.values.all { it }) return@rememberLauncherForActivityResult
        when (pendingCapture) {
            CaptureKind.VIDEO -> {
                val uri = createCaptureUri("mp4")
                captureUri = uri
                recordVideo.launch(uri)
            }
            CaptureKind.PHOTO -> {
                val uri = createCaptureUri("jpg")
                captureUri = uri
                takePhoto.launch(uri)
            }
            null -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateCharcoal)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.media_back),
                    tint = SoftMist,
                )
            }
            Text(
                text = stringResource(R.string.media_title),
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        Text(
            text = stringResource(R.string.media_body),
            style = MaterialTheme.typography.bodyLarge,
            color = SoftMist,
            modifier = Modifier.padding(horizontal = 8.dp),
        )

        GoalPreview(state)

        QuietCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel(stringResource(R.string.media_field_title))
                QuietField(
                    value = state.title,
                    onValueChange = onTitleChange,
                    hint = stringResource(R.string.media_hint_title),
                )
            }
        }

        Text(
            text = stringResource(R.string.media_choose_kind),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CoolGray),
        ) {
            ReminderKindCard(
                selected = selectedKind == GoalKind.VIDEO,
                title = stringResource(R.string.media_kind_video),
                onClick = { selectedKind = GoalKind.VIDEO },
                modifier = Modifier.weight(1f),
            )
            ReminderKindCard(
                selected = selectedKind == GoalKind.IMAGE,
                title = stringResource(R.string.media_kind_photo),
                onClick = { selectedKind = GoalKind.IMAGE },
                modifier = Modifier.weight(1f),
            )
            ReminderKindCard(
                selected = selectedKind == GoalKind.TEXT,
                title = stringResource(R.string.media_kind_words),
                onClick = { selectedKind = GoalKind.TEXT },
                modifier = Modifier.weight(1f),
            )
        }
        Text(
            text = stringResource(
                when (selectedKind) {
                    GoalKind.IMAGE -> R.string.media_kind_photo_body
                    GoalKind.TEXT -> R.string.media_kind_words_body
                    else -> R.string.media_kind_video_body
                },
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = SoftMist,
            modifier = Modifier.padding(horizontal = 8.dp),
        )

        when (selectedKind) {
            GoalKind.VIDEO -> {
                CalmButton(
                    text = stringResource(R.string.media_record),
                    onClick = {
                        pendingCapture = CaptureKind.VIDEO
                        cameraPermission.launch(
                            arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                CalmButton(
                    text = stringResource(R.string.media_library),
                    onClick = {
                        pickVideo.launch(PickVisualMediaRequest(PickVisualMedia.VideoOnly))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    style = CalmButtonStyle.Secondary,
                )
            }
            GoalKind.IMAGE -> {
                CalmButton(
                    text = stringResource(R.string.media_take_photo),
                    onClick = {
                        pendingCapture = CaptureKind.PHOTO
                        cameraPermission.launch(arrayOf(Manifest.permission.CAMERA))
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                CalmButton(
                    text = stringResource(R.string.media_photo),
                    onClick = {
                        pickImage.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    style = CalmButtonStyle.Secondary,
                )
            }
            GoalKind.TEXT -> {
                QuietCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        FieldLabel(stringResource(R.string.media_field_words))
                        QuietField(
                            value = state.body,
                            onValueChange = onBodyChange,
                            hint = stringResource(R.string.media_hint_words),
                            singleLine = false,
                        )
                    }
                }
                CalmButton(
                    text = stringResource(if (state.saved) R.string.media_saved else R.string.media_save_words),
                    onClick = onSaveTextGoal,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.body.isNotBlank(),
                )
            }
            GoalKind.UNSET -> Unit
        }

        Text(
            text = stringResource(R.string.media_privacy),
            style = MaterialTheme.typography.bodyMedium,
            color = QuietSlate,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        CalmButton(
            text = stringResource(R.string.media_done),
            onClick = {
                if (selectedKind == GoalKind.TEXT && state.body.isNotBlank() && !state.saved) {
                    onSaveTextGoal()
                }
                onBack()
            },
            modifier = Modifier.fillMaxWidth(),
            style = CalmButtonStyle.Secondary,
        )
        Spacer(Modifier.height(8.dp))
    }
}

private enum class CaptureKind {
    VIDEO,
    PHOTO,
}

@Composable
private fun ReminderKindCard(
    selected: Boolean,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        color = if (selected) AccentInk else SoftMist,
        style = MaterialTheme.typography.titleMedium,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Emerald else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
    )
}

@Composable
private fun GoalPreview(state: MediaSetupUiState) {
    val goal = state.savedGoal
    if (!goal.isConfigured) return
    QuietCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.media_ready),
                style = MaterialTheme.typography.labelMedium,
                color = QuietSlate,
            )
            when (goal.kind) {
                GoalKind.IMAGE -> AsyncImage(
                    model = goal.mediaUri,
                    contentDescription = goal.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(CardCorner)),
                    contentScale = ContentScale.Crop,
                )
                GoalKind.VIDEO -> Text(stringResource(R.string.media_video_ready), color = SoftMist)
                GoalKind.TEXT -> Text(goal.body, color = SoftMist)
                GoalKind.UNSET -> Unit
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.labelMedium, color = QuietSlate)
}

@Composable
private fun QuietField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    singleLine: Boolean = true,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = CrispWhite),
        cursorBrush = SolidColor(CrispWhite),
        modifier = Modifier
            .fillMaxWidth()
            .height(if (singleLine) 28.dp else 96.dp),
        decorationBox = { inner ->
            if (value.isEmpty()) {
                Text(text = hint, style = MaterialTheme.typography.bodyLarge, color = QuietSlate)
            }
            inner()
        },
    )
}
