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

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Build
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.withSave
import kotlin.math.hypot
import kotlin.math.roundToInt

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/10/17
// Description: 
// Documentation:

/**
 * Dark Mask.
 *
 * @param initIsDark True if the system in dark mode,false otherwise.
 * @param onValueChanged Callback when the mode is changed.
 * @param maskCenterX The x coordinate of mask circle center.
 * @param maskCenterY The y coordinate of mask circle center.
 * @param maskAnimationSpec The animation that will be used to change
 * the value of mask circle radius.
 * @param content The content scope.
 * @since 0.0.2
 */
@Composable
fun DarkMask(
    initIsDark: Boolean = isSystemInDarkTheme(),
    onValueChanged: (Boolean) -> Unit = { },
    maskCenterX: Float = 0f,
    maskCenterY: Float = 0f,
    maskAnimationSpec: AnimationSpec<Float> = tween(1000),
    content: @Composable() ((() -> Unit) -> Unit)
) {
    var isAnimRunning by remember { mutableStateOf(false) }
    var isDark by remember { mutableStateOf(initIsDark) }
    val view = LocalView.current.rootView
    var viewBounds by remember { mutableStateOf<Rect?>(null) }
    val (viewSnapshot, updateViewSnapshot) = remember { mutableStateOf<Bitmap?>(null) }
    val maskRadius by animateFloatAsState(
        targetValue = if (isDark) hypot(view.width.toFloat(), view.height.toFloat()) else 0f,
        animationSpec = maskAnimationSpec,
        label = "Mask Animation",
        finishedListener = {
            updateViewSnapshot(null)
            isAnimRunning = false
        }
    )
    val clickEvent: () -> Unit = clickEvent@{
        if (!isAnimRunning) {
            isDark = !isDark
            isAnimRunning = true
            val bounds = viewBounds ?: return@clickEvent
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
                val xfermode = if (isDark)
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
                            if (isDark) {
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
        content(clickEvent)
    }
}