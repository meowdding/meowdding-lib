package me.owdding.lib.utils.config

import com.teamresourceful.resourcefulconfigkt.api.CachedTransformedEntry
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import com.teamresourceful.resourcefulconfigkt.api.ConfigDelegateProvider
import com.teamresourceful.resourcefulconfigkt.api.ObservableEntry
import com.teamresourceful.resourcefulconfigkt.api.RConfigKtEntry
import com.teamresourceful.resourcefulconfigkt.api.TransformedEntry
import com.teamresourceful.resourcefulconfigkt.api.builders.CategoryBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.SeparatorBuilder
import me.owdding.lib.rendering.text.serialization.TagComponentSerialization
import net.minecraft.network.chat.Component

public var SeparatorBuilder.translation: String
    get() = ""
    set(value) {
        this.title = value
        this.description = "$value.desc"
    }

public fun CategoryBuilder.category(category: CategoryKt, init: CategoryKt.() -> Unit) {
    category(category)
    category.init()
}

public fun CategoryBuilder.categories(vararg categories: CategoryKt) { categories.forEach { this@categories.category(it) } }

public fun CategoryBuilder.separator(translation: String): Unit = this.separator { this.translation = translation }

public fun <T, R> ConfigDelegateProvider<RConfigKtEntry<T>>.cachedTransform(from: (R) -> T, to: (T) -> R): CachedTransformedEntry<T, R> =
    CachedTransformedEntry(this, from, to)

public fun <T, R> ConfigDelegateProvider<RConfigKtEntry<T>>.transform(from: (R) -> T, to: (T) -> R): TransformedEntry<T, R> =
    TransformedEntry(this, from, to)

public fun <T> ConfigDelegateProvider<RConfigKtEntry<T>>.observable(onChange: (T, T) -> Unit): ObservableEntry<T> =
    ObservableEntry(this, onChange)

public fun ConfigDelegateProvider<RConfigKtEntry<String>>.cachedTransformPlaceholderComponent(): CachedTransformedEntry<String, Component> =
    CachedTransformedEntry(
        this,
        { TagComponentSerialization.serialize(it) },
        { TagComponentSerialization.deserialize(it) },
    )

public fun ConfigDelegateProvider<RConfigKtEntry<Array<out String>>>.cachedTransformPlaceholderComponents(): CachedTransformedEntry<Array<out String>, List<Component>> =
    CachedTransformedEntry(
        this,
        { list -> list.map { TagComponentSerialization.serialize(it) }.toTypedArray() },
        { list -> list.map { TagComponentSerialization.deserialize(it) } },
    )
