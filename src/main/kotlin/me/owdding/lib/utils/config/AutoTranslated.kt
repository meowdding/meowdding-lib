package me.owdding.lib.utils.config

import com.teamresourceful.resourcefulconfigkt.api.Entry
import com.teamresourceful.resourcefulconfigkt.api.builders.ButtonBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.ColorBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.DraggableBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.EntriesBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.KeyBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.NumberBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.SelectBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.SeparatorBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.StringBuilder
import com.teamresourceful.resourcefulconfigkt.api.builders.TypeBuilder
import tech.thatgravyboat.skyblockapi.utils.extentions.capitalize
import tech.thatgravyboat.skyblockapi.utils.extentions.toSnakeCase

@Suppress("unused")
public interface AutoTranslated {
    @Suppress("EnumEntryName")
    public enum class StringStyle(public val modify: (String) -> String) {
        // TODO: make a toCamelCase function
        CamelCase({ str ->
            str.toSnakeCase().split("_").joinToString("") { it.capitalize() }.replaceFirstChar(Char::lowercaseChar)
        }),
        snake_case({ it.toSnakeCase() }),
        NORMAL({ it }),
    }

    public val translationBase: String

    public val stringStyle: StringStyle
        get() = StringStyle.snake_case

    public fun makeTranslation(id: String): String =
        listOf(translationBase, stringStyle.modify(id))
            .filter { it.isNotEmpty() }
            .joinToString(".")
            .replace("-", ".")

    context(entryBuilder: EntriesBuilder)
    public fun TypeBuilder.makeTranslations(): Unit {
        this.translation = makeTranslation(id)
    }

    context(entryBuilder: EntriesBuilder)
    public fun <Type : TypeBuilder> wrap(builder: (Type) -> Unit): (Type) -> Unit = {
        it.makeTranslations()
        builder(it)
    }

