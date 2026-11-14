package me.owdding.lib.config

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.ConfigKt
import me.owdding.ktmodules.Module
import me.owdding.lib.MeowddingLib
import me.owdding.lib.events.MeowddingLibRegisterCommandsEvent
import me.owdding.lib.utils.config.AutoTranslated
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.helpers.McClient

@Module
public object MeowddingLibConfig : ConfigKt("meowdding-lib/config"), AutoTranslated {
    override val translationBase: String = "mlib.config"
    public override val name: TranslatableValue = Translated("mlib.config")

    internal val configurator = Configurator(MeowddingLib.MOD_ID)
    internal val config = register(configurator)

    public fun save(): Unit = config.save()

    @Subscription
    internal fun onCommand(event: MeowddingLibRegisterCommandsEvent) {
        event.registerWithCallback("config") {
            McClient.setScreenAsync { ResourcefulConfigScreen.getFactory(MeowddingLib.MOD_ID).apply(null) }
        }
    }

    public val hypixelPackSharing: Boolean by autoBoolean(true)

    init {
        autoSeparator("cosmetics")
    }

    public val suffixCosmetic: Boolean by autoBoolean(true)

    public val capeCosmetic: Boolean by autoBoolean(true)

    public val playerScaleCosmetic: Boolean by autoBoolean(true)

    public val pvCosmetic: Boolean by autoBoolean(true)

}
