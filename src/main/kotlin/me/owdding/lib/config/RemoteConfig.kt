package me.owdding.lib.config

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfigElement
import com.teamresourceful.resourcefulconfig.api.types.elements.ResourcefulConfigEntryElement
import com.teamresourceful.resourcefulconfig.api.types.elements.ResourcefulConfigObjectEntryElement
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigValueEntry
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType
import tech.thatgravyboat.skyblockapi.utils.extentions.asList
import java.util.function.Predicate

fun ResourcefulConfig.lock(partialConfig: JsonObject) {
    lockConfig(this, partialConfig)
}

private fun lockConfig(config: ResourcefulConfig, data: JsonObject) {
    lockElements(config.elements(), data)
    for ((id, config) in config.categories()) {
        (data.get(id) as? JsonObject)?.let { data ->
            lockConfig(config, data)
        }
    }
}

private fun lockElements(entries: MutableList<ResourcefulConfigElement>, data: JsonObject) {
    entries.replaceAll { element ->
        when {
            element is ResourcefulConfigObjectEntryElement -> {
                (data.get(element.id()) as? JsonObject)?.let { data ->
                    lockElements(element.entry().elements(), data)
                }
            }
            element is ResourcefulConfigEntryElement && element.entry() is ResourcefulConfigValueEntry -> {
                return@replaceAll data.get(element.id())?.let { data ->
                    lockEntry(element.entry() as ResourcefulConfigValueEntry, data)

                    object : ResourcefulConfigElement {
                        override fun search(predicate: Predicate<String>): Boolean = false
                        override fun isHidden(): Boolean = true
                    }
                } ?: element
            }
        }

        element
    }
}

private fun lockEntry(entry: ResourcefulConfigValueEntry, data: JsonElement) {
    if (entry.isArray) {
        val array = data.asList<Any?> { element ->
            when (entry.type()) {
                EntryType.BYTE -> element.asByte
                EntryType.SHORT -> element.asString
                EntryType.INTEGER -> element.asInt
                EntryType.LONG -> element.asLong
                EntryType.FLOAT -> element.asFloat
                EntryType.DOUBLE -> element.asDouble
                EntryType.STRING -> element.asString
                EntryType.BOOLEAN -> element.asBoolean
                EntryType.ENUM -> entry.objectType().enumConstants.find { (it as? Enum<*>)?.name == element.asString } as? Enum<*>
                EntryType.OBJECT -> null
            }
        }

        entry.array = array.toTypedArray()
    } else {
        when (entry.type()) {
            EntryType.BYTE -> entry.byte = data.asByte
            EntryType.SHORT -> entry.string = data.asString
            EntryType.INTEGER -> entry.int = data.asInt
            EntryType.LONG -> entry.long = data.asLong
            EntryType.FLOAT -> entry.float = data.asFloat
            EntryType.DOUBLE -> entry.double = data.asDouble
            EntryType.STRING -> entry.string = data.asString
            EntryType.BOOLEAN -> entry.boolean = data.asBoolean
            EntryType.ENUM -> entry.enum = entry.objectType().enumConstants.find { (it as? Enum<*>)?.name == data.asString } as? Enum<*>
            EntryType.OBJECT -> {}
        }
    }
}

