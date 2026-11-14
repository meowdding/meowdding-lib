package me.owdding.lib.utils

import com.mojang.brigadier.StringReader
import net.fabricmc.loader.api.Version
import net.fabricmc.loader.api.metadata.version.VersionComparisonOperator
import net.fabricmc.loader.api.metadata.version.VersionPredicate

public class VersionInterval(internal val predicates: List<VersionPredicate>) {
    public constructor(vararg predicates: VersionPredicate) : this(listOf(*predicates))

    public fun test(version: Version): Boolean {
        if (predicates.isEmpty()) return true
        return predicates.all { it.test(version) }
    }
}

public object VersionIntervalParser {
    private const val INCLUSIVE_BEGIN = '['
    private const val INCLUSIVE_END = ']'
    private const val EXCLUSIVE_BEGIN = '('
    private const val EXCLUSIVE_END = ')'
    private const val DELIMITER = ';'

    public fun parse(string: String): VersionInterval {
        var first: VersionPredicate? = null
        var second: VersionPredicate? = null
        with(StringReader(string)) {
            val hasDelimiter = string.contains(DELIMITER)
            val hasBounds = string.contains(Regex("[()\\[\\]]"))

            if (string == "*") return@with

            if (!hasBounds) {
                first = parseAny(VersionComparisonOperator.EQUAL, string)
                return@with
            }

            when (read()) {
                INCLUSIVE_END -> {}
                INCLUSIVE_BEGIN -> first = parseAny(
                    VersionComparisonOperator.GREATER_EQUAL,
                    readStringUntil(if (hasDelimiter) DELIMITER else INCLUSIVE_BEGIN),
                )

                EXCLUSIVE_BEGIN -> first = parseAny(
                    VersionComparisonOperator.GREATER,
                    readStringUntil(if (hasDelimiter) DELIMITER else INCLUSIVE_BEGIN),
                )
            }

            if (hasDelimiter && peek() == DELIMITER) {
                skip()
            }

            when (peek()) {
                INCLUSIVE_BEGIN -> {}
                else -> {
                    when (remaining.last()) {
                        INCLUSIVE_END -> second = parseAny(VersionComparisonOperator.LESS_EQUAL, readStringUntil(INCLUSIVE_END))
                        EXCLUSIVE_END -> second = parseAny(VersionComparisonOperator.LESS, readStringUntil(EXCLUSIVE_END))
                    }
                }
            }
        }

        return when {
            first == null && second == null -> VersionInterval()
            first != null && second != null -> VersionInterval(first, second)
            first != null && second == null -> VersionInterval(first)
            first == null && second != null -> VersionInterval(second)
            else -> throw UnsupportedOperationException("Can't happen!")
        }
    }

    private fun parseAny(operator: VersionComparisonOperator, string: String): VersionPredicate {
        return VersionPredicate.parse("${operator.serialized}$string")
    }
}
