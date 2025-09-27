package me.owdding.lib.cosmetics

import com.google.gson.JsonObject
import kotlinx.coroutines.runBlocking
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import me.owdding.lib.MeowddingLib
import me.owdding.lib.generated.CodecUtils
import me.owdding.lib.generated.MeowddingLibCodecs
import me.owdding.lib.rendering.text.builtin.GradientTextShader
import net.minecraft.core.ClientAsset
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.animal.Parrot
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import java.util.*

private const val CONTRIBUTOR_URL = "https://meowdd.ing/mod/contributors.json"
private const val SPECIAL_PEOPLE_URL = "https://meowdd.ing/mod/special_people.json"

@Module
object CosmeticParser {

    var cosmetics: MutableMap<UUID, Cosmetics> = mutableMapOf()
        private set

    private val CODEC = CodecUtils.map(MeowddingLibCodecs.getCodec<UUID>(), MeowddingLibCodecs.getCodec<Cosmetics>())

    init {
        runBlocking {
            try {
                cosmetics = Http.getResult<JsonObject>(CONTRIBUTOR_URL).getOrNull().toData(CODEC) ?: mutableMapOf()
                cosmetics.putAll(Http.getResult<JsonObject>(SPECIAL_PEOPLE_URL).getOrNull().toData(CODEC) ?: mutableMapOf())
            } catch (e: Throwable) {
                MeowddingLib.error("Something went wrong trying to fetch and parse cosmetics", e)
            }
        }
    }

}

@GenerateCodec
data class Cosmetics(
    val cape: ResourceLocation?,
    val symbol: String?,
    val pvCosmetics: PvCosmetics?,
)

@GenerateCodec
data class PvCosmetics(
    val title: Component?,
    val parrot: ParrotOnShoulder?,
    val cat: CatOnShoulder?,
    val shaking: Boolean = false,
    @FieldName("title_colors") val titleColors: List<TextColor>?,
) {
    val titleShader = titleColors?.let { GradientTextShader(*it.toTypedArray()) }
}

@GenerateCodec
data class ParrotOnShoulder(val variant: Parrot.Variant, @FieldName("left_shoulder") val leftSide: Boolean)

@GenerateCodec
data class CatOnShoulder(@FieldName("asset_id") val asset: ClientAsset, @FieldName("left_shoulder") val leftSide: Boolean)
