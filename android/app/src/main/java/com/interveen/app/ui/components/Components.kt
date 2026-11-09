package com.interveen.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.interveen.app.ui.theme.AccentInk
import com.interveen.app.ui.theme.CardCorner
import com.interveen.app.ui.theme.CoolGray
import com.interveen.app.ui.theme.CoolGrayRaised
import com.interveen.app.ui.theme.CrispWhite
import com.interveen.app.ui.theme.Emerald
import com.interveen.app.ui.theme.Figure
import com.interveen.app.ui.theme.QuietSlate
import com.interveen.app.ui.theme.Rule
import com.interveen.app.ui.theme.SoftCorner
import com.interveen.app.ui.theme.SoftMist

enum class CalmButtonStyle {
    Primary,
    Secondary,
}

@Composable
fun QuietCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val clickable = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(CardCorner))
            .background(CoolGray)
            .border(1.dp, Rule, RoundedCornerShape(CardCorner))
            .then(clickable)
            .padding(16.dp),
    ) {
        content()
    }
}

@Composable
fun PermissionToggleRow(
    title: String,
    body: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    QuietCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftMist,
                )
            }
            Spacer(Modifier.width(16.dp))
            Switch(
                checked = enabled,
                onCheckedChange = { onClick() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AccentInk,
                    checkedTrackColor = Emerald,
                    uncheckedThumbColor = CrispWhite,
                    uncheckedTrackColor = Rule,
                    uncheckedBorderColor = Rule,
                    checkedBorderColor = Emerald,
                ),
            )
        }
    }
}

@Composable
fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    QuietCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = QuietSlate,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = Figure,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.sp,
                ),
                color = CrispWhite,
            )
        }
    }
}

@Composable
fun CalmButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: CalmButtonStyle = CalmButtonStyle.Primary,
) {
    val background by animateColorAsState(
        targetValue = when {
            !enabled -> CoolGrayRaised
            style == CalmButtonStyle.Primary -> Emerald
            else -> CoolGray
        },
        animationSpec = tween(280),
        label = "calmButtonBg",
    )
    val foreground by animateColorAsState(
        targetValue = when {
            !enabled -> QuietSlate
            style == CalmButtonStyle.Primary -> AccentInk
            else -> CrispWhite
        },
        animationSpec = tween(280),
        label = "calmButtonFg",
    )
    val shape = RoundedCornerShape(SoftCorner)
    Box(
        modifier = modifier
            .clip(shape)
            .background(background)
            .border(
                width = 1.dp,
                color = if (style == CalmButtonStyle.Primary && enabled) Emerald else Rule,
                shape = shape,
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = foreground,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    diameter: Dp = 56.dp,
    stroke: Dp = 4.dp,
) {
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(400),
        label = "ring",
    )
    Canvas(modifier = modifier.size(diameter)) {
        val strokePx = stroke.toPx()
        val arcSize = Size(size.minDimension - strokePx, size.minDimension - strokePx)
        val topLeft = Offset(strokePx / 2f, strokePx / 2f)
        drawArc(
            color = Rule,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokePx, cap = StrokeCap.Round),
        )
        drawArc(
            color = Emerald,
            startAngle = -90f,
            sweepAngle = 360f * animated,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = strokePx, cap = StrokeCap.Round),
        )
    }
}

@Composable
fun StatusDot(active: Boolean, modifier: Modifier = Modifier) {
    val color by animateColorAsState(
        targetValue = if (active) Emerald else QuietSlate,
        animationSpec = tween(320),
        label = "statusDot",
    )
    Box(
        modifier = modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color),
    )
}
