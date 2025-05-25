package me.owdding.lib.utils

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import me.owdding.patches.RepoPatch
import me.owdding.patches.actions.Composite
import net.fabricmc.loader.api.ModContainer
import java.net.URL

class DataPatcher(patchLocation: URL, val mod: ModContainer) {

    val emptyPatch = RepoPatch(emptyMap(), Composite(emptyList(), false), "")

    private val patches: List<RepoPatch>

    init {
        val patchesJson = patchLocation.openStream().use {
            it.bufferedReader(Charsets.UTF_8).use { reader ->
                JsonParser.parseReader(reader)
            }
        }

        patches = RepoPatch.CODEC.orElse(emptyPatch).listOf().parse(JsonOps.INSTANCE, patchesJson).orThrow
    }

    fun patch(jsonElement: JsonElement, name: String) {
        patches
            .filter { it.shouldApply(mod, name) }
            .sortedBy { it.ordinal }
            .forEach { it.apply(jsonElement) }
    }

}
