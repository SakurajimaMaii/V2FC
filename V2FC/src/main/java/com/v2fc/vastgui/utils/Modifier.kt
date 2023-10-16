package com.v2fc.vastgui.utils

import androidx.compose.ui.Modifier

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2023/10/16
// Description: 
// Documentation:
// Reference:

/**
 * Receiver [Modifier] will call [modifierFunction] if [condition] is true.
 *
 * @since 0.0.2
 */
internal fun Modifier.applyIf(
    condition: Boolean,
    modifierFunction: Modifier.() -> Modifier,
) = this.run {
    if (condition) {
        this.modifierFunction()
    } else {
        this
    }
}