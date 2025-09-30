package me.owdding.lib.platform.screens

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen
import net.minecraft.network.chat.Component
import net.msrandom.stub.Stub

@Stub
expect abstract class MeowddingScreen(component: Component) : BaseCursorScreen {

    open fun mouseClicked(mouseEvent: MouseButtonEvent, doubleClicked: Boolean = false): Boolean
    open fun mouseReleased(mouseEvent: MouseButtonEvent): Boolean
    open fun mouseDragged(mouseEvent: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean
    open fun keyPressed(keyEvent: KeyEvent): Boolean
    open fun keyReleased(keyEvent: KeyEvent): Boolean
    open fun charTyped(characterEvent: CharacterEvent): Boolean

}
