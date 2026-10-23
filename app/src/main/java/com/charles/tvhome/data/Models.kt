package com.charles.tvhome.data

import androidx.compose.ui.graphics.Color
import java.time.Duration
import java.time.Instant

data class ContinueItem(
    val title: String,
    val sourceApp: String,
    /** 0..1 */
    val progress: Float,
    /** Placeholder thumbnail gradient until real artwork arrives in step 6. */
    val thumbColors: List<Color>,
)

data class AppTile(
    val label: String,
    /** Expected package; confirmed on the device in step 3. */
    val packageName: String,
)

data class Weather(
    val temperatureC: Int,
    val location: String,
    val conditions: String,
)

data class SportSession(
    /** "Sprint", "Race" or null when the row has a single time. */
    val label: String?,
    val start: Instant,
    /** How long the event counts as LIVE after it starts. */
    val duration: Duration,
)

data class SportEvent(
    val series: String,
    val title: String,
    val subtitle: String,
    val sessions: List<SportSession>,
    val liveScore: String? = null,
    /** True when the last refresh failed and this is a cached value. */
    val stale: Boolean = false,
)

data class InputSource(
    val label: String,
    val port: String?,
    val kind: Kind,
) {
    enum class Kind { Hdmi, Cast }
}
