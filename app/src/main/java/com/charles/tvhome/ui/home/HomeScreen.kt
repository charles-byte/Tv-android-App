package com.charles.tvhome.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import com.charles.tvhome.data.SampleData
import com.charles.tvhome.ui.theme.TvColors
import com.charles.tvhome.ui.theme.dpx
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * @param homePresses increments each time Home is pressed while the launcher is already open;
 * focus returns to the first app tile.
 */
@Composable
fun HomeScreen(homePresses: Int) {
    val context = LocalContext.current
    val now by rememberMinuteClock()
    val firstApp = remember { FocusRequester() }
    val settings = remember { FocusRequester() }

    val continueWatching = SampleData.continueWatching
    val sports = remember { SampleData.sports(Instant.now()) }

    LaunchedEffect(homePresses) {
        // Wait one frame so the tile is laid out before taking focus.
        withFrameNanos { }
        runCatching { firstApp.requestFocus() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TvColors.Ground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 96.dpx, end = 96.dpx, top = 72.dpx, bottom = 56.dpx),
        ) {
            Header(now = now, weather = SampleData.weather, settingsFocus = settings)
            Spacer(Modifier.height(36.dpx))

            if (continueWatching.isNotEmpty()) {
                ContinueWatchingRow(
                    items = continueWatching,
                    settingsFocus = settings,
                    onSelect = { showComingSoon(context, "Resuming ${it.title}") },
                )
                Spacer(Modifier.height(32.dpx))
            }

            AppsRow(
                apps = SampleData.apps,
                firstFocus = firstApp,
                upTarget = if (continueWatching.isEmpty()) settings else null,
            )
            Spacer(Modifier.height(32.dpx))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(24.dpx),
            ) {
                SportsSection(
                    events = sports,
                    now = now,
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight(),
                )
                InputsSection(
                    inputs = SampleData.inputs,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
            }
        }
    }
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
