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
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2fc.vastgui.app.ui.theme.V2FCTheme
import com.v2fc.vastgui.components.darkmask.DarkMask
import com.v2fc.vastgui.components.darkmask.MaskState
import kotlinx.coroutines.launch

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/10/17
// Documentation: https://github.com/SakurajimaMaii/V2FC/wiki/DarkMask

class DarkActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            var isDark by remember { mutableStateOf(false) }
            var iconX by remember { mutableFloatStateOf(0f) }
            var iconY by remember { mutableFloatStateOf(0f) }
            V2FCTheme(isDark) {
                DarkMask(maskCenterX = iconX, maskCenterY = iconY, onValueChanged = {
                    isDark = it
                }) { maskActive ->
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        "抽屉标题",
                                        modifier = Modifier
                                            .weight(1f)
                                            .align(Alignment.CenterVertically),
                                        fontSize = 32f.sp
                                    )
                                    IconButton(
                                        onClick = { maskActive(MaskState.Collapsed) },
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .onGloballyPositioned { coordinates ->
                                                iconX = coordinates.boundsInRoot().center.x
                                                iconY = coordinates.boundsInRoot().center.y
                                            }
                                    ) {
                                        MaskIcon(isDark)
                                    }
                                }
                                Divider()
                                NavigationDrawerItem(
                                    label = { Text(text = "抽屉选项") },
                                    selected = false,
                                    onClick = { }
                                )
                            }
                        },
                    ) {
                        Scaffold(
                            floatingActionButton = {
                                ExtendedFloatingActionButton(
                                    text = { Text("打开抽屉") },
                                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                                    onClick = {
                                        scope.launch {
                                            drawerState.apply {
                                                if (isClosed) open() else close()
                                            }
                                        }
                                    }
                                )
                            }
                        ) { contentPadding ->
                            ListContent(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(contentPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MaskIcon(isDark: Boolean = isSystemInDarkTheme()) {
    Crossfade(targetState = isDark, label = "Icon Animation") { isChecked ->
        if (isChecked) {
            Icon(
                painter = painterResource(id = R.drawable.ic_sun),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_moon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun ListContent(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
    ) {
        (0..20).forEach {
            item {
                ListItem(
                    headlineContent = { Text(text = "这是第 $it 条数据") },
                )
            }
        }
    }
}