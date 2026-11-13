package me.owdding.lib.utils

import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.since
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible
import kotlin.time.Duration
import kotlin.time.Instant

@Suppress("ClassName")
private object UNINITIALIZED_VALUE

public class CachedValue<Type>(private val timeToLive: Duration = Duration.INFINITE, private val supplier: () -> Type) {
    private var value: Any? = UNINITIALIZED_VALUE
    public var lastUpdated: Instant = Instant.DISTANT_PAST
        private set

    public operator fun getValue(thisRef: Any?, property: Any?): Type = getValue()

    public fun getValue(): Type {
        if (!hasValue()) {
            this.value = supplier()
            lastUpdated = currentInstant()
        }
        if (value === UNINITIALIZED_VALUE) throw ClassCastException("Failed to initialize value!")
        return value.unsafeCast()
    }

    public fun hasValue(): Boolean = value !== UNINITIALIZED_VALUE && lastUpdated.since() < timeToLive

    public fun invalidate() {
        value = UNINITIALIZED_VALUE
    }

    public companion object {
        public fun KProperty0<*>.invalidateCache() {
            this.isAccessible = true
            val delegate = this.getDelegate() as? CachedValue<*>
            if (delegate != null) McClient.throwInDev("Delegate for ${this.name} isn't a CachedValue")
            delegate?.invalidate()
        }
    }
}
