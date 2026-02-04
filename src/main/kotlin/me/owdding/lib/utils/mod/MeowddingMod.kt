package me.owdding.lib.utils.mod

import com.mojang.serialization.Codec
import me.owdding.lib.utils.MeowddingLogger
import me.owdding.lib.utils.mod.data.MeowddingFolderStorageData
import me.owdding.lib.utils.mod.data.MeowddingProfileStorageData
import me.owdding.lib.utils.mod.data.MeowddingStorageData
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.getEmptyConstructor

@Suppress("PropertyName")
abstract class MeowddingMod(
    id: String,
    loggerName: String? = null,
) : ClientModInitializer,
    MeowddingLogger by MeowddingLogger.named(loggerName ?: MeowddingLogger.STACK_WALKER.callerClass.simpleName)
{

    val SELF: ModContainer = FabricLoader.getInstance().getModContainer(id).get()
    val MOD_ID: String = SELF.metadata.id
    val VERSION: String = SELF.metadata.version.friendlyString

    fun id(path: String): Identifier = Identifier.fromNamespaceAndPath(MOD_ID, path)

    internal open val storagePath = McClient.config.resolve("$MOD_ID/data")

    abstract fun <T : Any> getCodec(clazz: Class<T>): Codec<T>

    protected fun <T : Any> registerEvents(list: Iterable<T>) = list.forEach(SkyBlockAPI.eventBus::register)


    //region Storage Data
    inline fun <reified T : Any> storage(
        fileName: String,
        codec: Codec<T> = getCodec(T::class.java),
    ): MeowddingStorageData<T> {
        val clazz = T::class
        val constructor = requireNotNull(clazz.getEmptyConstructor()) { "No empty constructor found for class ${clazz.simpleName}" }
        return storage(fileName, { constructor.callBy(emptyMap()) }, 0) { codec }
    }

    inline fun <reified T : Any> storage(
        fileName: String,
        noinline defaultData: () -> T,
        codec: Codec<T> = getCodec(T::class.java),
    ): MeowddingStorageData<T> = storage(fileName, defaultData, 0) { codec }

    fun <T : Any> storage(
        fileName: String,
        defaultData: () -> T,
        version: Int,
        codec: (Int) -> Codec<T>,
    ) : MeowddingStorageData<T> = MeowddingStorageData(version, this, defaultData, fileName, codec)
    //endregion

    //region Profile Storage Data
    inline fun <reified T : Any> profileStorage(
        fileName: String,
        codec: Codec<T> = getCodec(T::class.java),
    ): MeowddingProfileStorageData<T> {
        val clazz = T::class
        val constructor = requireNotNull(clazz.getEmptyConstructor()) { "No empty constructor found for class ${clazz.simpleName}" }
        return profileStorage(fileName, { constructor.callBy(emptyMap()) }, 0) { codec }
    }

    inline fun <reified T : Any> profileStorage(
        fileName: String,
        noinline defaultData: () -> T,
        codec: Codec<T> = getCodec(T::class.java),
    ): MeowddingProfileStorageData<T> = profileStorage(fileName, defaultData, 0) { codec }

    fun <T : Any> profileStorage(
        fileName: String,
        defaultData: () -> T,
        version: Int,
        codec: (Int) -> Codec<T>,
    ) : MeowddingProfileStorageData<T> = MeowddingProfileStorageData(version, this, defaultData, fileName, codec)
    //endregion

    //region Folder Storage Data
    inline fun <reified T : Any> folderStorage(
        folderName: String,
        codec: Codec<T> = getCodec(T::class.java),
    ): MeowddingFolderStorageData<T> = folderStorage(folderName, 0) { codec }

    fun <T : Any> folderStorage(
        folderName: String,
        version: Int,
        codec: (Int) -> Codec<T>,
    ) : MeowddingFolderStorageData<T> = MeowddingFolderStorageData(version, this, folderName, codec)
    //endregion


}
