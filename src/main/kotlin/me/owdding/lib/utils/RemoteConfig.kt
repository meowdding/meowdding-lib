package me.owdding.lib.utils

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfig
import com.teamresourceful.resourcefulconfig.api.types.ResourcefulConfigElement
import com.teamresourceful.resourcefulconfig.api.types.elements.ResourcefulConfigEntryElement
import com.teamresourceful.resourcefulconfig.api.types.elements.ResourcefulConfigObjectEntryElement
import com.teamresourceful.resourcefulconfig.api.types.entries.ResourcefulConfigValueEntry
import com.teamresourceful.resourcefulconfig.api.types.options.EntryType
import kotlinx.coroutines.runBlocking
import me.owdding.patches.utils.VersionIntervalParser
import net.fabricmc.loader.api.ModContainer
import tech.thatgravyboat.skyblockapi.utils.extentions.asList
import tech.thatgravyboat.skyblockapi.utils.extentions.asMap
import tech.thatgravyboat.skyblockapi.utils.http.Http

object RemoteConfig {
    fun lockConfig(config: ResourcefulConfig, url: String, mod: ModContainer) {
        runCatching {
            runBlocking {
                val data = Http.getResult<JsonObject>(url).getOrNull() ?: return@runBlocking
                val patches = data.asMap { string, element -> VersionIntervalParser.parse(string) to element as? JsonObject }

                for ((version, data) in patches) {
                    if (data == null) continue
                    if (!version.test(mod.metadata.version)) continue

                    lockConfig(config, data)
                }
            }
        }
    }
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
    entries.removeIf { element ->
        when {
            element is ResourcefulConfigObjectEntryElement -> {
                (data.get(element.id()) as? JsonObject)?.let { data ->
                    lockElements(element.entry().elements(), data)
                }
            }

            element is ResourcefulConfigEntryElement && element.entry() is ResourcefulConfigValueEntry -> {
                val data = data.get(element.id())
                if (data != null) {
                    lockEntry(element.entry() as ResourcefulConfigValueEntry, data)
                    return@removeIf true
                }
            }
        }

        false
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

