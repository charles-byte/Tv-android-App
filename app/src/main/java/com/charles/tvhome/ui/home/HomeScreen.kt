package com.charles.tvhome.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.charles.tvhome.data.InstalledApps
import com.charles.tvhome.data.ResolvedApp
import com.charles.tvhome.data.SampleData
import com.charles.tvhome.ui.theme.TvColors
import com.charles.tvhome.ui.theme.dpx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * @param homePresses increments each time Home is pressed while the launcher is already open;
 * focus returns to the first app.
 * @param resumes increments each time the home screen comes back into view; installed apps
 * are looked up again then, so a newly installed app appears on the shelf.
 */
@Composable
fun HomeScreen(homePresses: Int, resumes: Int) {
    val context = LocalContext.current
    val now by rememberMinuteClock()
    val firstApp = remember { FocusRequester() }

    val continueWatching = SampleData.continueWatching
    val sports = remember { SampleData.sports(Instant.now()) }

    val apps by produceState(
        initialValue = AppsState(SampleData.apps.map { ResolvedApp(it, null, null, null) }, loaded = false),
        resumes,
    ) {
        value = AppsState(withContext(Dispatchers.IO) { InstalledApps.resolve(context, SampleData.apps) }, loaded = true)
    }

    LaunchedEffect(homePresses) {
        // Wait one frame so the tile is laid out before taking focus.
        withFrameNanos { }
        runCatching { firstApp.requestFocus() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind { drawBackdrop() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 96.dpx, end = 96.dpx, top = 56.dpx, bottom = 44.dpx),
        ) {
            Header(now = now, weather = SampleData.weather, inputs = SampleData.inputs)
            Spacer(Modifier.height(28.dpx))

            if (continueWatching.isNotEmpty()) {
                ContinueWatchingRow(
                    items = continueWatching,
                    onSelect = { showComingSoon(context, "Resuming ${it.title}") },
                )
                Spacer(Modifier.height(30.dpx))
            }

            SportsRow(events = sports, now = now)
            Spacer(Modifier.weight(1f))

            AppShelf(apps = apps.list, loaded = apps.loaded, firstFocus = firstApp)
        }
    }
}

private data class AppsState(val list: List<ResolvedApp>, val loaded: Boolean)

/** Deep ground with soft coloured light pooling in from the corners. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBackdrop() {
    val w = size.width
    val h = size.height
    drawRect(TvColors.Ground)
    drawRect(
        Brush.radialGradient(
            listOf(Color(0xFF1F3358).copy(alpha = 0.75f), Color.Transparent),
            center = Offset(w * 0.12f, h * 0.0f),
            radius = w * 0.62f,
        ),
    )
    drawRect(
        Brush.radialGradient(
            listOf(Color(0xFF3B2A5C).copy(alpha = 0.55f), Color.Transparent),
            center = Offset(w * 0.95f, h * 0.45f),
            radius = w * 0.5f,
        ),
    )
    drawRect(
        Brush.radialGradient(
            listOf(TvColors.Accent.copy(alpha = 0.10f), Color.Transparent),
            center = Offset(w * 0.5f, h * 1.1f),
            radius = w * 0.55f,
        ),
    )
}

/**
 * Current time in the device timezone, updated when the minute (or the zone) changes.
 * Checks every second rather than sleeping to the next minute, so the clock is right
 * straight away after the TV wakes from standby.
 */
@Composable
private fun rememberMinuteClock(): State<ZonedDateTime> =
    produceState(initialValue = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES)) {
        while (true) {
            delay(1_000)
            val current = ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES)
            if (current != value) value = current
        }
    }
