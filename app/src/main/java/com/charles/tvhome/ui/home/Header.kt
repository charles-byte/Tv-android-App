package com.charles.tvhome.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.tv.material3.Text
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

@Composable
fun Header(
    now: ZonedDateTime,
    weather: Weather,
    settingsFocus: FocusRequester,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = now.format(timeFormat), style = TvType.clock, color = TvColors.TextPrimary)
            Text(text = now.format(dateFormat), style = TvType.date, color = TvColors.TextSecondary)
        }
        WeatherBlock(weather)
        Spacer(Modifier.width(40.dpx))
        FocusTile(
            onClick = { openSystemSettings(context) },
            modifier = Modifier.size(80.dpx),
            cornerRadius = 40.dpx,
            focusRequester = settingsFocus,
        ) {
            Image(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                colorFilter = ColorFilter.tint(TvColors.TextPrimary),
                modifier = Modifier.size(40.dpx).align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun WeatherBlock(weather: Weather) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dpx)) {
        WeatherIcon(Modifier.size(72.dpx))
        Column {
            Text(text = "${weather.temperatureC}°", style = TvType.temperature, color = TvColors.TextPrimary)
            Text(
                text = "${weather.location} · ${weather.conditions}",
                style = TvType.body,
                color = TvColors.TextSecondary,
            )
        }
    }
}

/** Sun behind a cloud, drawn in code so no artwork is bundled. Real conditions arrive in step 4. */
@Composable
private fun WeatherIcon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        drawCircle(TvColors.Accent, radius = w * 0.22f, center = Offset(w * 0.64f, w * 0.34f))
        val cloud = TvColors.TextSecondary
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
