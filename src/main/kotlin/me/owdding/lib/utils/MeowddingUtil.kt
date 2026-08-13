package me.owdding.lib.utils

import java.util.concurrent.ConcurrentHashMap

object MeowddingUtil {

    fun <First, Second, Third, Result : Any> memoize(function: (First, Second, Third) -> Result): (First, Second, Third) -> Result =
        object : Function3<First, Second, Third, Result> {
            private val cache = ConcurrentHashMap<Triple<First, Second, Third>, Result>()

            override fun invoke(p1: First, p2: Second, p3: Third): Result {
                return this.cache.computeIfAbsent(Triple(p1, p2, p3)) {
                    function.invoke(p1, p2, p3)
                }
            }

            override fun toString(): String {
                return "memoize/2[function=" + function + ", size=" + this.cache.size + "]"
            }
        }

}
