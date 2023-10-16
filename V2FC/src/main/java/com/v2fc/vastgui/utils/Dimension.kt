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