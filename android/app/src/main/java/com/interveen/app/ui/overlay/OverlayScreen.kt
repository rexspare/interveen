package com.interveen.app.ui.overlay

import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.interveen.app.R
import com.interveen.app.domain.model.Goal
import com.interveen.app.domain.model.GoalKind
import com.interveen.app.ui.components.CalmButton
import com.interveen.app.ui.components.ProgressRing
import com.interveen.app.ui.theme.CardCorner
import com.interveen.app.ui.theme.CoolGray
import com.interveen.app.ui.theme.CrispWhite
import com.interveen.app.ui.theme.OverlayScrim
import com.interveen.app.ui.theme.QuietSlate
import com.interveen.app.ui.theme.SlateCharcoal
import com.interveen.app.ui.theme.SoftMist
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun OverlayScreen(
    goal: Goal,
    onRemembered: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(520)),
        exit = fadeOut(animationSpec = tween(280)),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SlateCharcoal)
                    .then(if (Build.VERSION.SDK_INT >= 31) Modifier.blur(28.dp) else Modifier),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(OverlayScrim),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.overlay_moment),
                    style = MaterialTheme.typography.labelMedium,
                    color = QuietSlate,
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    GoalStage(goal = goal)
                }
                CalmButton(
                    text = stringResource(R.string.overlay_action_remember),
                    onClick = {
                        visible = false
                        onRemembered()
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun GoalStage(goal: Goal) {
    when {
        goal.kind == GoalKind.VIDEO && !goal.mediaUri.isNullOrBlank() -> {
            GoalVideo(uri = goal.mediaUri)
        }
        goal.kind == GoalKind.IMAGE && !goal.mediaUri.isNullOrBlank() -> {
            GoalImage(uri = goal.mediaUri, title = goal.title)
        }
        else -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = goal.title.ifBlank { stringResource(R.string.overlay_default_title) },
                    style = MaterialTheme.typography.headlineMedium,
                    color = CrispWhite,
                )
                Spacer(Modifier.padding(top = 12.dp))
                Text(
                    text = goal.body.ifBlank { stringResource(R.string.overlay_default_body) },
                    style = MaterialTheme.typography.bodyLarge,
                    color = SoftMist,
                )
            }
        }
    }
}

@Composable
private fun GoalImage(uri: String, title: String) {
    AsyncImage(
        model = uri,
        contentDescription = title,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(CardCorner))
            .background(CoolGray),
        contentScale = ContentScale.Fit,
    )
}

@OptIn(UnstableApi::class)
@Composable
private fun GoalVideo(uri: String) {
    val context = LocalContext.current
    var progress by remember { mutableFloatStateOf(0f) }
    val player = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
            volume = 1f
            prepare()
        }
    }

    DisposableEffect(player) {
        onDispose { player.release() }
    }

    LaunchedEffect(player) {
        while (isActive) {
            val duration = player.duration
            progress = if (duration > 0) player.currentPosition.toFloat() / duration.toFloat() else 0f
            delay(80)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(CardCorner))
            .background(CoolGray),
        contentAlignment = Alignment.Center,
    ) {
        AndroidView(
            factory = { viewContext ->
                PlayerView(viewContext).apply {
                    this.player = player
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
        ) {
            ProgressRing(progress = progress, diameter = 40.dp, stroke = 3.dp)
        }
    }
}
