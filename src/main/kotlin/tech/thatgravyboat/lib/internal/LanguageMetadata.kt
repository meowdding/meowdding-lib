package tech.thatgravyboat.lib.internal

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import tech.thatgravyboat.skyblockapi.utils.extentions.asBoolean
import tech.thatgravyboat.skyblockapi.utils.extentions.asString

internal data class LanguageMetadata(
    val allowMultiline: Boolean,
    val allowTags: Boolean,
    val prefix: String?,
    val objectsAsKeys: Boolean,
    val allowReferences: Boolean,
    val references: MutableMap<String, JsonElement>?,
) {
    companion object {
        @JvmStatic
        fun fromJson(json: JsonObject): LanguageMetadata {
            return LanguageMetadata(
                json.get("allowMultiline").asBoolean(false),
                json.get("allowTags").asBoolean(false),
                json.get("prefix").asString("").takeUnless { it.isBlank() },
                json.get("objectAsKeys").asBoolean(false),
                json.get("allowReferences").asBoolean(false),
                json.getAsJsonObject("@references")?.asMap(),
            )
        }
    }
}

internal object LanguageHelper {
    @JvmStatic
    val componentKeyList: MutableList<String> = mutableListOf()
}
