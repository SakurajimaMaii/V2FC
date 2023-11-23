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

package com.v2fc.vastgui.app.ui.component

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.v2fc.vastgui.app.R
import kotlin.math.hypot

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/11/22

@Composable
fun CircleMaskPreview() {
    var size by remember { mutableStateOf(Offset(0f, 0f)) }
    var circleMaskState by remember { mutableStateOf(CircleMaskState.End) }
    val transition = updateTransition(circleMaskState, label = "Circle mask state")
    val radius by transition.animateFloat(label = "Circle mask radius") { state ->
        when (state) {
            CircleMaskState.Start -> hypot(size.x, size.y) * 1.2f
            CircleMaskState.End -> 0f
        }
    }
    val color by transition.animateColor(label = "Circle mask content tint color") { state ->
        when (state) {
            CircleMaskState.Start -> Color.White
            CircleMaskState.End -> Color.Black
        }
    }
    val alpha by transition.animateFloat(label = "Arrow alpha animation") { state ->
        when (state) {
            CircleMaskState.Start -> 1f
            CircleMaskState.End -> 0f
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    circleMaskState = CircleMaskState.End
                })
            },
        contentAlignment = Alignment.Center
    ) {
        var position by remember { mutableStateOf(Offset.Unspecified) }
        val colorStops = arrayOf(0.85f to Color(0xff6c5ce7), 1f to Color.Transparent)
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.technology))
        val dynamicProperties = rememberLottieDynamicProperties(
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR_FILTER,
                value = PorterDuffColorFilter(color.toArgb(), PorterDuff.Mode.SRC),
                keyPath = arrayOf("**")
            )
        )
        ConstraintLayout(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .width(300.dp)
                .wrapContentHeight()
                .background(Color.White)
                .padding(10.dp)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val event: PointerEvent = awaitPointerEvent(PointerEventPass.Initial)
                        val xCoordinate = event.changes[0].position.x
                        val yCoordinate = event.changes[0].position.y
                        val minDistance = listOf(
                            LDistance(xCoordinate),
                            TDistance(yCoordinate),
                            RDistance(size.x - xCoordinate),
                            BDistance(size.y - yCoordinate)
                        ).minBy { it.value }
                        position = when (minDistance) {
                            is LDistance -> Offset(0f, yCoordinate)
                            is TDistance -> Offset(xCoordinate, 0f)
                            is RDistance -> Offset(size.x, yCoordinate)
                            is BDistance -> Offset(xCoordinate, size.y)
                        }
                        circleMaskState = CircleMaskState.Start
                    }
                }
                .drawWithContent {
                    if (position != Offset.Unspecified) {
                        drawCircle(
                            Brush.radialGradient(
                                colorStops = colorStops,
                                center = position,
                                radius = radius.coerceAtLeast(0.01f)
                            ),
                            radius = radius,
                            center = position
                        )
                    }
                    this@drawWithContent.drawContent()
                }
                .onSizeChanged {
                    size = Offset(it.width.toFloat(), it.height.toFloat())
                }
        ) {
            val (title, icon, subTitle, arrow) = createRefs()
            Text(
                text = "Jetpack Compose",
                color = color,
                fontSize = 25.sp,
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(title) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(icon.top, margin = 10.dp)
                    }
            )
            Icon(
                imageVector = Icons.Outlined.ArrowForward,
                contentDescription = null,
                modifier = Modifier
                    .alpha(alpha)
                    .wrapContentSize()
                    .constrainAs(arrow) {
                        top.linkTo(title.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(title.bottom)
                    },
                tint = color
            )
            LottieAnimation(
                composition = composition,
                modifier = Modifier
                    .size(150.dp)
                    .constrainAs(icon) {
                        start.linkTo(parent.start)
                        top.linkTo(title.bottom, margin = 10.dp)
                        end.linkTo(parent.end, margin = 10.dp)
                        bottom.linkTo(subTitle.top)
                    },
                iterations = LottieConstants.IterateForever,
                contentScale = ContentScale.Fit,
                dynamicProperties = dynamicProperties
            )
            Text(
                text = "Jetpack Compose is Android’s recommended modern toolkit for building native UI. " +
                        "It simplifies and accelerates UI development on Android. Quickly bring your app " +
                        "to life with less code, powerful tools, and intuitive Kotlin APIs.",
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(subTitle) {
                        start.linkTo(parent.start)
                        top.linkTo(icon.bottom, margin = 10.dp)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }
    }
}

private enum class CircleMaskState { Start, End }

private sealed class Distance(val value: Float) : Comparable<Float> {
    override fun compareTo(other: Float): Int = (value - other).toInt()
}

private class LDistance(value: Float) : Distance(value)
private class TDistance(value: Float) : Distance(value)
private class RDistance(value: Float) : Distance(value)
private class BDistance(value: Float) : Distance(value)