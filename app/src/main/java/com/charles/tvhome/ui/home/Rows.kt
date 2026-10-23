package com.charles.tvhome.ui.home

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.tv.material3.Text
import com.charles.tvhome.data.AppTile
import com.charles.tvhome.data.ContinueItem
import com.charles.tvhome.ui.components.FocusTile
import com.charles.tvhome.ui.theme.TvColors
import com.charles.tvhome.ui.theme.TvType
import com.charles.tvhome.ui.theme.dpx

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier, tag: String? = null) {
    Row(
        modifier = modifier.padding(bottom = 14.dpx),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dpx),
    ) {
        Text(text = text.uppercase(), style = TvType.section, color = TvColors.TextSecondary)
        if (tag != null) {
            Text(text = tag, style = TvType.section, color = TvColors.Border)
        }
    }
}

/** Four equal cards. Up from any card goes to Settings. */
@Composable
fun ContinueWatchingRow(
    items: List<ContinueItem>,
    settingsFocus: FocusRequester,
    onSelect: (ContinueItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        SectionLabel("Continue watching", tag = "SAMPLE")
        Row(horizontalArrangement = Arrangement.spacedBy(24.dpx)) {
            val shown = items.take(4)
            shown.forEach { item ->
                FocusTile(
                    onClick = { onSelect(item) },
                    modifier = Modifier
                        .weight(1f)
                        .focusProperties { up = settingsFocus },
                ) {
                    ContinueCardContent(item)
                }
            }
            repeat(4 - shown.size) { Spacer(Modifier.weight(1f)) }
        }
    }
}

@Composable
private fun ContinueCardContent(item: ContinueItem) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(168.dpx)
                .background(Brush.linearGradient(item.thumbColors)),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(6.dpx)
                    .background(TvColors.Border),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(item.progress.coerceIn(0f, 1f))
                        .height(6.dpx)
                        .background(TvColors.Accent),
                )
            }
        }
        Column(modifier = Modifier.padding(horizontal = 20.dpx, vertical = 14.dpx)) {
            Text(
                text = item.title,
                style = TvType.cardTitle,
                color = TvColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(text = item.sourceApp, style = TvType.body, color = TvColors.TextSecondary, maxLines = 1)
        }
    }
}

/**
 * Six equal app tiles. [firstFocus] is attached to the first tile (initial focus).
 * [upTarget] overrides Up when the Continue watching row is hidden.
 */
@Composable
fun AppsRow(
    apps: List<AppTile>,
    firstFocus: FocusRequester,
    upTarget: FocusRequester?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Column(modifier) {
        SectionLabel("Apps")
        Row(horizontalArrangement = Arrangement.spacedBy(24.dpx)) {
            apps.take(6).forEachIndexed { index, app ->
                FocusTile(
                    onClick = { showComingSoon(context, "Opening ${app.label}") },
                    modifier = Modifier
                        .weight(1f)
                        .height(150.dpx)
                        .focusProperties { if (upTarget != null) up = upTarget },
                    focusRequester = if (index == 0) firstFocus else null,
                ) { focused ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = app.label,
                            style = TvType.tileLabel,
                            color = if (focused) TvColors.TextPrimary else TvColors.TextSecondary,
                            maxLines = 1,
                        )
                    }
                }
            }
            repeat(6 - apps.size.coerceAtMost(6)) { Spacer(Modifier.weight(1f)) }
        }
    }
}
