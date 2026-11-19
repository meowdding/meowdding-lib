package me.owdding.lib.config

import com.teamresourceful.resourcefulconfigkt.api.ConfigKt
import me.owdding.lib.MeowddingLib
import me.owdding.lib.utils.config.AutoTranslated

object MeowddingLibConfig : ConfigKt("meowdding-lib/config"), AutoTranslated {
    override val translationBase: String = "mlib.config"
    override val name = Translated("mlib.config")

    fun save() = MeowddingLib.config.save()

    val hypixelPackSharing by autoBoolean(true)

    init {
        autoSeparator("cosmetics")
    }

    val suffixCosmetic by autoBoolean(true)

    val capeCosmetic by autoBoolean(true)

    val playerScaleCosmetic by autoBoolean(true)

    val pvCosmetic by autoBoolean(true)

}
