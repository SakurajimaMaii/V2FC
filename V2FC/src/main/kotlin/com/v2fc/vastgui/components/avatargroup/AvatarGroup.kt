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

package com.v2fc.vastgui.components.avatargroup

import androidx.annotation.IntDef
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.v2fc.vastgui.utils.applyIf
import com.v2fc.vastgui.utils.toPx

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/10/16
// Documentation: https://github.com/SakurajimaMaii/V2FC/wiki/AvatarGroup
// Reference: https://blog.csdn.net/u011897062/article/details/129821121

internal val DEFAULT_MARGIN = 4f.dp
internal val DEFAULT_OVERLAP_DISTANCE = 10f.dp

const val OVERLAP_NONE = 1
const val OVERLAP_START = 2
const val OVERLAP_END = 3

/**
 * The coverage distance of the avatars.
 *
 * @since 0.0.2
 */
@IntDef(flag = true, value = [OVERLAP_NONE, OVERLAP_START, OVERLAP_END])
@Retention(AnnotationRetention.SOURCE)
annotation class OverlapFrom

/**
 * Avatar Size.
 *
 * @since 0.0.2
 */
enum class AvatarSize(val value: Dp) {
    Extra_extra_small(20f.dp),
    Extra_small(24f.dp),
    Small(32f.dp),
    Default(40f.dp),
    Medium(48f.dp),
    Large(72f.dp),
    Extra_large(128f.dp)
}

/**
 * Avatar Group.
 *
 * Here is an example of usage.
 *
 * ```kotlin
 * AvatarGroup(overlapDistance = 20f.dp) { distance ->
 *     (0..3).forEach {
 *         Avatar(cropped = it != 3, overlapDistance = distance) { modifier ->
 *             AsyncImage(
 *                 modifier = modifier,
 *                 model = "https://avatars.githubusercontent.com/u/46998172?v=4",
 *                 contentDescription = null,
 *                 contentScale = ContentScale.Crop,
 *             )
 *         }
 *     }
 * }
 * ```
 * @param overlapDistance The coverage distance of the avatars.
 * @since 0.0.2
 */
@Composable
fun AvatarGroup(
    modifier: Modifier = Modifier,
    overlapDistance: Dp = DEFAULT_OVERLAP_DISTANCE,
    scope: @Composable RowScope.(Dp) -> Unit
) {
    val distance = overlapDistance.coerceAtLeast(0f.dp)
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(-distance)) {
        scope(distance)
    }
}

/**
 * Avatar.
 *
 * @param size The size of the avatars with size.
 * @param margin The margin width of the avatar image.
 * @param overlapFrom The coverage direction of the avatars.
 * @param overlapDistance The coverage distance of the avatars.
 * @param cropped True if crop the avatar, false otherwise.
 * @param source The Image resources.
 * @since 0.0.2
 */
@Composable
fun Avatar(
    size: AvatarSize = AvatarSize.Large,
    margin: Dp = DEFAULT_MARGIN,
    @OverlapFrom overlapFrom: Int = OVERLAP_START,
    overlapDistance: Dp = DEFAULT_OVERLAP_DISTANCE,
    cropped: Boolean = false,
    source: @Composable (Modifier) -> Unit = { _ -> }
) {
    val avatarModifier = Modifier
        .clip(CircleShape)
        .size(size.value)
        .aspectRatio(1f)
        .applyIf(cropped) {
            circleMask(margin.toPx(), overlapFrom, overlapDistance.toPx())
        }
    source(avatarModifier)
}

/**
 * Circle Mask of avatar.
 *
 * @param margin The margin width of the avatar image.
 * @param overlapFrom The coverage direction of the avatars.
 * @param overlapDistance The coverage distance of the avatars.
 * @since 0.0.2
 */
internal fun Modifier.circleMask(
    margin: Float,
    @OverlapFrom overlapFrom: Int,
    overlapDistance: Float
) = this
    .graphicsLayer {
        // Ensure BlendMode.Clear strategy works
        compositingStrategy = CompositingStrategy.Offscreen
    }
    .drawWithCache {
        val path = Path().apply {
            addOval(
                Rect(
                    topLeft = Offset.Zero,
                    bottomRight = Offset(size.width, size.height),
                ),
            )
        }
        onDrawWithContent {
            clipPath(path) {
                // this draws the actual image
                // if you don't call drawContent, it won't render anything
                this@onDrawWithContent.drawContent()
            }
            val marginRadius = size.width / 2f + margin
            val offset = when (overlapFrom) {
                OVERLAP_START -> size.width * 1.5f - overlapDistance
                OVERLAP_END -> -size.width / 2f + overlapDistance
                else -> size.width / 2f
            }
            drawCircle(
                color = Color.Green,
                radius = marginRadius,
                center = Offset(x = offset, y = size.height / 2f),
                blendMode = BlendMode.Clear,
            )
        }
    }