package me.owdding.lib.config

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.ConfigKt
import me.owdding.ktmodules.Module
import me.owdding.lib.MeowddingLib
import me.owdding.lib.utils.config.AutoTranslated
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient

@Module
object MeowddingLibConfig : ConfigKt("meowdding-lib/config"), AutoTranslated {
    override val translationBase: String = "mlib.config"
    override val name = Translated("mlib.config")

    val configurator = Configurator(MeowddingLib.MOD_ID)
    val config = register(configurator)

    fun save() = config.save()

    @Subscription
    fun onCommand(event: RegisterCommandsEvent) {
        event.registerWithCallback("meowdding config") {
            McClient.setScreenAsync { ResourcefulConfigScreen.getFactory(MeowddingLib.MOD_ID).apply(null) }
        }
    }

    init {
        autoSeparator("cosmetics")
    }

    val suffixCosmetic by autoBoolean(true)

    val capeCosmetic by autoBoolean(true)

    val playerScaleCosmetic by autoBoolean(true)

    val pvCosmetic by autoBoolean(true)

}
