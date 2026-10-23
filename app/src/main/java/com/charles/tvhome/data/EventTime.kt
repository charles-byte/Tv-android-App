package com.charles.tvhome.data

import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

sealed interface EventTimeLabel {
    data object Live : EventTimeLabel
    data class Countdown(val text: String) : EventTimeLabel
    data class At(val text: String) : EventTimeLabel
}

private val atFormat = DateTimeFormatter.ofPattern("EEE d MMM, HH:mm", Locale.UK)

/** LIVE while under way, "in 3 h 20 m" within 24 hours, otherwise "Sun 25 Oct, 16:30" in the device timezone. */
fun eventTimeLabel(session: SportSession, now: ZonedDateTime): EventTimeLabel {
    val start = session.start.atZone(now.zone)
    val end = start.plus(session.duration)
    if (!now.isBefore(start) && now.isBefore(end)) return EventTimeLabel.Live

    val until = Duration.between(now, start)
    if (!until.isNegative && until <= Duration.ofHours(24)) {
        // Round up so "in 0 m" never shows before the start.
        val minutes = (until.toMillis() + 59_999) / 60_000
        val h = minutes / 60
        val m = minutes % 60
        return EventTimeLabel.Countdown(if (h > 0) "in $h h $m m" else "in $m m")
    }
    return EventTimeLabel.At(start.format(atFormat))
}
