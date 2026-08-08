package me.owdding.lib.rendering.text

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.teamresourceful.resourcefulconfig.api.types.info.Translatable
import me.owdding.lib.MeowddingLib
import me.owdding.lib.PreInitModule
import me.owdding.lib.events.RegisterTextShaderEvent
import me.owdding.lib.generated.MeowddingLibCodecs
import me.owdding.lib.rendering.text.builtin.GradientTextShader
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.resources.Identifier
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.utils.text.Text

@PreInitModule
object TextShaders {

    var activeShader: TextShader? = null
        @JvmStatic get
        @JvmStatic @ApiStatus.Internal set

    fun GuiGraphicsExtractor.withTextShader(shader: TextShader?, action: () -> Unit) = pushPop(shader, action)

    fun pushPop(shader: TextShader?, action: () -> Unit) {
        activeShader = shader
        action()
        activeShader = null
    }

    private val codecRegistry by lazy {
        val registry = mutableMapOf<Identifier, MapCodec<out TextShader>>()
        RegisterTextShaderEvent(registry).post(SkyBlockAPI.eventBus)
        registry
    }

    val CODEC: Codec<TextShader> = Identifier.CODEC.dispatch({ it.id }, { codecRegistry[it]!! })

    @Subscription
    fun register(event: RegisterTextShaderEvent) {
        event.register(GradientTextShader.ID, GradientTextShader.CODEC)

        Text.of {
            this.textShader = PrideShader.BISEXUAL
        }
    }
}

enum class PrideShader(val colors: List<Int>, private val shader: GradientTextShader = GradientTextShader(colors)) : TextShader by shader, Translatable {
    RAINBOW(0xFF0000, 0xFF7F00, 0xFFFF00, 0x00FF00, 0x0000FF, 0x4B0082, 0x8B00FF),
    BISEXUAL(0xD60270, 0x9B4F96, 0x0038A8),
    GAY(0xFF0000, 0xFF9900, 0xFFFF00, 0x33CC33, 0x3399FF, 0x9900CC),
    LESBIAN(0xD62900, 0xFF9A56, 0xFFAC54, 0xFFFFFF, 0xD362A4, 0xB9558A, 0xA40061),
    PANSEXUAL(0xFF1B8D, 0xFFD800, 0x1BB3FF),
    ASEXUAL(0x000000, 0xA4A4A4, 0xFFFFFF, 0x810081),
    NON_BINARY(0xFFD800, 0xFFFFFF, 0x9C59D1, 0x000000),
    TRANS(0x55CDFC, 0xF7A8B8, 0xFFFFFF, 0xF7A8B8, 0x55CDFC),
    GENDERFLUID(0xFF76A4, 0xFFFFFF, 0xC011D7, 0x000000, 0x2F3CBE),
    ;

    override val id: Identifier = MeowddingLib.id("named_gradient")

    constructor(vararg colors: Int) : this(
        colors.toMutableList().apply { if (size > 1) addLast(first()) }
    )

    override fun getTranslationKey() = "mlib.gradients.${name.lowercase()}"

    @PreInitModule
    companion object {
        val ID = MeowddingLib.id("named_gradient")
        val CODEC: MapCodec<PrideShader> = MeowddingLibCodecs.getCodec<PrideShader>().fieldOf("name")

        @Subscription
        fun registerShaders(event: RegisterTextShaderEvent) {
            event.register(ID, CODEC)
        }
    }
}
