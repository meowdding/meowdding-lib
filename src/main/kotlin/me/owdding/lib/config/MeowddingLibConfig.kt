package me.owdding.lib.config

import com.teamresourceful.resourcefulconfigkt.api.ConfigKt
import me.owdding.lib.MeowddingLib
import me.owdding.lib.utils.config.AutoTranslated

object MeowddingLibConfig : ConfigKt("meowdding-lib/config"), AutoTranslated {
    override val translationBase: String = "mlib.config"
    override val name = Translated("mlib.config")

    fun save() = MeowddingLib.config.save()

    var hypixelPackSharing by autoBoolean(true)

    init {
        autoSeparator("cosmetics")
    }

    var suffixCosmetic by autoBoolean(true)

    var capeCosmetic by autoBoolean(true)

    var playerScaleCosmetic by autoBoolean(true)

    var pvCosmetic by autoBoolean(true)

}
