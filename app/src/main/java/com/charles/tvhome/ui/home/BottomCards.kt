package com.charles.tvhome.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.tv.material3.Text
import com.charles.tvhome.data.EventTimeLabel
import com.charles.tvhome.data.InputSource
import com.charles.tvhome.data.SportEvent
import com.charles.tvhome.data.eventTimeLabel
import com.charles.tvhome.ui.components.FocusTile
import com.charles.tvhome.ui.theme.TvColors
import com.charles.tvhome.ui.theme.TvType
import com.charles.tvhome.ui.theme.dpx
import java.time.ZonedDateTime

/** Sports: next up. One focusable card holding three fixed rows. */
@Composable
fun SportsSection(events: List<SportEvent>, now: ZonedDateTime, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier) {
        SectionLabel("Sports · next up", tag = "SAMPLE")
        FocusTile(
            onClick = { showComingSoon(context, "Sports details") },
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 28.dpx, vertical = 8.dpx),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                events.forEachIndexed { index, event ->
                    if (index > 0) {
                        Box(Modifier.fillMaxWidth().height(1.dpx).background(TvColors.Border))
                    }
                    SportRow(event, now, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SportRow(event: SportEvent, now: ZonedDateTime, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (event.stale) 0.5f else 1f),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(132.dpx)
                .background(TvColors.Ground, RoundedCornerShape(8.dpx))
                .padding(vertical = 8.dpx),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = event.series, style = TvType.badge, color = TvColors.TextPrimary, maxLines = 1)
        }
        Spacer(Modifier.width(24.dpx))
        Column(Modifier.weight(1f)) {
            Text(
                text = event.title,
                style = TvType.cardTitle,
                color = TvColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = if (event.stale) "${event.subtitle}  [UPDATE FAILED]" else event.subtitle,
                style = TvType.body,
                color = TvColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(16.dpx))
        Column(horizontalAlignment = Alignment.End) {
            event.sessions.forEach { session ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (session.label != null) {
                        Text(text = session.label, style = TvType.body, color = TvColors.TextSecondary)
                        Spacer(Modifier.width(12.dpx))
                    }
                    TimeText(eventTimeLabel(session, now), event.liveScore)
                }
            }
        }
    }
}

@Composable
private fun TimeText(label: EventTimeLabel, liveScore: String?) {
    when (label) {
        EventTimeLabel.Live -> Text(
            text = if (liveScore != null) "LIVE  $liveScore" else "LIVE",
            style = TvType.time,
            color = TvColors.Accent,
        )
        is EventTimeLabel.Countdown -> Text(text = label.text, style = TvType.time, color = TvColors.TextPrimary)
        is EventTimeLabel.At -> Text(
            text = label.text,
            style = TvType.time,
            color = TvColors.TextSecondary,
            textAlign = TextAlign.End,
        )
    }
}

/** Inputs: one tile per source, stacked to match the sports card height. */
@Composable
fun InputsSection(inputs: List<InputSource>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier) {
        SectionLabel("Inputs")
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dpx),
        ) {
            inputs.forEach { input ->
                FocusTile(
                    onClick = { showComingSoon(context, "Switching to ${input.label}") },
                    modifier = Modifier.fillMaxWidth().weight(1f),
                ) { focused ->
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dpx),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        InputGlyph(input.kind, Modifier.size(36.dpx))
                        Spacer(Modifier.width(20.dpx))
                        Text(
                            text = input.label,
                            style = TvType.cardTitle,
                            color = if (focused) TvColors.TextPrimary else TvColors.TextSecondary,
                            maxLines = 1,
                            modifier = Modifier.weight(1f),
                        )
                        if (input.port != null) {
                            Text(text = input.port, style = TvType.body, color = TvColors.TextSecondary, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

/** Simple line glyphs drawn in code: an HDMI plug or a cast screen. */
@Composable
private fun InputGlyph(kind: InputSource.Kind, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val stroke = Stroke(width = w * 0.08f)
        val color = TvColors.TextSecondary
        when (kind) {
            InputSource.Kind.Hdmi -> {
                val path = Path().apply {
                    moveTo(w * 0.1f, w * 0.3f)
                    lineTo(w * 0.9f, w * 0.3f)
                    lineTo(w * 0.9f, w * 0.52f)
                    lineTo(w * 0.75f, w * 0.7f)
                    lineTo(w * 0.25f, w * 0.7f)
                    lineTo(w * 0.1f, w * 0.52f)
                    close()
                }
                drawPath(path, color, style = stroke)
                drawLine(color, Offset(w * 0.3f, w * 0.47f), Offset(w * 0.7f, w * 0.47f), strokeWidth = w * 0.08f)
            }
            InputSource.Kind.Cast -> {
                drawRoundRect(
                    color,
                    topLeft = Offset(w * 0.3f, w * 0.15f),
                    size = Size(w * 0.62f, w * 0.6f),
                    cornerRadius = CornerRadius(w * 0.06f),
                    style = stroke,
                )
                val origin = Offset(w * 0.08f, w * 0.88f)
                drawCircle(color, radius = w * 0.06f, center = origin)
                listOf(0.28f, 0.48f).forEach { r ->
                    drawArc(
                        color,
                        startAngle = 270f,
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = Offset(origin.x - w * r, origin.y - w * r),
                        size = Size(w * r * 2, w * r * 2),
                        style = stroke,
                    )
                }
            }
        }
    }
}
