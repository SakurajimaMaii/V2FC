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

package com.v2fc.vastgui.app.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2fc.vastgui.app.ui.theme.V2FCTheme
import com.v2fc.vastgui.components.progress.ArcProgress

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/10/16
// Documentation: https://github.com/SakurajimaMaii/V2FC/wiki/ArcProgress

@Preview(showBackground = true)
@Composable
fun ArcProgressPreview() {
    V2FCTheme {
        // A surface container using the 'background' color from the theme
        var sliderPosition by remember { mutableFloatStateOf(0f) }
        Column {
            ArcProgress(
                radius = 100f.dp,
                width = 10f.dp,
                endPointRadius = 20f.dp,
                modifier = Modifier.padding(20f.dp).size(220.dp),
                currentProgress = sliderPosition
            )
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                modifier = Modifier.padding(horizontal = 20f.dp)
            )
        }
    }
}