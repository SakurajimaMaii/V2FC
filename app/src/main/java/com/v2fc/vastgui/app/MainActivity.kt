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

package com.v2fc.vastgui.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.v2fc.vastgui.app.ui.theme.V2FCTheme
import com.v2fc.vastgui.components.avatargroup.Avatar
import com.v2fc.vastgui.components.avatargroup.AvatarGroup
import com.v2fc.vastgui.components.avatargroup.AvatarSize
import com.v2fc.vastgui.components.avatargroup.OVERLAP_END
import com.v2fc.vastgui.components.progress.ArcProgress
import com.v2fc.vastgui.components.progress.ArcProgressDefaults

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            V2FCTheme {
                val colorStops = arrayOf(
                    0.0f to Color.Yellow,
                    0.2f to Color.Red,
                    1f to Color.Blue
                )
                // A surface container using the 'background' color from the theme
                var sliderPosition by remember { mutableFloatStateOf(0f) }
                var progressPosition by remember { mutableFloatStateOf(0f) }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(text = "$progressPosition")
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
                            currentProgress = sliderPosition
                        ) { progressPosition = it }
                        Slider(
                            value = sliderPosition,
                            onValueChange = {
                                sliderPosition = it
                            },
                            modifier = Modifier.padding(horizontal = 20f.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    V2FCTheme {

    }
}