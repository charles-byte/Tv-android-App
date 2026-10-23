package com.charles.tvhome.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import com.charles.tvhome.R

object TvColors {
    val Ground = Color(0xFF0B0E13)
    val Card = Color(0xFF151A22)
    val Border = Color(0xFF2A313C)
    val TextPrimary = Color(0xFFEEF1F5)
    val TextSecondary = Color(0xFFA9B1BD)

    /** The one accent colour: focus rings, progress bars, LIVE labels. Change it here. */
    val Accent = Color(0xFFF2A93B)
}

val Sora = FontFamily(
    Font(R.font.sora_regular, FontWeight.Normal),
    Font(R.font.sora_semibold, FontWeight.SemiBold),
    Font(R.font.sora_bold, FontWeight.Bold),
)

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
)

/**
 * The layout is specified in pixels on a 1920x1080 canvas. [LocalUiScale] maps one
 * design pixel to dp for the current screen, so the same layout fills a 1080p panel
 * (usually 960x540 dp) and a 4K panel alike.
 */
val LocalUiScale = staticCompositionLocalOf { 0.5f }

/** Design pixels (1920x1080 canvas) as dp. */
val Number.dpx: Dp
    @Composable
    @ReadOnlyComposable
    get() = (toFloat() * LocalUiScale.current).dp

/** Design pixels as text size. Ignores the system font scale so the layout cannot overflow. */
val Number.spx: TextUnit
    @Composable
    @ReadOnlyComposable
    get() = (toFloat() * LocalUiScale.current / LocalDensity.current.fontScale).sp

object TvType {
    val clock: TextStyle
        @Composable @ReadOnlyComposable
        get() = TextStyle(fontFamily = Sora, fontWeight = FontWeight.SemiBold, fontSize = 104.spx, lineHeight = 112.spx)

    val date: TextStyle
        @Composable @ReadOnlyComposable
        get() = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 28.spx, lineHeight = 36.spx)

    val temperature: TextStyle
        @Composable @ReadOnlyComposable
        get() = TextStyle(fontFamily = Sora, fontWeight = FontWeight.SemiBold, fontSize = 48.spx, lineHeight = 54.spx)

    val section: TextStyle
        @Composable @ReadOnlyComposable
        get() = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 20.spx, lineHeight = 24.spx, letterSpacing = 1.5.spx)

    val tileLabel: TextStyle
        @Composable @ReadOnlyComposable
        get() = TextStyle(fontFamily = Sora, fontWeight = FontWeight.SemiBold, fontSize = 28.spx, lineHeight = 34.spx)

    val cardTitle: TextStyle
        @Composable @ReadOnlyComposable
        get() = TextStyle(fontFamily = Sora, fontWeight = FontWeight.SemiBold, fontSize = 24.spx, lineHeight = 30.spx)

    val body: TextStyle
        @Composable @ReadOnlyComposable
        get() = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 20.spx, lineHeight = 26.spx)

    val badge: TextStyle
        @Composable @ReadOnlyComposable
        get() = TextStyle(fontFamily = Sora, fontWeight = FontWeight.Bold, fontSize = 18.spx, lineHeight = 22.spx)

    val time: TextStyle
        @Composable @ReadOnlyComposable
        get() = TextStyle(fontFamily = Sora, fontWeight = FontWeight.SemiBold, fontSize = 24.spx, lineHeight = 30.spx)
}

@Composable
fun TvHomeTheme(content: @Composable () -> Unit) {
    val config = LocalConfiguration.current
    val scale = minOf(config.screenWidthDp / 1920f, config.screenHeightDp / 1080f)
    CompositionLocalProvider(LocalUiScale provides scale) {
        MaterialTheme(
            colorScheme = darkColorScheme(
                primary = TvColors.Accent,
                background = TvColors.Ground,
                surface = TvColors.Card,
                onBackground = TvColors.TextPrimary,
                onSurface = TvColors.TextPrimary,
            ),
        ) {
            CompositionLocalProvider(LocalContentColor provides TvColors.TextPrimary, content = content)
        }
    }
}
