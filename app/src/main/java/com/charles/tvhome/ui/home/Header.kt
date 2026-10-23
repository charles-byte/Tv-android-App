package com.charles.tvhome.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SettingsInputHdmi
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.tv.material3.Text
import com.charles.tvhome.data.InputSource
import com.charles.tvhome.data.Weather
import com.charles.tvhome.ui.components.FocusTile
import com.charles.tvhome.ui.theme.TvColors
import com.charles.tvhome.ui.theme.TvType
import com.charles.tvhome.ui.theme.dpx
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.UK)
private val dateFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.UK)

/** Clock and date on the left; weather, input buttons and Settings on the right. */
@Composable
fun Header(
    now: ZonedDateTime,
    weather: Weather,
    inputs: List<InputSource>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    // Name of the focused round button, shown beneath the row.
    var caption by remember { mutableStateOf<String?>(null) }

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = now.format(timeFormat), style = TvType.clock, color = TvColors.TextPrimary)
            Text(text = now.format(dateFormat), style = TvType.date, color = TvColors.TextSecondary)
        }
        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WeatherBlock(weather)
                Spacer(Modifier.width(40.dpx))
                Row(horizontalArrangement = Arrangement.spacedBy(20.dpx)) {
                    inputs.forEach { input ->
                        val name = if (input.port != null) "${input.label} · ${input.port}" else input.label
                        RoundButton(
                            icon = input.kind.icon(),
                            description = name,
                            onClick = { showComingSoon(context, "Switching to ${input.label}") },
                            onFocusChange = { has -> caption = if (has) name else caption?.takeIf { it != name } },
                        )
                    }
                    RoundButton(
                        icon = Icons.Rounded.Settings,
                        description = "Settings",
                        onClick = { openSystemSettings(context) },
                        onFocusChange = { has -> caption = if (has) "Settings" else caption?.takeIf { it != "Settings" } },
                    )
                }
            }
            Spacer(Modifier.height(14.dpx))
            Box(Modifier.height(28.dpx)) {
                caption?.let { Text(text = it, style = TvType.body, color = TvColors.TextSecondary) }
            }
        }
    }
}

private fun InputSource.Kind.icon(): ImageVector = when (this) {
    InputSource.Kind.Console -> Icons.Rounded.SportsEsports
    InputSource.Kind.Decoder -> Icons.Rounded.SettingsInputHdmi
    InputSource.Kind.Cast -> Icons.Rounded.Cast
}

@Composable
private fun RoundButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
) {
    FocusTile(
        onClick = onClick,
        modifier = Modifier.size(76.dpx),
        cornerRadius = 38.dpx,
        focusScale = 1.12f,
        onFocusChange = onFocusChange,
    ) { focused ->
        Image(
            imageVector = icon,
            contentDescription = description,
            colorFilter = ColorFilter.tint(if (focused) TvColors.TextPrimary else TvColors.TextSecondary),
            modifier = Modifier.size(34.dpx).align(Alignment.Center),
        )
    }
}

@Composable
private fun WeatherBlock(weather: Weather) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dpx)) {
        WeatherIcon(Modifier.size(56.dpx))
        Column {
            Text(text = "${weather.temperatureC}°", style = TvType.temperature, color = TvColors.TextPrimary)
            Text(text = "${weather.location} · ${weather.conditions}", style = TvType.body, color = TvColors.TextSecondary)
        }
    }
}

/** Sun behind a cloud, drawn in code. Real conditions arrive with live weather (step 4). */
@Composable
private fun WeatherIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        drawCircle(TvColors.Accent, radius = w * 0.22f, center = Offset(w * 0.64f, w * 0.34f))
        val cloud = TvColors.TextPrimary
        drawCircle(cloud, radius = w * 0.17f, center = Offset(w * 0.36f, w * 0.58f))
        drawCircle(cloud, radius = w * 0.21f, center = Offset(w * 0.56f, w * 0.54f))
        drawRoundRect(
            cloud,
            topLeft = Offset(w * 0.18f, w * 0.58f),
            size = Size(w * 0.62f, w * 0.2f),
            cornerRadius = CornerRadius(w * 0.1f),
        )
    }
}
