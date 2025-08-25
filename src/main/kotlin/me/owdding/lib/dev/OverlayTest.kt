package me.owdding.lib.dev

import me.owdding.ktmodules.Module
import me.owdding.lib.overlays.ConfigPosition
import me.owdding.lib.overlays.Overlays
import me.owdding.lib.overlays.Position
import me.owdding.lib.overlays.TextOverlay
import tech.thatgravyboat.skyblockapi.utils.text.Text

@Module
object OverlayTest {

    init {
        registerTestOverlay()
    }

    fun registerTestOverlay() {
        if (System.getProperty("meowdding.overlay.test", "false").toBooleanStrictOrNull() != true) return

        Overlays.register(TextOverlay("meowdding-lib", Text.of("grrr"), ConfigPosition(0, 0), { true }, { Text.of("test") }))
        Overlays.register(TextOverlay("skyocean", Text.of("grrrs"), ConfigPosition(0, 0), { true }, { Text.of("testing") }))
    }

}
