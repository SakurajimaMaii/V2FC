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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.IconButton
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
import com.v2fc.vastgui.utils.toPx
import kotlin.math.roundToInt

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/10/17
// Description: 
// Documentation:
// Reference:

/**
 * Dark Mask.
 *
 * @since 0.0.2
 */
@Composable
fun DarkMask(
    change: Boolean = false,
    icon: @Composable () -> Unit,
    iconLeftTop: Offset = Offset.Zero,
    onValueChanged: (Boolean) -> Unit = { },
    onBitmap: (Bitmap) -> Unit = {},
    scope: @Composable() (ConstraintLayoutScope.() -> Unit)
) {
    val (bitmap, updateBitmap) = remember { mutableStateOf<Bitmap?>(null) }
    val radius by animateFloatAsState(
        targetValue = if (change) 1000f.dp.toPx() else 0f,
        animationSpec = tween(1000),
        label = "Ripple Animation",
        finishedListener = {
            updateBitmap(null)
        }
    )
    val view = LocalView.current.rootView
    var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }
    var changeDark by remember { mutableStateOf(change) }
    var iconX by remember { mutableFloatStateOf(0f) }
    var iconY by remember { mutableFloatStateOf(0f) }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                capturingViewBounds = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.boundsInWindow()
                } else {
                    it.boundsInRoot()
                }
            }
            .drawWithCache {
                val xfermode = if (change)
                    PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                else
                    PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                onDrawWithContent {
                    clipRect {
                        this@onDrawWithContent.drawContent()
                    }
                    with(drawContext.canvas.nativeCanvas) {
                        if (bitmap != null) {
                            val checkPoint = saveLayer(null, null)
                            if (change) {
                                drawBitmap(bitmap, 0f, 0f, null)
                                paint.xfermode = xfermode
                                drawCircle(iconX, iconY, radius, paint)
                                paint.xfermode = null
                            } else {
                                drawCircle(iconX, iconY, radius, paint)
                                paint.xfermode = xfermode
                                drawBitmap(bitmap, 0f, 0f, paint)
                                paint.xfermode = null
                            }
                            restoreToCount(checkPoint)
                        }
                    }
                }
            }
    ) {
        val mIcon = createRef()
        scope()
        IconButton(
            onClick = {
                changeDark = !changeDark
                val bounds = capturingViewBounds ?: return@IconButton
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
                updateBitmap(image)
                onValueChanged(changeDark)
                onBitmap(image)
            },
            modifier = Modifier
                .constrainAs(mIcon) {
                    start.linkTo(parent.start, margin = iconLeftTop.x.dp)
                    top.linkTo(parent.top, margin = iconLeftTop.y.dp)
                }
                .onGloballyPositioned { coordinates ->
                    iconX = coordinates.boundsInParent().center.x
                    iconY = coordinates.boundsInParent().center.y
                }
        ) {
            icon()
        }
    }
}