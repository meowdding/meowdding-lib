@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package me.owdding.lib.platform.screens

import earth.terrarium.olympus.client.components.base.BaseWidget
import net.minecraft.client.input.CharacterEvent as McCharacterEvent
import net.minecraft.client.input.KeyEvent as McKeyEvent
import net.minecraft.client.input.MouseButtonEvent as McMouseButtonEvent
import net.minecraft.client.input.MouseButtonInfo as McMouseButtonInfo

actual abstract class BaseWidget : BaseWidget {
    actual constructor() : super()
    actual constructor(width: Int, height: Int) : super(width, height)

    actual open fun onClick(event: MouseButtonEvent, doubleClick: Boolean) = super.onClick(event.into(), doubleClick)
    override fun onClick(event: McMouseButtonEvent, doubleClick: Boolean) = this.onClick(event.into(), doubleClick)
    actual open fun onRelease(event: MouseButtonEvent) = super.onRelease(event.into())
    override fun onRelease(event: McMouseButtonEvent) = this.onRelease(event.into())
    actual open fun onDrag(event: MouseButtonEvent, deltaX: Double, deltaY: Double) = super.onDrag(event.into(), deltaX, deltaY)
    override fun onDrag(event: McMouseButtonEvent, deltaX: Double, deltaY: Double) = this.onDrag(event.into(), deltaX, deltaY)

    actual open fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean = super.mouseClicked(event.into(), doubleClick)
    override fun mouseClicked(event: McMouseButtonEvent, doubleClick: Boolean) = this.mouseClicked(event.into(), doubleClick)

    actual open fun mouseReleased(event: MouseButtonEvent): Boolean = super.mouseReleased(event.into())
    override fun mouseReleased(event: McMouseButtonEvent) = this.mouseReleased(event.into())

    actual open fun mouseDragged(event: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean = super.mouseDragged(event.into(), deltaX, deltaY)
    override fun mouseDragged(event: McMouseButtonEvent, dragX: Double, dragY: Double) = this.mouseDragged(event.into(), dragX, dragY)

    protected actual open fun isValidClickButton(info: MouseButtonInfo): Boolean = super.isValidClickButton(info.into())
    override fun isValidClickButton(event: McMouseButtonInfo) = this.isValidClickButton(event.into())


    actual open fun keyPressed(event: KeyEvent): Boolean = super.keyPressed(event.into())
    override fun keyPressed(event: McKeyEvent): Boolean = this.keyPressed(event.into())

    actual open fun keyReleased(event: KeyEvent): Boolean = super.keyReleased(event.into())
    override fun keyReleased(event: McKeyEvent) = this.keyReleased(event.into())

    actual open fun charTyped(event: CharacterEvent): Boolean = super.charTyped(event.into())
    override fun charTyped(event: McCharacterEvent) = this.charTyped(event.into())
}
