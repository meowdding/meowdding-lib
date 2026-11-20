package me.owdding.lib.utils.mod

import com.mojang.serialization.Codec
import me.owdding.lib.utils.MeowddingLogger
import me.owdding.lib.utils.RemoteRepoDelegate
import me.owdding.lib.utils.mod.data.MeowddingFolderStorageData
import me.owdding.lib.utils.mod.data.MeowddingProfileStorageData
import me.owdding.lib.utils.mod.data.MeowddingStorageData
import me.owdding.repo.RemoteRepo
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.getEmptyConstructor
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.nio.file.Path
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Suppress("PropertyName")
public abstract class MeowddingMod(
    id: String,
    loggerName: String? = null,
) : ClientModInitializer, MeowddingLogger by MeowddingLogger.named(loggerName ?: FabricLoader.getInstance().getModContainer(id).get().metadata.name) {

    public val SELF: ModContainer = FabricLoader.getInstance().getModContainer(id).get()
    public val MOD_ID: String = SELF.metadata.id
    public val VERSION: String = SELF.metadata.version.friendlyString
    public open val RESOURCE_PATH: String = MOD_ID

    public fun id(path: String): Identifier = Identifier.fromNamespaceAndPath(RESOURCE_PATH, path)

    public open val storagePath: Path = McClient.config.resolve("$MOD_ID/data")

    public inline fun <reified T : Any> getCodec(): Codec<T> = getCodec(typeOf<T>())

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    //~ if >= 26.4 'open' -> 'abstract'
    public open fun <T : Any> getCodec(type: KType): Codec<T>
        //? if <= 26.4
        = getCodec((type.classifier as KClass<T>).java)

    //? if < 26.4 {
    @Deprecated("Use getCodec(KType) instead. This will be removed on 26.4")
    public abstract fun <T : Any> getCodec(clazz: Class<T>): Codec<T>
    //?}

    public fun <T : Any> registerEvents(list: Iterable<T>): Unit = list.forEach(SkyBlockAPI.eventBus::register)

    public open fun preInit() {}

    public open fun postInit() {
    }

    init {
        preInit()
        ClientLifecycleEvents.CLIENT_STARTED.register {
            postInit()
        }
    }

    //region Storage Data
    public inline fun <reified T : Any> storage(
        fileName: String,
        differentAlphaData: Boolean = true,
        codec: Codec<T> = getCodec<T>(),
    ): MeowddingStorageData<T> {
        val clazz = T::class
        val constructor = requireNotNull(clazz.getEmptyConstructor()) { "No empty constructor found for class ${clazz.simpleName}" }
        return storage(fileName, { constructor.callBy(emptyMap()) }, 0, { codec }, differentAlphaData)
    }

    public inline fun <reified T : Any> storage(
        fileName: String,
        noinline defaultData: () -> T,
        differentAlphaData: Boolean = true,
        codec: Codec<T> = getCodec<T>(),
    ): MeowddingStorageData<T> = storage(fileName, defaultData, 0, { codec }, differentAlphaData)

    @JvmOverloads
    public fun <T : Any> storage(
        fileName: String,
        defaultData: () -> T,
        version: Int,
        codec: (Int) -> Codec<T>,
        differentAlphaData: Boolean = true,
    ) : MeowddingStorageData<T> = MeowddingStorageData(version, this, defaultData, fileName, codec, differentAlphaData)

    //endregion

    //region Profile Storage Data
    public inline fun <reified T : Any> profileStorage(
        fileName: String,
        differentAlphaData: Boolean = true,
        codec: Codec<T> = getCodec<T>(),
    ): MeowddingProfileStorageData<T> {
        val clazz = T::class
        val constructor = requireNotNull(clazz.getEmptyConstructor()) { "No empty constructor found for class ${clazz.simpleName}" }
        return profileStorage(fileName, { constructor.callBy(emptyMap()) }, 0, { codec }, differentAlphaData)
    }

    public inline fun <reified T : Any> profileStorage(
        fileName: String,
        noinline defaultData: () -> T,
        differentAlphaData: Boolean = true,
        codec: Codec<T> = getCodec<T>(),
    ): MeowddingProfileStorageData<T> = profileStorage(fileName, defaultData, 0, { codec }, differentAlphaData)

    @JvmOverloads
    public fun <T : Any> profileStorage(
        fileName: String,
        defaultData: () -> T,
        version: Int,
        codec: (Int) -> Codec<T>,
        differentAlphaData: Boolean = true,
    ) : MeowddingProfileStorageData<T> = MeowddingProfileStorageData(version, this, defaultData, fileName, codec, differentAlphaData)
    //endregion

    //region Folder Storage Data
    public inline fun <reified T : Any> folderStorage(
        folderName: String,
        codec: Codec<T> = getCodec<T>(),
    ): MeowddingFolderStorageData<T> = folderStorage(folderName, 0) { codec }

    public fun <T : Any> folderStorage(
        folderName: String,
        version: Int,
        codec: (Int) -> Codec<T>,
    ) : MeowddingFolderStorageData<T> = MeowddingFolderStorageData(version, this, folderName, codec)
    //endregion

    private val repoLogger: MeowddingLogger = this.featureLogger("RemoteRepo")

    public inline fun <reified T : Any> loadRepo(path: String): T? = loadRepo(path, getCodec<T>())

    public fun <T : Any> loadRepo(path: String, codec: Codec<T>): T? = repoLogger.runCatching("Loading $path from remote repo!") {
        RemoteRepo.getFileContentAsJson("$path.json").toDataOrThrow(codec)
    }

    public inline fun <reified T : Any> repoDelegate(path: String): RemoteRepoDelegate<T, T?> {
        return repoDelegate(path, getCodec<T>())
    }

    public inline fun <reified T : Any> repoDelegate(path: String, default: T): RemoteRepoDelegate<T, T> {
        return repoDelegate(path, default, getCodec<T>())
    }

    public fun <T : Any> repoDelegate(
        path: String,
        codec: Codec<T>,
    ): RemoteRepoDelegate<T, T?> = context(repoLogger) {
        RemoteRepoDelegate.load(path, codec)
    }

    public fun <T : Any> repoDelegate(
        path: String,
        default: T,
        codec: Codec<T>,
    ): RemoteRepoDelegate<T, T> = context(repoLogger) {
        RemoteRepoDelegate.load(path, default, codec)
    }
}
