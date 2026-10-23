package com.charles.tvhome.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.tv.material3.Text
import com.charles.tvhome.data.ContinueItem
import com.charles.tvhome.data.EventTimeLabel
import com.charles.tvhome.data.InstalledApps
import com.charles.tvhome.data.ResolvedApp
import com.charles.tvhome.data.SportEvent
import com.charles.tvhome.data.eventTimeLabel
import com.charles.tvhome.ui.components.FocusTile
import com.charles.tvhome.ui.components.GlassEdge
import com.charles.tvhome.ui.components.GlassFill
import com.charles.tvhome.ui.theme.TvColors
import com.charles.tvhome.ui.theme.TvType
import com.charles.tvhome.ui.theme.dpx
import java.time.ZonedDateTime

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier, note: String? = null) {
    Row(modifier = modifier.padding(bottom = 14.dpx), verticalAlignment = Alignment.Bottom) {
        Text(text = text, style = TvType.section, color = TvColors.TextPrimary)
        if (note != null) {
            Spacer(Modifier.width(14.dpx))
            Text(text = note, style = TvType.body, color = TvColors.TextTertiary)
        }
    }
}

/** Four 16:9 cards, title beneath like a poster wall. */
@Composable
fun ContinueWatchingRow(
    items: List<ContinueItem>,
    onSelect: (ContinueItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        SectionTitle("Continue watching", note = "sample")
        Row(horizontalArrangement = Arrangement.spacedBy(32.dpx)) {
            val shown = items.take(4)
            shown.forEach { item ->
                var focused by remember { mutableStateOf(false) }
                Column(Modifier.weight(1f)) {
                    FocusTile(
                        onClick = { onSelect(item) },
                        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                        fill = null,
                        focusScale = 1.05f,
                        onFocusChange = { focused = it },
                    ) {
                        PlaceholderArt(item.thumbColors)
                        ProgressBar(item.progress, Modifier.align(Alignment.BottomStart))
                    }
                    Spacer(Modifier.height(16.dpx))
                    Text(
                        text = item.title,
                        style = TvType.cardTitle,
                        color = if (focused) TvColors.TextPrimary else TvColors.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(text = item.sourceApp, style = TvType.body, color = TvColors.TextTertiary, maxLines = 1)
                }
            }
            repeat(4 - shown.size) { Spacer(Modifier.weight(1f)) }
        }
    }
}

/** Soft lit gradient until real thumbnails arrive (step 6). */
@Composable
private fun PlaceholderArt(colors: List<Color>) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(colors))
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.18f), Color.Transparent),
                        center = Offset(size.width * 0.25f, size.height * 0.2f),
                        radius = size.width * 0.7f,
                    ),
                )
            },
    )
}

@Composable
private fun ProgressBar(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dpx, vertical = 16.dpx)
            .height(5.dpx)
            .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(3.dpx)),
    ) {
        Box(
            Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(5.dpx)
                .background(TvColors.Accent, RoundedCornerShape(3.dpx)),
        )
    }
}

/** Sports: next up. Three glass cards: F1, MotoGP, Man Utd. */
@Composable
fun SportsRow(events: List<SportEvent>, now: ZonedDateTime, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(modifier) {
        SectionTitle("Sport · next up", note = "sample")
        Row(horizontalArrangement = Arrangement.spacedBy(32.dpx)) {
            events.take(3).forEach { event ->
                FocusTile(
                    onClick = { showComingSoon(context, "Sports details") },
                    modifier = Modifier.weight(1f).height(140.dpx),
                    focusScale = 1.04f,
                ) {
                    SportCard(event, now)
                }
            }
        }
    }
}

@Composable
private fun SportCard(event: SportEvent, now: ZonedDateTime) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dpx, vertical = 20.dpx)
            .alpha(if (event.stale) 0.5f else 1f),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = event.series.uppercase(), style = TvType.overline, color = TvColors.TextTertiary)
        Column {
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            event.sessions.forEachIndexed { i, session ->
                if (i > 0) Text(text = "   ·   ", style = TvType.time, color = TvColors.TextTertiary)
                if (session.label != null) {
                    Text(text = session.label + " ", style = TvType.body, color = TvColors.TextSecondary)
                }
                TimeText(eventTimeLabel(session, now), event.liveScore)
            }
        }
    }
}

@Composable
private fun TimeText(label: EventTimeLabel, liveScore: String?) {
    when (label) {
        EventTimeLabel.Live -> Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .background(TvColors.Accent, RoundedCornerShape(6.dpx))
                    .padding(horizontal = 10.dpx, vertical = 2.dpx),
            ) {
                Text(text = "LIVE", style = TvType.overline, color = TvColors.Ground)
            }
            if (liveScore != null) {
                Spacer(Modifier.width(10.dpx))
                Text(text = liveScore, style = TvType.time, color = TvColors.TextPrimary)
            }
        }
        is EventTimeLabel.Countdown -> Text(text = label.text, style = TvType.time, color = TvColors.Accent)
        is EventTimeLabel.At -> Text(text = label.text, style = TvType.time, color = TvColors.TextPrimary)
    }
}

/**
 * The glass shelf of apps. Each tile shows the app's own TV banner, read from the
 * installed app, so nothing is bundled. Tiles for apps that aren't installed are dimmed.
 */
@Composable
fun AppShelf(
    apps: List<ResolvedApp>,
    loaded: Boolean,
    firstFocus: FocusRequester,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val shape = RoundedCornerShape(44.dpx)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(GlassFill, shape)
            .border(1.dpx, GlassEdge, shape)
            .padding(horizontal = 24.dpx, vertical = 22.dpx),
        horizontalArrangement = Arrangement.spacedBy(26.dpx),
    ) {
        apps.take(6).forEachIndexed { index, app ->
            val missing = loaded && !app.installed
            FocusTile(
                onClick = {
                    val pkg = app.packageName
                    when {
                        pkg != null -> if (!InstalledApps.launch(context, pkg)) showMessage(context, "Couldn't open ${app.tile.label}")
                        loaded -> showMessage(context, "${app.tile.label} isn't installed")
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(16f / 9f)
                    .alpha(if (missing) 0.4f else 1f),
                cornerRadius = 18.dpx,
                fill = if (app.banner != null) null else SolidColor(TvColors.Card),
                focusScale = 1.1f,
                focusRequester = if (index == 0) firstFocus else null,
            ) { focused ->
                ShelfTileContent(app, missing, focused)
            }
        }
        repeat(6 - apps.size.coerceAtMost(6)) { Spacer(Modifier.weight(1f)) }
    }
}

@Composable
private fun ShelfTileContent(app: ResolvedApp, missing: Boolean, focused: Boolean) {
    val banner = app.banner
    val icon = app.icon
    if (banner != null) {
        Image(
            bitmap = banner,
            contentDescription = app.tile.label,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        return
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Image(bitmap = icon, contentDescription = null, modifier = Modifier.size(56.dpx))
            Spacer(Modifier.height(8.dpx))
        }
        Text(
            text = app.tile.label,
            style = TvType.tileLabel,
            color = if (focused) TvColors.TextPrimary else TvColors.TextSecondary,
            maxLines = 1,
        )
        if (missing) {
            Text(text = "Not installed", style = TvType.body, color = TvColors.TextTertiary, maxLines = 1)
        }
    }
}
