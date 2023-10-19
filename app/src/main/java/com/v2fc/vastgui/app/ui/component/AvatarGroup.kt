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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.v2fc.vastgui.app.ui.theme.V2FCTheme
import com.v2fc.vastgui.components.avatargroup.Avatar
import com.v2fc.vastgui.components.avatargroup.AvatarGroup

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/10/16
// Documentation: https://github.com/SakurajimaMaii/V2FC/wiki/AvatarGroup

@Preview(showBackground = true)
@Composable
fun AvatarGroupPreview() {
    V2FCTheme {
        AvatarGroup(
            modifier = Modifier.wrapContentSize().background(Color.Gray).padding(10f.dp),
            overlapDistance = 20f.dp
        ) { distance ->
            (0..3).forEach {
                Avatar(cropped = it != 3, overlapDistance = distance) { modifier ->
                    AsyncImage(
                        modifier = modifier,
                        model = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSjhxEeGeg72g5XeSWq78kszW1jGLi1FKKcLQ&usqp=CAU",
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
    }
}