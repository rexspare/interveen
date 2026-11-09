package com.interveen.app.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.interveen.app.ui.theme.CoolGray
import com.interveen.app.ui.theme.CrispWhite
import com.interveen.app.ui.theme.Emerald
import com.interveen.app.ui.theme.QuietSlate

@Composable
fun OnboardingGraphic(page: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(CoolGray),
    ) {
        when (page) {
            0 -> WelcomeArt()
            else -> HowArt()
        }
    }
}

@Composable
private fun WelcomeArt() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        drawCircle(
            color = Emerald.copy(alpha = 0.10f),
            radius = size.minDimension * 0.42f,
            center = Offset(cx, cy - 8.dp.toPx()),
        )
        drawCircle(
            color = Emerald.copy(alpha = 0.08f),
            radius = size.minDimension * 0.28f,
            center = Offset(cx, cy - 8.dp.toPx()),
        )

        val cardW = w * 0.46f
        val cardH = h * 0.58f
        val card = Offset(cx - cardW / 2f, cy - cardH / 2f + 8.dp.toPx())
        drawRoundRect(
            color = Color(0xFF273548),
            topLeft = card,
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(22.dp.toPx()),
        )
        val photoH = cardH * 0.52f
        drawRoundRect(
            color = Emerald.copy(alpha = 0.22f),
            topLeft = Offset(card.x + 16.dp.toPx(), card.y + 16.dp.toPx()),
            size = Size(cardW - 32.dp.toPx(), photoH),
            cornerRadius = CornerRadius(14.dp.toPx()),
        )
        drawCircle(
            color = Emerald.copy(alpha = 0.55f),
            radius = 8.dp.toPx(),
            center = Offset(cx, card.y + 16.dp.toPx() + photoH / 2f),
        )
        drawRoundRect(
            color = CrispWhite.copy(alpha = 0.88f),
            topLeft = Offset(card.x + 22.dp.toPx(), card.y + photoH + 32.dp.toPx()),
            size = Size(cardW * 0.58f, 10.dp.toPx()),
            cornerRadius = CornerRadius(5.dp.toPx()),
        )
        drawRoundRect(
            color = CrispWhite.copy(alpha = 0.32f),
            topLeft = Offset(card.x + 22.dp.toPx(), card.y + photoH + 50.dp.toPx()),
            size = Size(cardW * 0.40f, 8.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx()),
        )
    }
}

@Composable
private fun HowArt() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val pad = 28.dp.toPx()
        val browser = Size(size.width * 0.40f, size.height * 0.56f)
        val left = Offset(pad, (size.height - browser.height) / 2f)
        drawRoundRect(
            color = Color(0xFF334155),
            topLeft = left,
            size = browser,
            cornerRadius = CornerRadius(18.dp.toPx()),
        )
        drawRoundRect(
            color = QuietSlate.copy(alpha = 0.35f),
            topLeft = Offset(left.x + 14.dp.toPx(), left.y + 18.dp.toPx()),
            size = Size(browser.width - 28.dp.toPx(), 12.dp.toPx()),
            cornerRadius = CornerRadius(6.dp.toPx()),
        )
        drawCircle(
            color = Color(0xFFF87171),
            radius = 6.dp.toPx(),
            center = Offset(left.x + 26.dp.toPx(), left.y + 24.dp.toPx()),
        )
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(left.x + 14.dp.toPx(), left.y + 42.dp.toPx()),
            size = Size(browser.width - 28.dp.toPx(), browser.height - 58.dp.toPx()),
            cornerRadius = CornerRadius(10.dp.toPx()),
        )

        val cardW = size.width * 0.40f
        val cardH = size.height * 0.60f
        val right = Offset(size.width - pad - cardW, (size.height - cardH) / 2f)
        drawRoundRect(
            color = Emerald.copy(alpha = 0.18f),
            topLeft = right,
            size = Size(cardW, cardH),
            cornerRadius = CornerRadius(20.dp.toPx()),
        )
        drawRoundRect(
            color = Emerald.copy(alpha = 0.28f),
            topLeft = Offset(right.x + 18.dp.toPx(), right.y + 20.dp.toPx()),
            size = Size(cardW - 36.dp.toPx(), cardH * 0.42f),
            cornerRadius = CornerRadius(12.dp.toPx()),
        )
        drawRoundRect(
            color = Emerald,
            topLeft = Offset(right.x + 18.dp.toPx(), right.y + cardH * 0.52f),
            size = Size(cardW - 36.dp.toPx(), 10.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx()),
        )
        drawRoundRect(
            color = CrispWhite.copy(alpha = 0.28f),
            topLeft = Offset(right.x + 18.dp.toPx(), right.y + cardH * 0.52f + 20.dp.toPx()),
            size = Size(cardW * 0.50f, 8.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx()),
        )

        val midY = size.height / 2f
        val arrowStart = Offset(left.x + browser.width + 10.dp.toPx(), midY)
        val arrowEnd = Offset(right.x - 10.dp.toPx(), midY)
        drawLine(
            color = Emerald,
            start = arrowStart,
            end = arrowEnd,
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round,
        )
        val head = Path().apply {
            moveTo(arrowEnd.x, arrowEnd.y)
            lineTo(arrowEnd.x - 14.dp.toPx(), arrowEnd.y - 9.dp.toPx())
            lineTo(arrowEnd.x - 14.dp.toPx(), arrowEnd.y + 9.dp.toPx())
            close()
        }
        drawPath(head, color = Emerald)
    }
}
