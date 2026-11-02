package me.owdding.lib.dev

import me.owdding.ktmodules.Module
import me.owdding.lib.MeowddingLib
import me.owdding.lib.utils.sendWithPrefix
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.devutils.DevUtils
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugMultiSelect
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugSelect
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugToggle
import java.nio.file.Path
import kotlin.io.path.Path

internal fun debugToggle(path: String, description: String = "", prefix: String = path): DebugToggle {
    return DebugToggle(MeowddingLib.id(path), description, MlibDevUtils, prefix)
}

internal fun <T : Any> debugSelect(
    path: String,
    description: String = "",
    initialState: T?,
    states: List<T>,
    toString: (T) -> String = { it.toString() },
): DebugSelect<T> {
    return DebugSelect(MeowddingLib.id(path), description, MlibDevUtils, initialState, toString, states)
}

internal inline fun <reified T : Enum<T>> debugSelect(
    path: String,
    description: String = "",
    initialState: T? = null,
): DebugSelect<T> = debugSelect(path, description, initialState, T::class.java.enumConstants.toList())


internal fun <T : Any> debugMultiSelect(
    path: String,
    description: String = "",
    initialState: Set<T> = emptySet(),
    toString: (T) -> String = { it.toString() },
    states: () -> List<T>,
): DebugMultiSelect<T> {
    return DebugMultiSelect(MeowddingLib.id(path), description, MlibDevUtils, initialState, toString, states)
}

internal inline fun <reified T : Enum<T>> debugMultiSelect(
    path: String,
    description: String = "",
    initialState: Set<T> = emptySet(),
): DebugMultiSelect<T> = debugMultiSelect(path, description, initialState, { it.name }) { T::class.java.enumConstants.toList() }

@Module
internal data object MlibDevUtils : DevUtils() {
    override val commandName: String = "meowdding dev toggle"
    override fun send(component: MutableComponent) = component.sendWithPrefix()

    override val supportsChangingDefault: Boolean = true
    override val path: Path = loadPath()
    val propertiesMap: Map<String, String> = loadFromProperties(path)

    override val namespace: String = MeowddingLib.NAMESPACE

    private fun loadPath(): Path {
        val specifiedPath = System.getenv("tech_thatgravyboat_sbapi_debug_property_path") ?: System.getProperty("sbapi.property_path")
        return if (specifiedPath != null) Path(specifiedPath)
        else McClient.config.resolve("sbapi.properties")
    }
}
