package me.owdding.lib.utils

import com.google.gson.JsonElement
import net.fabricmc.loader.api.ModContainer
import java.net.URL

class DataPatcher(patchLocation: URL, val mod: ModContainer) {
    fun patch(jsonElement: JsonElement, name: String) {
    }
}
