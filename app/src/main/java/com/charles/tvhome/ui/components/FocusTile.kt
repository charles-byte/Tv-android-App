package com.charles.tvhome.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.charles.tvhome.ui.theme.TvColors
import com.charles.tvhome.ui.theme.dpx

/** Slightly springy, settles in about 250 ms: feels alive without wobbling. */
private val focusSpring = spring<Float>(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow)

/** Glass fill for tiles and panels. */
val GlassFill = Brush.verticalGradient(listOf(TvColors.GlassTop, TvColors.GlassBottom))
val GlassEdge = Brush.verticalGradient(listOf(TvColors.GlassEdgeTop, TvColors.GlassEdgeBottom))

/**
 * A remote-selectable tile. When focused it springs up to [focusScale], lifts on a shadow
 * and draws a double ring outside its edge: a 3 px ground-coloured inner ring, then a 3 px
 * accent outer ring. The dark inner ring keeps the accent readable on light wallpapers.
 *
 * @param fill tile background, or null for none (for tiles filled by an image)
 * @param edge 1 px edge drawn at rest, or null for none
 */
@Composable
fun FocusTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dpx,
    fill: Brush? = GlassFill,
    edge: Brush? = GlassEdge,
    focusScale: Float = 1.06f,
    focusRequester: FocusRequester? = null,
    onFocusChange: ((Boolean) -> Unit)? = null,
    content: @Composable BoxScope.(focused: Boolean) -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (focused) 1f else 0f,
        animationSpec = focusSpring,
        label = "focus",
    )
    val shape: Shape = RoundedCornerShape(cornerRadius)
    val innerRing = 3.dpx
    val outerRing = 3.dpx
    val lift = 28.dpx
    val edgeWidth = 1.dpx
    val interactions = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .onFocusChanged {
                if (focused != it.isFocused) {
                    focused = it.isFocused
                    onFocusChange?.invoke(it.isFocused)
                }
            }
            .graphicsLayer {
                val s = 1f + (focusScale - 1f) * progress
                scaleX = s
                scaleY = s
            }
            .shadow(
                elevation = lift * progress.coerceIn(0f, 1f),
                shape = shape,
                clip = false,
                ambientColor = Color.Black,
                spotColor = Color.Black,
            )
            .drawWithContent {
                drawContent()
                val a = progress.coerceIn(0f, 1f)
                if (a > 0f) {
                    val inner = innerRing.toPx()
                    val outer = outerRing.toPx()
                    val r = cornerRadius.toPx()
                    val i = inner / 2f
                    drawRoundRect(
                        color = TvColors.Ground.copy(alpha = a),
                        topLeft = Offset(-i, -i),
                        size = Size(size.width + inner, size.height + inner),
                        cornerRadius = CornerRadius(r + i),
                        style = Stroke(width = inner),
                    )
                    val o = inner + outer / 2f
                    drawRoundRect(
                        color = TvColors.Accent.copy(alpha = a),
                        topLeft = Offset(-o, -o),
                        size = Size(size.width + 2 * o, size.height + 2 * o),
                        cornerRadius = CornerRadius(r + o),
                        style = Stroke(width = outer),
                    )
                }
            }
            .clip(shape)
            .then(if (fill != null) Modifier.background(fill) else Modifier)
            .then(if (edge != null) Modifier.border(edgeWidth, edge, shape) else Modifier)
            .clickable(interactionSource = interactions, indication = null, onClick = onClick),
    ) {
        content(focused)
    }
}
