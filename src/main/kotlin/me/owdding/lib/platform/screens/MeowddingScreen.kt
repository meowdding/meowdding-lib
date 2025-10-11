package me.owdding.lib.platform.screens

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.msrandom.stub.Stub
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
open class ParameterLessScreen : BaseCursorScreen {
    constructor() : super(CommonComponents.EMPTY)
    constructor(name: Component) : this()
    constructor(name: String) : this()
}

@Stub
expect abstract class MeowddingScreen : ParameterLessScreen {
    constructor(component: Component = Component.empty())
    constructor(name: String)

    open fun mouseClicked(mouseEvent: MouseButtonEvent, doubleClicked: Boolean = false): Boolean
    open fun mouseReleased(mouseEvent: MouseButtonEvent): Boolean
    open fun mouseDragged(mouseEvent: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean
    open fun keyPressed(keyEvent: KeyEvent): Boolean
    open fun keyReleased(keyEvent: KeyEvent): Boolean
    open fun charTyped(characterEvent: CharacterEvent): Boolean

}
