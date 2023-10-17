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

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2fc.vastgui.app.ui.theme.V2FCTheme
import com.v2fc.vastgui.components.progress.ArcProgress
import com.v2fc.vastgui.components.progress.ArcProgressDefaults

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/10/16
// Description: 
// Documentation:
// Reference:

@Preview(showBackground = true)
@Composable
fun ArcProgressPreview(){
    V2FCTheme {
        val colorStops = arrayOf(
            0.0f to Color.Yellow,
            0.2f to Color.Red,
            1f to Color.Blue
        )
        ArcProgress(
            radius = 100f.dp,
            width = 10f.dp,
            endPointRadius = 20f.dp,
            modifier = Modifier
                .padding(20f.dp)
                .size(220.dp),
            colors = ArcProgressDefaults.brush(
                Brush.horizontalGradient(colorStops = colorStops)
            ),
            currentProgress = 0f
        ) {  }
    }
}