package me.owdding.lib.extensions

import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import kotlin.time.DurationUnit

private val DECIMAL_FORMAT = DecimalFormat("#.##")
fun Number.round(): String = DECIMAL_FORMAT.format(this)

private val COMPACT_NUMBER_FORMAT = NumberFormat.getCompactNumberInstance()
fun Number.shorten(decimalDigits: Int = 1): String = COMPACT_NUMBER_FORMAT.apply { maximumFractionDigits = decimalDigits }.format(this)

fun Duration.toReadableTime(biggestUnit: DurationUnit = DurationUnit.DAYS, maxUnits: Int = 2, allowMs: Boolean = false): String {
    val units = listOfNotNull(
        DurationUnit.DAYS to this.inWholeDays,
        DurationUnit.HOURS to this.inWholeHours % 24,
        DurationUnit.MINUTES to this.inWholeMinutes % 60,
        DurationUnit.SECONDS to this.inWholeSeconds % 60,
        (DurationUnit.MILLISECONDS to this.inWholeMilliseconds % 1000).takeIf { allowMs },
    )

    val unitNames = listOfNotNull(
        DurationUnit.DAYS to "d",
        DurationUnit.HOURS to "h",
        DurationUnit.MINUTES to "min",
        DurationUnit.SECONDS to "s",
        (DurationUnit.MILLISECONDS to "ms").takeIf { allowMs },
    ).toMap()

    val filteredUnits = units.dropWhile { it.first != biggestUnit }
        .filter { it.second > 0 }
        .take(maxUnits)

    return filteredUnits.joinToString(", ") { (unit, value) ->
        "$value${unitNames[unit]}"
    }.ifEmpty { "0 seconds" }
}

private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss")
fun Instant.toReadableString(zoneId: ZoneId = ZoneOffset.systemDefault()): String {
    return DATE_TIME_FORMATTER.format(LocalDateTime.ofInstant(this, zoneId))
}

fun Number.ordinal(): String {
    val value = this.toLong()
    if (value % 100 in 11..13) return "th"
    return when (value % 10) {
        1L -> "st"
        2L -> "nd"
        3L -> "rd"
        else -> "th"
    }
}
