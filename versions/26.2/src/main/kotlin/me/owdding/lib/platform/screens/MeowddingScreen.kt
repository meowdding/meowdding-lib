package me.owdding.lib.platform.screens

import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.utils.text.Text

import net.minecraft.client.input.CharacterEvent as McCharacterEvent
import net.minecraft.client.input.KeyEvent as McKeyEvent
import net.minecraft.client.input.MouseButtonEvent as McMouseButtonEvent

@ApiStatus.Internal
public open class ParameterLessScreen : Screen {
    protected constructor() : super(CommonComponents.EMPTY)
    protected constructor(name: Component) : this()
    protected constructor(name: String) : this()
}

public abstract class MeowddingScreen : ParameterLessScreen {
    public constructor(component: Component = Component.empty()) : super(component)
    public constructor(name: String) : this(Text.of(name))

    public open fun mouseClicked(mouseEvent: MouseButtonEvent, doubleClicked: Boolean = false): Boolean {
        return super.mouseClicked(mouseEvent.into(), doubleClicked)
    }
    public open fun mouseReleased(mouseEvent: MouseButtonEvent): Boolean {
        return super.mouseReleased(mouseEvent.into())

    }
    public open fun mouseDragged(mouseEvent: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean {
        return super.mouseDragged(mouseEvent.into(), deltaX, deltaY)
    }
    public open fun keyPressed(keyEvent: KeyEvent): Boolean {
        return super.keyPressed(keyEvent.into())
    }
    public open fun keyReleased(keyEvent: KeyEvent): Boolean {
        return super.keyReleased(keyEvent.into())
    }
    public open fun charTyped(characterEvent: CharacterEvent): Boolean {
        return super.charTyped(characterEvent.into())
    }

    override fun mouseClicked(event: McMouseButtonEvent, doubleClicked: Boolean): Boolean = this.mouseClicked(event.into(), doubleClicked)
    override fun mouseReleased(event: McMouseButtonEvent): Boolean = this.mouseReleased(event.into())
    override fun mouseDragged(event: McMouseButtonEvent, deltaX: Double, deltaY: Double): Boolean = this.mouseDragged(event.into(), deltaX, deltaY)
    override fun keyPressed(event: McKeyEvent): Boolean = this.keyPressed(event.into())
    override fun keyReleased(event: McKeyEvent): Boolean = this.keyReleased(event.into())
    override fun charTyped(event: McCharacterEvent): Boolean = this.charTyped(event.into())

}
