package com.interveen.app.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.interveen.app.R
import com.interveen.app.ui.theme.AccentInk
import com.interveen.app.ui.theme.CoolGray
import com.interveen.app.ui.theme.CrispWhite
import com.interveen.app.ui.theme.Emerald
import com.interveen.app.ui.theme.Instrument
import com.interveen.app.ui.theme.QuietSlate
import com.interveen.app.ui.theme.SlateCharcoal

@Composable
fun OnboardingGraphic(page: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(CoolGray),
    ) {
        val configuration = LocalConfiguration.current
        val deviceRatio = configuration.screenWidthDp.toFloat() / configuration.screenHeightDp.toFloat()
        if (page == 0) {
            Canvas(modifier = Modifier.fillMaxSize()) { drawWelcome(1f / deviceRatio) }
        } else {
            HowGraphic(deviceRatio)
        }
    }
}

private fun DrawScope.drawWelcome(heightOverWidth: Float) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    drawCircle(
        color = Emerald.copy(alpha = 0.12f),
        radius = size.minDimension * 0.44f,
        center = Offset(cx, cy),
    )
    drawCircle(
        color = Emerald.copy(alpha = 0.08f),
        radius = size.minDimension * 0.30f,
        center = Offset(cx, cy),
    )
    val phone = phoneSize(
        maxWidth = size.width * 0.46f,
        maxHeight = size.height * 0.86f,
        heightOverWidth = heightOverWidth,
    )
    phone(
        topLeft = Offset(cx - phone.width / 2f, cy - phone.height / 2f),
        phone = phone,
        screen = SlateCharcoal,
        content = { origin, screen -> reminderScreen(origin, screen) },
    )
}

private fun DrawScope.phoneSize(maxWidth: Float, maxHeight: Float, heightOverWidth: Float): Size {
    val width = minOf(maxWidth, maxHeight / heightOverWidth)
    return Size(width, width * heightOverWidth)
}

private fun DrawScope.phone(
    topLeft: Offset,
    phone: Size,
    screen: Color,
    content: DrawScope.(Offset, Size) -> Unit,
) {
    val outer = phone.width * 0.13f
    val bezel = phone.width * 0.045f
    drawRoundRect(
        color = SlateCharcoal,
        topLeft = topLeft,
        size = phone,
        cornerRadius = CornerRadius(outer),
    )
    drawRoundRect(
        color = QuietSlate.copy(alpha = 0.28f),
        topLeft = topLeft,
        size = phone,
        cornerRadius = CornerRadius(outer),
        style = Stroke(width = 1.dp.toPx()),
    )
    val button = Size(phone.width * 0.02f, phone.height * 0.07f)
    drawRoundRect(
        color = QuietSlate.copy(alpha = 0.45f),
        topLeft = Offset(topLeft.x + phone.width - 1.dp.toPx(), topLeft.y + phone.height * 0.28f),
        size = button,
        cornerRadius = CornerRadius(2.dp.toPx()),
    )
    val speaker = Size(phone.width * 0.22f, phone.width * 0.028f)
    drawRoundRect(
        color = QuietSlate.copy(alpha = 0.55f),
        topLeft = Offset(
            topLeft.x + (phone.width - speaker.width) / 2f,
            topLeft.y + 7.dp.toPx(),
        ),
        size = speaker,
        cornerRadius = CornerRadius(2.dp.toPx()),
    )
    val screenTop = Offset(topLeft.x + bezel, topLeft.y + bezel + 8.dp.toPx())
    val screenSize = Size(phone.width - bezel * 2f, phone.height - bezel * 2f - 16.dp.toPx())
    drawRoundRect(
        color = screen,
        topLeft = screenTop,
        size = screenSize,
        cornerRadius = CornerRadius(16.dp.toPx()),
    )
    content(screenTop, screenSize)
    val home = Size(phone.width * 0.28f, phone.width * 0.022f)
    drawRoundRect(
        color = CrispWhite.copy(alpha = 0.40f),
        topLeft = Offset(
            topLeft.x + (phone.width - home.width) / 2f,
            topLeft.y + phone.height - bezel - 1.dp.toPx(),
        ),
        size = home,
        cornerRadius = CornerRadius(2.dp.toPx()),
    )
}

@Composable
private fun HowGraphic(deviceRatio: Float) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PhoneFrame(deviceRatio, Modifier.weight(1f)) { BlurredFeed() }
        Canvas(modifier = Modifier.size(width = 16.dp, height = 12.dp)) {
            val end = Offset(size.width, size.height / 2f)
            drawLine(Emerald, Offset(0f, end.y), end, strokeWidth = 2.dp.toPx())
            val head = 5.dp.toPx()
            drawLine(Emerald, end, Offset(end.x - head, end.y - head * 0.7f), strokeWidth = 2.dp.toPx())
            drawLine(Emerald, end, Offset(end.x - head, end.y + head * 0.7f), strokeWidth = 2.dp.toPx())
        }
        PhoneFrame(deviceRatio, Modifier.weight(1f)) { ReminderMock() }
    }
}

