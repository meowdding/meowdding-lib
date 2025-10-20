package me.owdding.lib.utils

import com.mojang.brigadier.StringReader
import net.fabricmc.loader.api.Version
import net.fabricmc.loader.api.metadata.version.VersionComparisonOperator
import net.fabricmc.loader.api.metadata.version.VersionPredicate


class VersionInterval(val predicates: List<VersionPredicate>) {
    constructor(vararg predicates: VersionPredicate) : this(listOf(*predicates))

    fun test(version: Version): Boolean {
        if (predicates.isEmpty()) return true
        return predicates.all { it.test(version) }
    }
}

object VersionIntervalParser {
    const val INCLUSIVE_BEGIN = '['
    const val INCLUSIVE_END = ']'
    const val EXCLUSIVE_BEGIN = '('
    const val EXCLUSIVE_END = ')'
    const val DELIMITER = ';'

    var first: VersionPredicate? = null
    var second: VersionPredicate? = null

    fun parse(string: String): VersionInterval {
        with(StringReader(string)) {
            val hasDelimiter = string.contains(DELIMITER)
            val hasBounds = string.contains(Regex("[()\\[\\]]"))

            if (string == "*") return@with

            if (!hasBounds) {
                parseFirst(VersionComparisonOperator.EQUAL, string)
                return@with
            }

            when (read()) {
                INCLUSIVE_END -> {}
                INCLUSIVE_BEGIN -> parseFirst(
                    VersionComparisonOperator.GREATER_EQUAL,
                    readStringUntil(if (hasDelimiter) DELIMITER else INCLUSIVE_BEGIN),
                )

                EXCLUSIVE_BEGIN -> parseFirst(
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
                        INCLUSIVE_END -> parseSecond(VersionComparisonOperator.LESS_EQUAL, readStringUntil(INCLUSIVE_END))
                        EXCLUSIVE_END -> parseSecond(VersionComparisonOperator.LESS, readStringUntil(EXCLUSIVE_END))
                    }
                }
            }
        }

        return when {
            first == null && second == null -> VersionInterval()
            first != null && second != null -> VersionInterval(first!!, second!!)
            first != null && second == null -> VersionInterval(first!!)
            first == null && second != null -> VersionInterval(second!!)
            else -> throw UnsupportedOperationException("Can't happen!")
        }
    }

    private fun parseFirst(operator: VersionComparisonOperator, string: String) {
        first = parseAny(operator, string)
    }

    private fun parseSecond(operator: VersionComparisonOperator, string: String) {
        second = parseAny(operator, string)
    }

    private fun parseAny(operator: VersionComparisonOperator, string: String): VersionPredicate {
        return VersionPredicate.parse("${operator.serialized}$string")
    }
}