    context(entryBuilder: EntriesBuilder)
    public fun autoByte(
        value: Byte,
        builder: NumberBuilder<Byte>.() -> Unit = {},
    ): Entry<Byte, NumberBuilder<Byte>> =
        entryBuilder.byte(value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoByte(
        id: String,
        value: Byte,
        builder: NumberBuilder<Byte>.() -> Unit = {},
    ): Entry<Byte, NumberBuilder<Byte>> =
        entryBuilder.byte(id, value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoBytes(
        vararg value: Byte,
        builder: NumberBuilder<Byte>.() -> Unit = {},
    ): Entry<ByteArray, NumberBuilder<Byte>> =
        entryBuilder.bytes(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoBytes(
        id: String,
        vararg value: Byte,
        builder: NumberBuilder<Byte>.() -> Unit = {},
    ): Entry<ByteArray, NumberBuilder<Byte>> =
        entryBuilder.bytes(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoShort(
        value: Short,
        builder: NumberBuilder<Short>.() -> Unit = {},
    ): Entry<Short, NumberBuilder<Short>> =
        entryBuilder.short(value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoShort(
        id: String,
        value: Short,
        builder: NumberBuilder<Short>.() -> Unit = {},
    ): Entry<Short, NumberBuilder<Short>> =
        entryBuilder.short(id, value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoShorts(
        vararg value: Short,
        builder: NumberBuilder<Short>.() -> Unit = {},
    ): Entry<ShortArray, NumberBuilder<Short>> =
        entryBuilder.shorts(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoShorts(
        id: String,
        vararg value: Short,
        builder: NumberBuilder<Short>.() -> Unit = {},
    ): Entry<ShortArray, NumberBuilder<Short>> =
        entryBuilder.shorts(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoInt(
        value: Int,
        builder: NumberBuilder<Int>.() -> Unit = {},
    ): Entry<Int, NumberBuilder<Int>> =
        entryBuilder.int(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoInt(
        id: String,
        value: Int,
        builder: NumberBuilder<Int>.() -> Unit = {},
    ): Entry<Int, NumberBuilder<Int>> =
        entryBuilder.int(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoInts(
        vararg value: Int,
        builder: NumberBuilder<Int>.() -> Unit = {},
    ): Entry<IntArray, NumberBuilder<Int>> =
        entryBuilder.ints(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoInts(
        id: String,
        vararg value: Int,
        builder: NumberBuilder<Int>.() -> Unit = {},
    ): Entry<IntArray, NumberBuilder<Int>> =
        entryBuilder.ints(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoLong(
        value: Long,
        builder: NumberBuilder<Long>.() -> Unit = {},
    ): Entry<Long, NumberBuilder<Long>> =
        entryBuilder.long(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoLong(
        id: String,
        value: Long,
        builder: NumberBuilder<Long>.() -> Unit = {},
    ): Entry<Long, NumberBuilder<Long>> =
        entryBuilder.long(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoLongs(
        vararg value: Long,
        builder: NumberBuilder<Long>.() -> Unit = {},
    ): Entry<LongArray, NumberBuilder<Long>> =
        entryBuilder.longs(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoLongs(
        id: String,
        vararg value: Long,
        builder: NumberBuilder<Long>.() -> Unit = {},
    ): Entry<LongArray, NumberBuilder<Long>> =
        entryBuilder.longs(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoFloat(
        value: Float,
        builder: NumberBuilder<Float>.() -> Unit = {},
    ): Entry<Float, NumberBuilder<Float>> =
        entryBuilder.float(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoFloat(
        id: String,
        value: Float,
        builder: NumberBuilder<Float>.() -> Unit = {},
    ): Entry<Float, NumberBuilder<Float>> =
        entryBuilder.float(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoFloats(
        vararg value: Float,
        builder: NumberBuilder<Float>.() -> Unit = {},
    ): Entry<FloatArray, NumberBuilder<Float>> =
        entryBuilder.floats(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoFloats(
        id: String,
        vararg value: Float,
        builder: NumberBuilder<Float>.() -> Unit = {},
    ): Entry<FloatArray, NumberBuilder<Float>> =
        entryBuilder.floats(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoDouble(
        value: Double,
        builder: NumberBuilder<Double>.() -> Unit = {},
    ): Entry<Double, NumberBuilder<Double>> =
        entryBuilder.double(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoDouble(
        id: String,
        value: Double,
        builder: NumberBuilder<Double>.() -> Unit = {},
    ): Entry<Double, NumberBuilder<Double>> =
        entryBuilder.double(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoDoubles(
        vararg value: Double,
        builder: NumberBuilder<Double>.() -> Unit = {},
    ): Entry<DoubleArray, NumberBuilder<Double>> =
        entryBuilder.doubles(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoDoubles(
        id: String,
        vararg value: Double,
        builder: NumberBuilder<Double>.() -> Unit = {},
    ): Entry<DoubleArray, NumberBuilder<Double>> =
        entryBuilder.doubles(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoBoolean(
        value: Boolean,
        builder: TypeBuilder.() -> Unit = {},
    ): Entry<Boolean, TypeBuilder> =
        entryBuilder.boolean(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoBoolean(
        id: String? = null,
        value: Boolean,
        builder: TypeBuilder.() -> Unit = {},
    ): Entry<Boolean, TypeBuilder> =
        entryBuilder.boolean(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoBooleans(
        vararg value: Boolean,
        builder: TypeBuilder.() -> Unit = {},
    ): Entry<BooleanArray, TypeBuilder> =
        entryBuilder.booleans(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoBooleans(
        id: String,
        vararg value: Boolean,
        builder: TypeBuilder.() -> Unit = {},
    ): Entry<BooleanArray, TypeBuilder> =
        entryBuilder.booleans(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoString(
        value: String,
        builder: StringBuilder.() -> Unit = {},
    ): Entry<String, StringBuilder> =
        entryBuilder.string(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoString(
        id: String,
        value: String,
        builder: StringBuilder.() -> Unit = {},
    ): Entry<String, StringBuilder> =
        entryBuilder.string(id, value = value, wrap(builder))

    // Very hacky but sadly the varargs if its nullable makes it weird
    context(entryBuilder: EntriesBuilder)
    public fun autoStrings(
        vararg value: String,
        builder: StringBuilder.() -> Unit = {},
    ): Entry<Array<out String>, StringBuilder> =
        entryBuilder.strings(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoStrings(
        id: String,
        vararg value: String,
        builder: StringBuilder.() -> Unit = {},
    ): Entry<Array<out String>, StringBuilder> =
        entryBuilder.stringsWithId(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun <T : Enum<T>> autoEnum(
        value: T,
        builder: TypeBuilder.() -> Unit = {},
    ): Entry<T, TypeBuilder> =
        entryBuilder.enum(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun <T : Enum<T>> autoEnum(
        id: String,
        value: T,
        builder: TypeBuilder.() -> Unit = {},
    ): Entry<T, TypeBuilder> =
        entryBuilder.enum(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun <T : Enum<T>> autoEnums(
        vararg value: T,
        builder: TypeBuilder.() -> Unit = {},
    ): Entry<Array<out T>, TypeBuilder> =
        entryBuilder.enums(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun <T : Enum<T>> autoEnums(
        id: String,
        vararg value: T,
        builder: TypeBuilder.() -> Unit = {},
    ): Entry<Array<out T>, TypeBuilder> =
        entryBuilder.enums(id, value = value, wrap(builder))

    // special
    context(entryBuilder: EntriesBuilder)
    public fun autoKey(
        value: Int,
        builder: KeyBuilder.() -> Unit = {},
    ): Entry<Int, KeyBuilder> =
        entryBuilder.key(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoKey(
        id: String,
        value: Int,
        builder: KeyBuilder.() -> Unit = {},
    ): Entry<Int, KeyBuilder> =
        entryBuilder.key(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoColor(
        value: Int,
        builder: ColorBuilder.() -> Unit = {},
    ): Entry<Int, ColorBuilder> =
        entryBuilder.color(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoColor(
        id: String,
        value: Int,
        builder: ColorBuilder.() -> Unit = {},
    ): Entry<Int, ColorBuilder> =
        entryBuilder.color(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun <T : Enum<T>> autoSelect(
        vararg value: T,
        builder: SelectBuilder<T>.() -> Unit = {},
    ): Entry<Array<out T>, SelectBuilder<T>> =
        entryBuilder.select(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun <T : Enum<T>> autoSelect(
        id: String,
        vararg value: T,
        builder: SelectBuilder<T>.() -> Unit = {},
    ): Entry<Array<out T>, SelectBuilder<T>> =
        entryBuilder.select(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun <T : Enum<T>> autoDraggable(
        vararg value: T,
        builder: DraggableBuilder<T>.() -> Unit = {},
    ): Entry<Array<out T>, DraggableBuilder<T>> =
        entryBuilder.draggable(value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun <T : Enum<T>> autoDraggable(
        id: String,
        vararg value: T,
        builder: DraggableBuilder<T>.() -> Unit = {},
    ): Entry<Array<out T>, DraggableBuilder<T>> =
        entryBuilder.draggable(id, value = value, wrap(builder))

    context(entryBuilder: EntriesBuilder)
    public fun autoButton(
        id: String,
        buttonBuilder: ButtonBuilder.() -> Unit,
    ): Unit =
        entryBuilder.button {
            val translation = makeTranslation(id)
            this.description = "$translation.desc"
            this.title = translation
            this.text = "$translation.text"
            buttonBuilder()
        }

    context(entryBuilder: EntriesBuilder)
    public fun autoSeparator(
        id: String,
        separatorBuilder: SeparatorBuilder.() -> Unit = {},
    ): Unit =
        entryBuilder.separator {
            this.translation = makeTranslation(id)
            separatorBuilder()
        }
}
