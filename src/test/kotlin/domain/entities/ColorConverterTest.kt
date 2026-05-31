package com.janoz.rl.statgatherer.domain.entities

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import org.junit.jupiter.api.Test
import java.awt.Color

class ColorConverterTest {
    @Test
    fun convertToStringTest() {
        assertThat(ColorConverter().convertToDatabaseColumn(Color(255, 255, 255))).isNotNull().isEqualTo("#ffffff")
        assertThat(ColorConverter().convertToDatabaseColumn(Color(255, 0, 0))).isNotNull().isEqualTo("#ff0000")
        assertThat(ColorConverter().convertToDatabaseColumn(Color(0, 128, 128))).isNotNull().isEqualTo("#008080")
        assertThat(ColorConverter().convertToDatabaseColumn(null)).isNull()
    }

    @Test
    fun convertToColorTest() {
        assertThat(ColorConverter().convertToEntityAttribute("#ffffff")).isNotNull().isEqualTo(Color(255, 255, 255))
        assertThat(ColorConverter().convertToEntityAttribute(null)).isNull()
    }
}
