package com.janoz.rl.statgatherer.domain.entities

import com.janoz.rl.statgatherer.util.ColorUtils.Companion.toColor
import com.janoz.rl.statgatherer.util.ColorUtils.Companion.toHex
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.awt.Color

@Converter(autoApply = true)
class ColorConverter : AttributeConverter<Color, String> {
    override fun convertToDatabaseColumn(color: Color?): String? {
        if (color == null) return null
        return color.toHex()
    }

    override fun convertToEntityAttribute(colorStr: String?): Color? {
        if (colorStr == null) return null
        return colorStr.toColor()
    }
}
