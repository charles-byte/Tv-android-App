package com.charles.tvhome.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.charles.tvhome.ui.theme.TvColors
import com.charles.tvhome.ui.theme.dpx

private const val FOCUS_ANIM_MS = 180
private const val FOCUS_SCALE = 1.06f

/**
 * A remote-selectable tile. When focused it scales to 1.06 and draws a double ring
 * outside its edge: a 3 px ground-coloured inner ring, then a 4 px accent outer ring.
 * The dark inner ring keeps the accent ring readable on light wallpapers.
 */
@Composable
fun FocusTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dpx,
    background: Color = TvColors.Card,
    focusRequester: FocusRequester? = null,
    content: @Composable BoxScope.(focused: Boolean) -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (focused) 1f else 0f,
        animationSpec = tween(FOCUS_ANIM_MS),
        label = "focus",
    )
    val shape = RoundedCornerShape(cornerRadius)
    val innerRing = 3.dpx
    val outerRing = 4.dpx
    val shadow = 24.dpx
    val idleBorder = 1.dpx
    val interactions = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .onFocusChanged { focused = it.isFocused }
            .graphicsLayer {
                val s = 1f + (FOCUS_SCALE - 1f) * progress
                scaleX = s
                scaleY = s
            }
            .shadow(elevation = shadow * progress, shape = shape, clip = false)
            .drawWithContent {
                drawContent()
                if (progress > 0f) {
                    val inner = innerRing.toPx()
                    val outer = outerRing.toPx()
                    val r = cornerRadius.toPx()
                    val i = inner / 2f
                    drawRoundRect(
                        color = TvColors.Ground.copy(alpha = progress),
                        topLeft = Offset(-i, -i),
                        size = Size(size.width + inner, size.height + inner),
                        cornerRadius = CornerRadius(r + i),
                        style = Stroke(width = inner),
                    )
                    val o = inner + outer / 2f
                    drawRoundRect(
                        color = TvColors.Accent.copy(alpha = progress),
                        topLeft = Offset(-o, -o),
                        size = Size(size.width + 2 * o, size.height + 2 * o),
                        cornerRadius = CornerRadius(r + o),
                        style = Stroke(width = outer),
                    )
                }
            }
            .clip(shape)
            .background(background)
            .border(idleBorder, TvColors.Border, shape)
            .clickable(interactionSource = interactions, indication = null, onClick = onClick),
    ) {
        content(focused)
    }
}
