package com.janoz.rl.statgatherer.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.janoz.rl.statgatherer.util.ColorUtils.Companion.toColor
import com.janoz.rl.statgatherer.util.ColorUtils.Companion.toHex
import org.junit.jupiter.api.Test
import java.awt.Color

class ColorUtilsTest {
    @Test
    fun `color to string`() {
        assertThat(Color(255, 255, 255).toHex()).isEqualTo("#ffffff")
    }

    @Test
    fun `color to string red`() {
        assertThat(Color(255, 0, 0).toHex()).isEqualTo("#ff0000")
    }

    @Test
    fun `color to string with leading zeros`() {
        assertThat(Color(0, 128, 128).toHex()).isEqualTo("#008080")
    }

    @Test
    fun `string to color`() {
        assertThat("#ffffff".toColor()).isEqualTo(Color(255, 255, 255))
    }
}
