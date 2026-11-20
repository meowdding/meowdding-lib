package me.owdding.lib.platform.screens

import earth.terrarium.olympus.client.components.base.BaseParentWidget
import net.minecraft.client.input.CharacterEvent as McCharacterEvent
import net.minecraft.client.input.KeyEvent as McKeyEvent
import net.minecraft.client.input.MouseButtonEvent as McMouseButtonEvent
import net.minecraft.client.input.MouseButtonInfo as McMouseButtonInfo

public abstract class BaseParentWidget : BaseParentWidget {
    public constructor() : super()
    public constructor(width: Int, height: Int) : super(width, height)

    public open fun onClick(event: MouseButtonEvent, doubleClick: Boolean) {
        super.onClick(event.into(), doubleClick)
    }

    public open fun onRelease(event: MouseButtonEvent) {
        super.onRelease(event.into())
    }

    public open fun onDrag(event: MouseButtonEvent, deltaX: Double, deltaY: Double) {
        super.onDrag(event.into(), deltaX, deltaY)
    }

    public open fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean = false): Boolean {
        return super.mouseClicked(event.into(), doubleClick)
    }

    public open fun mouseReleased(event: MouseButtonEvent): Boolean {
        return super.mouseReleased(event.into())
    }

    public open fun mouseDragged(event: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean {
        return super.mouseDragged(event.into(), deltaX, deltaY)
    }


    protected open fun isValidClickButton(info: MouseButtonInfo): Boolean {
        return super.isValidClickButton(info.into())
    }

    public open fun keyPressed(event: KeyEvent): Boolean {
        return super.keyPressed(event.into())
    }

    public open fun keyReleased(event: KeyEvent): Boolean {
        return super.keyReleased(event.into())
    }

    public open fun charTyped(event: CharacterEvent): Boolean {
        return super.charTyped(event.into())
    }

    public override fun onClick(event: McMouseButtonEvent, doubleClick: Boolean): Unit = onClick(event.into(), doubleClick)
    public override fun onRelease(event: McMouseButtonEvent): Unit = this.onRelease(event.into())
    public override fun onDrag(event: McMouseButtonEvent, deltaX: Double, deltaY: Double): Unit = onDrag(event.into(), deltaX, deltaY)
    override fun mouseClicked(event: McMouseButtonEvent, doubleClick: Boolean): Boolean = this.mouseClicked(event.into(), doubleClick)
    override fun mouseReleased(event: McMouseButtonEvent): Boolean = this.mouseReleased(event.into())
    override fun mouseDragged(event: McMouseButtonEvent, dragX: Double, dragY: Double): Boolean = this.mouseDragged(event.into(), dragX, dragY)
    override fun isValidClickButton(event: McMouseButtonInfo): Boolean = this.isValidClickButton(event.into())
    override fun keyPressed(event: McKeyEvent): Boolean = this.keyPressed(event.into())
    override fun keyReleased(event: McKeyEvent): Boolean = this.keyReleased(event.into())
    override fun charTyped(event: McCharacterEvent): Boolean = this.charTyped(event.into())
}
