package me.owdding.lib.config

import com.teamresourceful.resourcefulconfig.api.types.options.TranslatableValue
import com.teamresourceful.resourcefulconfigkt.api.ConfigKt
import me.owdding.lib.MeowddingLib
import me.owdding.lib.utils.config.AutoTranslated

public object MeowddingLibConfig : ConfigKt("meowdding-lib/config"), AutoTranslated {
    override val translationBase: String = "mlib.config"
    public override val name: TranslatableValue = Translated("mlib.config")

    public fun save(): Unit = MeowddingLib.config.save()

    public var hypixelPackSharing: Boolean by autoBoolean(true)

    init {
        autoSeparator("cosmetics")
    }

    public var suffixCosmetic: Boolean by autoBoolean(true)

    public var capeCosmetic: Boolean by autoBoolean(true)

    public var playerScaleCosmetic: Boolean by autoBoolean(true)

    public var pvCosmetic: Boolean by autoBoolean(true)

}
