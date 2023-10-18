/*
 * Copyright 2023 VastGui guihy2019@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.v2fc.vastgui.components.progress

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import java.text.DecimalFormat
import kotlin.math.cos
import kotlin.math.sin

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/10/15
// Documentation: https://github.com/SakurajimaMaii/V2FC/wiki/ArcProgress

/**
 * Arc Progress Colors.
 *
 * @property progress Color of the progress.
 * @property background Color of the progress background.
 * @property startPoint Color of the start-point circle.
 * @property endPoint Color of the end-point circle.
 * @since 0.0.1
 */
class ArcProgressColors internal constructor(
    internal val progress: Brush,
    internal val background: Color,
    internal val startPoint: Color,
    internal val endPoint: Color
)

/**
 * Arc Progress Text Style.
 *
 * @property text The display text in the endpoint circle of the progress.
 * @property size The size of the text.
 * @property color The color of the text.
 * @since 0.0.1
 */
class ArcProgressText internal constructor(
    internal val text: String,
    internal val size: TextUnit,
    internal val color: Color
)

/**
 * Object to hold defaults used by [ArcProgress].
 *
 * @since 0.0.1
 */
@Stable
object ArcProgressDefaults {
    /**
     * Creates a [ArcProgressColors] that represents the colors
     * used in parts of the [ArcProgress].
     *
     * @see ArcProgressColors
     * @since 0.0.1
     */
    @Composable
    fun colors(
        progress: Color = MaterialTheme.colorScheme.primary,
        background: Color = MaterialTheme.colorScheme.primaryContainer,
        startPoint: Color = MaterialTheme.colorScheme.primary,
        endPoint: Color = MaterialTheme.colorScheme.primary
    ): ArcProgressColors = ArcProgressColors(
        Brush.horizontalGradient(listOf(progress, progress)),
        background,
        startPoint,
        endPoint
    )

    /**
     * Creates a [ArcProgressColors] that represents the colors
     * used in parts of the [ArcProgress].
     *
     * @see ArcProgressColors
     * @since 0.0.1
     */
    @Composable
    fun brush(
        progress: Brush = Brush.horizontalGradient(
            listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primary
            )
        ),
        background: Color = MaterialTheme.colorScheme.primaryContainer,
        startPoint: Color = MaterialTheme.colorScheme.primary,
        endPoint: Color = MaterialTheme.colorScheme.primary
    ): ArcProgressColors = ArcProgressColors(
        progress, background, startPoint, endPoint
    )

    /**
     * Creates a [ArcProgressText] that represents the text
     * used in parts of the [ArcProgress].
     *
     * @see ArcProgressText
     * @since 0.0.1
     */
    @Composable
    fun text(
        text: String = "",
        size: TextUnit = 12f.sp,
        color: Color = Color.White
    ): ArcProgressText = ArcProgressText(text, size, color)
}

/**
 * Arc Progress. Click
 * [Link](https://ave.entropy2020.cn/documents/VastTools/core-topics/ui/progress/arc-progress-view/)
 * to see the example.
 *
 * @param radius The radius of the progress.
 * @param width The width of the progress.
 * @param endPointRadius The radius of the circle at the end of the
 * progress.
 * @param modifier The [Modifier] to be applied to this progress.
 * @param colors [ArcProgressColors] that will be used to resolve the
 * colors used for this progress.
 * @param text The display text in the endpoint circle.
 * @param currentProgress Current progress from 0 to 1.
 * @param onValueChange Called when progress value has changed.
 * @since 0.0.1
 */
@Composable
fun ArcProgress(
    @FloatRange(from = 0.0) radius: Dp,
    @FloatRange(from = 0.0) width: Dp,
    @FloatRange(from = 0.0) endPointRadius: Dp,
    modifier: Modifier = Modifier,
    colors: ArcProgressColors = ArcProgressDefaults.colors(),
    text: ArcProgressText = ArcProgressDefaults.text(),
    @FloatRange(from = 0.0, to = 1.0) currentProgress: Float = 0f,
    onValueChange: (Float) -> Unit = { _ -> }
) {
    val textMeasurer = rememberTextMeasurer()
    val displayText = text.text.ifEmpty {
        DecimalFormat("##0%").format(currentProgress)
    }
    val measuredText =
        textMeasurer.measure(
            text = displayText,
            style = TextStyle(fontSize = text.size, color = text.color),
            maxLines = 1
        )
    val progress by rememberUpdatedState<(Float) -> Unit> {
        onValueChange(it.coerceIn(0f, 1f))
    }
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = this@Canvas.size.width
            val canvasHeight = this@Canvas.size.height
            val angle = currentProgress * 360f
            progress.invoke(currentProgress)
            drawCircle(
                color = colors.background,
                radius = radius.toPx(),
                center = Offset(canvasWidth / 2f, canvasHeight / 2f),
                style = Stroke(width.toPx())
            )
            drawArc(
                brush = colors.progress,
                startAngle = -90f,
                sweepAngle = angle,
                useCenter = false,
                topLeft = Offset(
                    canvasWidth / 2f - radius.toPx(),
                    canvasWidth / 2f - radius.toPx()
                ),
                size = this.size.copy((2f * radius).toPx(), (2f * radius).toPx()),
                style = Stroke(width = width.toPx())
            )
            drawCircle(
                color = colors.startPoint,
                radius = (width / 2f).toPx(),
                center = Offset(canvasWidth / 2f, canvasHeight / 2f - radius.toPx()),
                blendMode = BlendMode.SrcAtop
            )
            val x1: Float = canvasWidth / 2f - radius.toPx() * cos((angle + 90) * 3.14f / 180f)
            val y1: Float = canvasHeight / 2f - radius.toPx() * sin((angle + 90) * 3.14f / 180f)
            val epRadius = endPointRadius.coerceAtLeast(width / 2f)
            drawCircle(
                color = colors.endPoint,
                radius = epRadius.toPx(),
                center = Offset(x1, y1),
                blendMode = BlendMode.SrcAtop
            )
            val textSize = measuredText.size
            drawText(
                textLayoutResult = measuredText,
                topLeft = Offset(
                    x1 - textSize.width / 2f,
                    y1 - textSize.height / 2f
                )
            )
        }
    }
}