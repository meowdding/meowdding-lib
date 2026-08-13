package me.owdding.lib.utils

import java.util.concurrent.ConcurrentHashMap

object MeowddingUtil {
    data class Holder<Type>(val value: Type)

    fun <Arg : Any, Result : Any> memoize(function: (Arg) -> Result): (Arg) -> Result =
        object : Function1<Arg, Result> {
            private val cache = ConcurrentHashMap<Holder<Arg>, Holder<Result>>()

            override fun invoke(p1: Arg): Result {
                return this.cache.computeIfAbsent(Holder(p1)) {
                    Holder(function.invoke(it.value))
                }.value
            }

            override fun toString(): String {
                return "memoize/1[function=" + function + ", size=" + this.cache.size + "]"
            }
        }

    fun <First, Second, Result> memoize(function: (First, Second) -> Result): (First, Second) -> Result =
        object : Function2<First, Second, Result> {
            private val cache = ConcurrentHashMap<Pair<First, Second>, Holder<Result>>()

            override fun invoke(p1: First, p2: Second): Result {
                return this.cache.computeIfAbsent(Pair(p1, p2)) { (p1, p2) ->
                    Holder(function.invoke(p1, p2))
                }.value
            }

            override fun toString(): String {
                return "memoize/2[function=" + function + ", size=" + this.cache.size + "]"
            }
        }

    fun <First, Second, Third, Result> memoize(function: (First, Second, Third) -> Result): (First, Second, Third) -> Result =
        object : Function3<First, Second, Third, Result> {
            private val cache = ConcurrentHashMap<Triple<First, Second, Third>, Holder<Result>>()

            override fun invoke(p1: First, p2: Second, p3: Third): Result {
                return this.cache.computeIfAbsent(Triple(p1, p2, p3)) { (p1, p2, p3) ->
                    Holder(function.invoke(p1, p2, p3))
                }.value
            }

            override fun toString(): String {
                return "memoize/3[function=" + function + ", size=" + this.cache.size + "]"
            }
        }

    private data class Quadruple<First, Second, Third, Fourth>(
        val p1: First,
        val p2: Second,
        val p3: Third,
        val p4: Fourth,
    )

    fun <First, Second, Third, Fourth, Result> memoize(function: (First, Second, Third, Fourth) -> Result): (First, Second, Third, Fourth) -> Result =
        object : Function4<First, Second, Third, Fourth, Result> {
            private val cache = ConcurrentHashMap<Quadruple<First, Second, Third, Fourth>, Holder<Result>>()

            override fun invoke(p1: First, p2: Second, p3: Third, p4: Fourth): Result {
                return this.cache.computeIfAbsent(Quadruple(p1, p2, p3, p4)) { (p1, p2, p3, p4) ->
                    Holder(function.invoke(p1, p2, p3, p4))
                }.value
            }

            override fun toString(): String {
                return "memoize/4[function=" + function + ", size=" + this.cache.size + "]"
            }
        }

    private data class Quintuple<First, Second, Third, Fourth, Fifth>(
        val p1: First,
        val p2: Second,
        val p3: Third,
        val p4: Fourth,
        val p5: Fifth,
    )

    fun <First, Second, Third, Fourth, Fifth, Result> memoize(function: (First, Second, Third, Fourth, Fifth) -> Result): (First, Second, Third, Fourth, Fifth) -> Result =
        object : Function5<First, Second, Third, Fourth, Fifth, Result> {
            private val cache = ConcurrentHashMap<Quintuple<First, Second, Third, Fourth, Fifth>, Holder<Result>>()

            override fun invoke(p1: First, p2: Second, p3: Third, p4: Fourth, p5: Fifth): Result {
                return this.cache.computeIfAbsent(Quintuple(p1, p2, p3, p4, p5)) { (p1, p2, p3, p4, p5) ->
                    Holder(function.invoke(p1, p2, p3, p4, p5))
                }.value
            }

            override fun toString(): String {
                return "memoize/5[function=" + function + ", size=" + this.cache.size + "]"
            }
        }

    private data class Sextuple<First, Second, Third, Fourth, Fifth, Sixth>(
        val p1: First,
        val p2: Second,
        val p3: Third,
        val p4: Fourth,
        val p5: Fifth,
        val p6: Sixth,
    )

    fun <First, Second, Third, Fourth, Fifth, Sixth, Result> memoize(function: (First, Second, Third, Fourth, Fifth, Sixth) -> Result): (First, Second, Third, Fourth, Fifth, Sixth) -> Result =
        object : Function6<First, Second, Third, Fourth, Fifth, Sixth, Result> {
            private val cache = ConcurrentHashMap<Sextuple<First, Second, Third, Fourth, Fifth, Sixth>, Holder<Result>>()

            override fun invoke(p1: First, p2: Second, p3: Third, p4: Fourth, p5: Fifth, p6: Sixth): Result {
                return this.cache.computeIfAbsent(Sextuple(p1, p2, p3, p4, p5, p6)) { (p1, p2, p3, p4, p5, p6) ->
                    Holder(function.invoke(p1, p2, p3, p4, p5, p6))
                }.value
            }

            override fun toString(): String {
                return "memoize/6[function=" + function + ", size=" + this.cache.size + "]"
            }
        }

}
