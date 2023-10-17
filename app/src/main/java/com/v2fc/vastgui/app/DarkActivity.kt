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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.v2fc.vastgui.app.ui.theme.V2FCTheme
import com.v2fc.vastgui.components.darkmask.DarkMask

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/10/17
// Description: 
// Documentation:
// Reference:

class DarkActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDark by remember { mutableStateOf(false) }
            V2FCTheme(isDark) {
                DarkMask(
                    change = isDark,
                    icon = {
                        Icon(painter = painterResource(id = R.drawable.ic_sun), contentDescription = null, tint = Color.Gray)
                    },
                    iconLeftTop = Offset(20f, 20f),
                    onValueChanged = {
                        isDark = it
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        (0..15).forEach {
                            ListItem(
                                headlineContent = { Text(text = "这是第 $it 条数据") },
                            )
                        }
                    }
                }
            }
        }
    }
}

