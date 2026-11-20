package me.owdding.lib.utils

import com.google.gson.JsonElement
import net.fabricmc.loader.api.ModContainer
import java.net.URL

public class DataPatcher(patchLocation: URL, public val mod: ModContainer) {
    public fun patch(jsonElement: JsonElement, name: String) {
    }
}
