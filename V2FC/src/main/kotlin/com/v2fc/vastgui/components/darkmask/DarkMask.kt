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

package com.v2fc.vastgui.components.darkmask

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Build
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.core.animation.addListener
import androidx.core.graphics.applyCanvas
import kotlin.math.hypot
import kotlin.math.roundToInt

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/10/17
// Documentation: https://github.com/SakurajimaMaii/V2FC/wiki/DarkMask

/**
 * Mask State.
 *
 * @since 0.1.1
 */
enum class MaskState {
    Collapsed,
    Expanded
}

/**
 * Dark Mask.
 *
 * @param initIsDark True if the system in dark mode,false otherwise.
 * @param onValueChanged Callback when the mode is changed.
 * @param maskCenterX The x coordinate of mask circle center.
 * @param maskCenterY The y coordinate of mask circle center.
 * @param maskDuration Sets the length of the mask animation.
 * @param maskInterpolator The time interpolator used in calculating the elapsed fraction of
 * mask animation.
 * @param content The content scope.
 * @since 0.1.1
 */
@Composable
fun DarkMask(
    initIsDark: Boolean = isSystemInDarkTheme(),
    onValueChanged: (Boolean) -> Unit = { },
    maskCenterX: Float = 0f,
    maskCenterY: Float = 0f,
    maskDuration: Long = 1000L,
    maskInterpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
    content: @Composable (((MaskState) -> Unit) -> Unit)
) {
    var isAnimRunning by remember { mutableStateOf(false) }
    var isDark by remember { mutableStateOf(initIsDark) }
    val view = LocalView.current.rootView
    val viewDiagonal = hypot(view.width.toFloat(), view.height.toFloat())
    var viewBounds by remember { mutableStateOf<Rect?>(null) }
    val (viewSnapshot, updateViewSnapshot) = remember { mutableStateOf<Bitmap?>(null) }
    var maskState: MaskState by remember { mutableStateOf(MaskState.Collapsed) }
    var maskRadius by remember { mutableFloatStateOf(0f) }
    val maskActive: (MaskState) -> Unit = maskActive@{
        if (!isAnimRunning) {
            isDark = !isDark
            isAnimRunning = true
            val bounds = viewBounds ?: return@maskActive
            val range = when (it) {
                MaskState.Collapsed -> viewDiagonal to 0f
                MaskState.Expanded -> 0f to viewDiagonal
            }
            val image = Bitmap
                .createBitmap(
                    bounds.width.roundToInt(),
                    bounds.height.roundToInt(),
                    Bitmap.Config.ARGB_8888
                )
                .applyCanvas {
                    translate(-bounds.left, -bounds.top)
                    view.draw(this)
                }
            updateViewSnapshot(image)
            onValueChanged(isDark)
            maskState = it
            ValueAnimator.ofFloat(range.first, range.second).apply {
                this.duration = maskDuration
                this.interpolator = maskInterpolator
                addUpdateListener { valueAnimator ->
                    maskRadius = valueAnimator.animatedValue as Float
                }
                addListener(onEnd = {
                    updateViewSnapshot(null)
                    isAnimRunning = false
                })
            }.start()
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                viewBounds = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.boundsInWindow()
                } else {
                    it.boundsInRoot()
                }
            }
            .drawWithCache {
                val xfermode = if (maskState == MaskState.Expanded)
                    PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                else
                    PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                onDrawWithContent {
                    clipRect {
                        this@onDrawWithContent.drawContent()
                    }
                    with(drawContext.canvas.nativeCanvas) {
                        if (viewSnapshot != null) {
                            val checkPoint = saveLayer(null, null)
                            if (maskState == MaskState.Expanded) {
                                drawBitmap(viewSnapshot, 0f, 0f, null)
                                paint.xfermode = xfermode
                                drawCircle(maskCenterX, maskCenterY, maskRadius, paint)
                                paint.xfermode = null
                            } else {
                                drawCircle(maskCenterX, maskCenterY, maskRadius, paint)
                                paint.xfermode = xfermode
                                drawBitmap(viewSnapshot, 0f, 0f, paint)
                                paint.xfermode = null
                            }
                            restoreToCount(checkPoint)
                        }
                    }
                }
            }
    ) {
        content(maskActive)
    }
}