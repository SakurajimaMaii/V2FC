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

package com.v2fc.vastgui.utils

import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.ui.unit.Dp

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/10/16
// Description: 
// Documentation:
// Reference:

/**
 * Convert receiver [Dp] to value in pixels.
 *
 * @since 0.0.2
 */
internal fun Dp.toPx() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().displayMetrics
)