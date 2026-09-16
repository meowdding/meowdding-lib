package me.owdding.lib.utils.config

import com.teamresourceful.resourcefulconfigkt.api.CachedTransformedEntry
import com.teamresourceful.resourcefulconfigkt.api.CategoryKt
import com.teamresourceful.resourcefulconfigkt.api.ConfigDelegateProvider
import com.teamresourceful.resourcefulconfigkt.api.ObservableEntry
import com.teamresourceful.resourcefulconfigkt.api.RConfigKtEntry
import com.teamresourceful.resourcefulconfigkt.api.TransformedEntry
import com.teamresourceful.resourcefulconfigkt.api.builders.CategoryBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.SeparatorBuilder

var SeparatorBuilder.translation: String
    get() = ""
    set(value) {
        this.title = value
        this.description = "$value.desc"
    }

fun CategoryBuilder.category(category: CategoryKt, init: CategoryKt.() -> Unit) {
    category(category)
    category.init()
}

fun CategoryBuilder.categories(vararg categories: CategoryKt) { categories.forEach { this@categories.category(it) } }

fun CategoryBuilder.separator(translation: String) = this.separator { this.translation = translation }

fun <T, R> ConfigDelegateProvider<RConfigKtEntry<T>>.cachedTransform(from: (R) -> T, to: (T) -> R) = CachedTransformedEntry(this, from, to)

fun <T, R> ConfigDelegateProvider<RConfigKtEntry<T>>.transform(from: (R) -> T, to: (T) -> R) = TransformedEntry(this, from, to)

fun <T> ConfigDelegateProvider<RConfigKtEntry<T>>.observable(onChange: (T, T) -> Unit) = ObservableEntry(this, onChange)
