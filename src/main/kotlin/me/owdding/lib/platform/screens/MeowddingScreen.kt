package me.owdding.lib.platform.screens

//? >= 26.1 {
import net.minecraft.client.gui.screens.Screen as BaseScreen
//? } else {
/*import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen as BaseScreen
*///? }
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.utils.text.Text

import net.minecraft.client.input.CharacterEvent as McCharacterEvent
import net.minecraft.client.input.KeyEvent as McKeyEvent
import net.minecraft.client.input.MouseButtonEvent as McMouseButtonEvent

@ApiStatus.Internal
open class ParameterLessScreen : BaseScreen {
    constructor() : super(CommonComponents.EMPTY)
    constructor(name: Component) : this()
    constructor(name: String) : this()
}

abstract class MeowddingScreen : ParameterLessScreen {
    constructor(component: Component = Component.empty()) : super(component)
    constructor(name: String) : this(Text.of(name))

    open fun mouseClicked(mouseEvent: MouseButtonEvent, doubleClicked: Boolean = false): Boolean {
        return super.mouseClicked(mouseEvent.into(), doubleClicked)
    }
    open fun mouseReleased(mouseEvent: MouseButtonEvent): Boolean {
        return super.mouseReleased(mouseEvent.into())

    }
    open fun mouseDragged(mouseEvent: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean {
        return super.mouseDragged(mouseEvent.into(), deltaX, deltaY)
    }
    open fun keyPressed(keyEvent: KeyEvent): Boolean {
        return super.keyPressed(keyEvent.into())
    }
    open fun keyReleased(keyEvent: KeyEvent): Boolean {
        return super.keyReleased(keyEvent.into())
    }
    open fun charTyped(characterEvent: CharacterEvent): Boolean {
        return super.charTyped(characterEvent.into())
    }

    override fun mouseClicked(event: McMouseButtonEvent, doubleClicked: Boolean) = this.mouseClicked(event.into(), doubleClicked)
    override fun mouseReleased(event: McMouseButtonEvent) = this.mouseReleased(event.into())
    override fun mouseDragged(event: McMouseButtonEvent, deltaX: Double, deltaY: Double) = this.mouseDragged(event.into(), deltaX, deltaY)
    override fun keyPressed(event: McKeyEvent) = this.keyPressed(event.into())
    override fun keyReleased(event: McKeyEvent) = this.keyReleased(event.into())
    override fun charTyped(event: McCharacterEvent) = this.charTyped(event.into())

}
