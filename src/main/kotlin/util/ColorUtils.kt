package com.janoz.rl.statgatherer.util

import java.awt.Color

class ColorUtils {
    companion object {
        fun Color.toHex(): String = String.format("#%06x", 0xFFFFFF and rgb)

        fun String.toColor(): Color = Color(this.takeLast(6).hexToInt())
    }
}
