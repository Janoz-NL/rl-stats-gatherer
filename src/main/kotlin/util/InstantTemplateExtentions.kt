package com.janoz.rl.statgatherer.util

import io.quarkus.qute.TemplateExtension
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.offsetIn
import kotlin.time.Instant

@TemplateExtension
object InstantTemplateExtentions {
    @OptIn(FormatStringsInDatetimeFormats::class)
    val onlyMonthDay =
        DateTimeComponents.Format {
            byUnicodePattern("MM-dd")
        }

    val tz = TimeZone.of("Europe/Amsterdam")

    @JvmStatic
    fun monthDay(instant: Instant): String = instant.format(onlyMonthDay, instant.offsetIn(tz))
}