@Composable
private fun PhoneFrame(
    deviceRatio: Float,
    modifier: Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .aspectRatio(deviceRatio)
            .clip(RoundedCornerShape(16.dp))
            .background(SlateCharcoal)
            .border(1.dp, QuietSlate.copy(alpha = 0.28f), RoundedCornerShape(16.dp)),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 5.dp)
                .size(width = 18.dp, height = 3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(QuietSlate.copy(alpha = 0.55f)),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 5.dp, end = 5.dp, top = 12.dp, bottom = 8.dp)
                .clip(RoundedCornerShape(7.dp)),
            content = content,
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 3.dp)
                .size(width = 22.dp, height = 2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(CrispWhite.copy(alpha = 0.4f)),
        )
    }
}

@Composable
private fun BlurredFeed() {
    val tiles = listOf(
        listOf(0xFFC4A484, 0xFF5C4033),
        listOf(0xFF8D6E63, 0xFF3E2723),
        listOf(0xFFD7CCC8, 0xFF6D4C41),
        listOf(0xFF4E342E, 0xFFA1887F),
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121826)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .blur(14.dp)
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            tiles.forEach { row ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    row.forEach { color ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color(color), Color(0xFF1C1410)),
                                    ),
                                ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderMock() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateCharcoal)
            .padding(horizontal = 5.dp, vertical = 4.dp),
    ) {
        Text(
            text = "9:41",
            color = CrispWhite,
            fontFamily = Instrument,
            fontWeight = FontWeight.Medium,
            fontSize = 7.sp,
        )
        Text(
            text = stringResource(R.string.overlay_moment).uppercase(),
            modifier = Modifier.fillMaxWidth(),
            color = QuietSlate,
            fontFamily = Instrument,
            fontWeight = FontWeight.Medium,
            fontSize = 6.sp,
            letterSpacing = 0.8.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp)),
        ) {
            Image(
                painter = painterResource(R.drawable.onboarding_goal),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(22.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xCC0F172A)),
                        ),
                    ),
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .border(0.6.dp, CrispWhite, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = CrispWhite,
                        modifier = Modifier.size(7.dp),
                    )
                }
                Text(
                    text = "0:04",
                    color = CrispWhite,
                    fontFamily = Instrument,
                    fontSize = 5.sp,
                    modifier = Modifier.padding(start = 3.dp),
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .weight(1f)
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(CrispWhite.copy(alpha = 0.35f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.33f)
                            .background(Emerald),
                    )
                }
                Text(
                    text = "0:12",
                    color = CrispWhite,
                    fontFamily = Instrument,
                    fontSize = 5.sp,
                )
            }
        }
        Spacer(Modifier.height(3.dp))
        Text(
            text = stringResource(R.string.overlay_action_remember),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(5.dp))
                .background(Emerald)
                .padding(vertical = 2.dp),
            style = TextStyle(
                color = AccentInk,
                fontFamily = Instrument,
                fontWeight = FontWeight.Medium,
                fontSize = 6.5.sp,
                lineHeight = 8.sp,
                textAlign = TextAlign.Center,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun DrawScope.reminderScreen(origin: Offset, screen: Size) {
    val pad = 10.dp.toPx()
    val photoH = screen.height * 0.50f
    val photo = Offset(origin.x + pad, origin.y + pad)
    val photoSize = Size(screen.width - pad * 2f, photoH)
    drawRoundRect(
        color = Emerald.copy(alpha = 0.22f),
        topLeft = photo,
        size = photoSize,
        cornerRadius = CornerRadius(10.dp.toPx()),
    )
    drawCircle(
        color = Emerald,
        radius = 7.dp.toPx(),
        center = Offset(photo.x + photoSize.width / 2f, photo.y + photoSize.height / 2f),
    )
    val lineX = origin.x + pad
    drawRoundRect(
        color = CrispWhite.copy(alpha = 0.92f),
        topLeft = Offset(lineX, photo.y + photoH + 14.dp.toPx()),
        size = Size(screen.width * 0.58f, 8.dp.toPx()),
        cornerRadius = CornerRadius(4.dp.toPx()),
    )
    drawRoundRect(
        color = CrispWhite.copy(alpha = 0.32f),
        topLeft = Offset(lineX, photo.y + photoH + 28.dp.toPx()),
        size = Size(screen.width * 0.36f, 6.dp.toPx()),
        cornerRadius = CornerRadius(3.dp.toPx()),
    )
}
