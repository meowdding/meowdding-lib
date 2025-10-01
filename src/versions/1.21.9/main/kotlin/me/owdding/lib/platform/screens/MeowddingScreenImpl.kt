@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package me.owdding.lib.platform.screens

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.text.Text
import net.minecraft.client.input.CharacterEvent as McCharacterEvent
import net.minecraft.client.input.KeyEvent as McKeyEvent
import net.minecraft.client.input.MouseButtonEvent as McMouseButtonEvent

actual abstract class MeowddingScreen : BaseCursorScreen {

    actual constructor(component: Component) : super(component)
    actual constructor(name: String) : this(Text.of(name))

    actual open fun mouseClicked(mouseEvent: MouseButtonEvent, doubleClicked: Boolean) = super.mouseClicked(mouseEvent.into(), doubleClicked)
    actual open fun mouseReleased(mouseEvent: MouseButtonEvent) = super.mouseReleased(mouseEvent.into())
    actual open fun mouseDragged(mouseEvent: MouseButtonEvent, deltaX: Double, deltaY: Double) = super.mouseDragged(mouseEvent.into(), deltaX, deltaY)
    actual open fun keyPressed(keyEvent: KeyEvent) = super.keyPressed(keyEvent.into())
    actual open fun keyReleased(keyEvent: KeyEvent) = super.keyReleased(keyEvent.into())
    actual open fun charTyped(characterEvent: CharacterEvent) = super.charTyped(characterEvent.into())

    override fun mouseClicked(event: McMouseButtonEvent, doubleClicked: Boolean) = this.mouseClicked(event.into(), doubleClicked)
    override fun mouseReleased(event: McMouseButtonEvent) = this.mouseReleased(event.into())
    override fun mouseDragged(event: McMouseButtonEvent, deltaX: Double, deltaY: Double) = this.mouseDragged(event.into(), deltaX, deltaY)
    override fun keyPressed(event: McKeyEvent) = this.keyPressed(event.into())
    override fun keyReleased(event: McKeyEvent) = this.keyReleased(event.into())
    override fun charTyped(event: McCharacterEvent) = this.charTyped(event.into())
}
