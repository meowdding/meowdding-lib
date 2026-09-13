package me.owdding.lib.config

import com.teamresourceful.resourcefulconfig.api.client.ResourcefulConfigScreen
import com.teamresourceful.resourcefulconfig.api.loader.Configurator
import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.ConfigKt
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient

@Module
object MeowddingLibConfig : ConfigKt("meowdding-lib/config") {

    override val name = TranslatableValue("Meowdding Lib Config")


    val configurator = Configurator("meowdding-lib")
    val config = register(configurator)

    fun save() = config.save()

    @Subscription
    fun onCommand(event: RegisterCommandsEvent) {
        event.registerWithCallback("meowdding config") {
            McClient.setScreenAsync { ResourcefulConfigScreen.getFactory("meowdding-lib").apply(null) }
        }
    }

    init {
        separator {
            this.title = "Cosmetics"
            this.description = "Cosmetics for specific players, loaded from: https://cosmetics.meowdd.ing/"
        }
    }

    val suffixCosmetic by boolean(true) {
        this.name = TranslatableValue("Name Suffix Cosmetic")
        this.description = TranslatableValue("Suffix after the player name in the Nametag & SkyCubed Tablist")
    }

    val capeCosmetic by boolean(true) {
        this.name = TranslatableValue("Cape Cosmetic")
        this.description = TranslatableValue("Custom Capes")
    }

    val playerScaleCosmetic by boolean(true) {
        this.name = TranslatableValue("Player Scale Cosmetic")
        this.description = TranslatableValue("Changes the scale of players, limited to inside SkyBlock only")
    }

    val pvCosmetic by boolean(true) {
        this.name = TranslatableValue("SkyBlockProfileViewer Cosmetics")
        this.description = TranslatableValue("All Cosmetics shown inside Pv, like custom text below name, pets, ...")
    }

}
