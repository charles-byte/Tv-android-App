package com.charles.tvhome.data

import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Placeholder content for step 2 (static layout). Sports times are relative to "now"
 * so the countdown, LIVE and dated states can all be checked on the TV at once.
 */
object SampleData {

    val continueWatching = listOf(
        ContinueItem("Sample show S2:E4", "Netflix", 0.62f, listOf(Color(0xFF3B2A4A), Color(0xFF1B2433))),
        ContinueItem("Sample film", "Showmax", 0.28f, listOf(Color(0xFF233A3A), Color(0xFF141C24))),
        ContinueItem("Sample video", "YouTube", 0.85f, listOf(Color(0xFF4A2F24), Color(0xFF1A1E26))),
        ContinueItem("Sample match replay", "DStv", 0.45f, listOf(Color(0xFF26364F), Color(0xFF151A22))),
    )

    val apps = listOf(
        AppTile("YouTube", "com.google.android.youtube.tv"),
        AppTile("Netflix", "com.netflix.ninja"),
        AppTile("DStv", "com.dstvmobile.android"),
        AppTile("TOD", "com.tod.tv"),
        AppTile("Showmax", "com.showmax.app"),
        AppTile("Spotify", "com.spotify.tv.android"),
    )

    val weather = Weather(temperatureC = 24, location = "Juja", conditions = "Partly cloudy")

    val inputs = listOf(
        InputSource("PlayStation", "HDMI 1", InputSource.Kind.Hdmi),
        InputSource("Decoder", "HDMI 2", InputSource.Kind.Hdmi),
        InputSource("Cast", null, InputSource.Kind.Cast),
    )

    fun sports(now: Instant): List<SportEvent> {
        val hour = now.truncatedTo(ChronoUnit.HOURS)
        return listOf(
            SportEvent(
                series = "F1",
                title = "Sample Grand Prix",
                subtitle = "Sample circuit",
                // Within 24 hours: shows a countdown.
                sessions = listOf(SportSession(null, now.plus(Duration.ofMinutes(200)), Duration.ofHours(2))),
            ),
            SportEvent(
                series = "MotoGP",
                title = "Sample Grand Prix",
                subtitle = "Sample circuit",
                sessions = listOf(
                    // Under way: shows LIVE.
                    SportSession("Sprint", now.minus(Duration.ofMinutes(10)), Duration.ofMinutes(45)),
                    SportSession("Race", hour.plus(Duration.ofHours(26)), Duration.ofMinutes(50)),
                ),
            ),
            SportEvent(
                series = "Man Utd",
                title = "Man Utd v Sample FC",
                subtitle = "Premier League",
                // More than 24 hours away: shows day and time.
                sessions = listOf(SportSession(null, hour.plus(Duration.ofHours(50)), Duration.ofMinutes(115))),
            ),
        )
    }
}
