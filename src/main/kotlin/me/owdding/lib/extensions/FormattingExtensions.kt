package me.owdding.lib.extensions

import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.log10
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.DurationUnit

private val DECIMAL_FORMAT = DecimalFormat("#.##")
fun Number.round(): String = DECIMAL_FORMAT.format(this)

private val decimalFormatCache = ConcurrentHashMap<Int, DecimalFormat>()
private val SUFFIXES = arrayOf("K", "M", "B", "T", "Q")

fun Number.shorten(decimalDigits: Int = 1): String {
    val doubleValue = this.toDouble()
    if (doubleValue < 1000) {
        return this.toString()
    }

    val tier = (log10(doubleValue) / 3).toInt()

    val suffix = SUFFIXES.getOrElse(tier - 1) { SUFFIXES.last() }

    val value = doubleValue / 10.0.pow(tier * 3)

    val formatter = decimalFormatCache.computeIfAbsent(decimalDigits) {
        DecimalFormat().apply {
            maximumFractionDigits = it
            minimumFractionDigits = 0
        }
    }

    return "${formatter.format(value)}$suffix"
}

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
