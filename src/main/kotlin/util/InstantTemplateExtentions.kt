package com.janoz.rl.statgatherer.util

import io.quarkus.qute.TemplateExtension
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char
import kotlinx.datetime.offsetIn
import kotlin.time.Instant

@TemplateExtension
object InstantTemplateExtentions {
    val onlyMonthDay =
        DateTimeComponents.Format {
            monthNumber()
            char('-')
            day()
        }
    val onlyHourMinute =
        DateTimeComponents.Format {
            hour()
            char(':')
            minute()
        }

    val tz = TimeZone.of("Europe/Amsterdam")

    @JvmStatic
    fun monthDay(instant: Instant): String = instant.format(onlyMonthDay, instant.offsetIn(tz))

    @JvmStatic
    fun hourMinute(instant: Instant): String = instant.format(onlyHourMinute, instant.offsetIn(tz))
}
