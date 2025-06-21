package me.owdding.lib.compat.meowdding

import com.google.gson.JsonArray
import kotlinx.coroutines.runBlocking
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import me.owdding.lib.generated.MeowddingLibCodecs
import net.fabricmc.loader.api.FabricLoader
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import java.net.URI
import java.util.concurrent.CompletableFuture

private const val URL = "https://meowdd.ing/mod/info.json"

@Module
object MeowddingModsParser {

    var mods: List<MeowddingMod> = emptyList()
        private set

    private val CODEC = MeowddingLibCodecs.getCodec<MeowddingMod>().listOf()

    init {
        CompletableFuture.runAsync {
            runBlocking {
                mods = Http.getResult<JsonArray>(URL).getOrNull().toDataOrThrow(CODEC)
            }
        }
    }
}

@GenerateCodec
data class MeowddingMod(
    val name: String,
    @FieldName("mod_id") val modId: String,
    @FieldName("config_id") val configId: String,
    @FieldName("modrinth_slug") val modrinthSlug: String,
    @FieldName("github_repo") val githubRepo: String,
    val icon: String,
) {
    val isInstalled by lazy { FabricLoader.getInstance().isModLoaded(modId) }
    val uri: URI = URI.create(icon)
}
