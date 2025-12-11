package me.owdding.lib.platform.screens

import earth.terrarium.olympus.client.components.base.BaseWidget

//? if > 1.21.8 {
import net.minecraft.client.input.CharacterEvent as McCharacterEvent
import net.minecraft.client.input.KeyEvent as McKeyEvent
import net.minecraft.client.input.MouseButtonEvent as McMouseButtonEvent
import net.minecraft.client.input.MouseButtonInfo as McMouseButtonInfo

//?}

abstract class BaseWidget : BaseWidget {
    constructor() : super()
    constructor(width: Int, height: Int) : super(width, height)

    //? if <= 1.21.8 {
    /*private var lastClickTime = 0L
    private var lastButton = 0
     *///?}

    open fun onClick(event: MouseButtonEvent, doubleClick: Boolean) {
        //? if > 1.21.8 {
        super.onClick(event.into(), doubleClick)
        //?} else
        /*super.onClick(event.x, event.y)*/
    }

    open fun onRelease(event: MouseButtonEvent) {
        //? if > 1.21.8 {
        super.onRelease(event.into())
        //?} else
        /*super.onRelease(event.x, event.y)*/
    }

    open fun onDrag(event: MouseButtonEvent, deltaX: Double, deltaY: Double) {
        //? if > 1.21.8 {
        super.onDrag(event.into(), deltaX, deltaY)
        //?} else
        /*super.onDrag(event.x, event.y, deltaX, deltaY)*/
    }

    open fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        //? > 1.21.8 {
        return super.mouseClicked(event.into(), doubleClick)
        //?} else
        /*return super.mouseClicked(event.x, event.y, event.button)*/
    }

    open fun mouseReleased(event: MouseButtonEvent): Boolean {
        //? if > 1.21.8 {
        return super.mouseReleased(event.into())
        //?} else
        /*return super.mouseReleased(event.x, event.y, event.button)*/
    }

    open fun mouseDragged(event: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean {
        //? if > 1.21.8 {
        return super.mouseDragged(event.into(), deltaX, deltaY)
        //?} else
        /*return super.mouseDragged(event.x, event.y, event.button, deltaX, deltaY)*/
    }


    protected open fun isValidClickButton(info: MouseButtonInfo): Boolean {
        //? if > 1.21.8 {
        return super.isValidClickButton(info.into())
        //?} else
        /*return super.isValidClickButton(info.button)*/
    }

    open fun keyPressed(event: KeyEvent): Boolean {
        //? if > 1.21.8 {
        return super.keyPressed(event.into())
        //?} else
        /*return super.keyPressed(event.key, event.scancode, event.modifiers)*/
    }

    open fun keyReleased(event: KeyEvent): Boolean {
        //? if > 1.21.8 {
        return super.keyReleased(event.into())
        //?} else
        /*return super.keyReleased(event.key, event.scancode, event.modifiers)*/
    }

    open fun charTyped(event: CharacterEvent): Boolean {
        //? if > 1.21.8 {
        return super.charTyped(event.into())
        //?} else
        /*return super.charTyped(event.codepoint.toChar(), event.modifiers)*/
    }


    //? if > 1.21.8 {
    override fun onClick(event: McMouseButtonEvent, doubleClick: Boolean) = onClick(event.into(), doubleClick)
    override fun onRelease(event: McMouseButtonEvent) = this.onRelease(event.into())
    override fun onDrag(event: McMouseButtonEvent, deltaX: Double, deltaY: Double) = onDrag(event.into(), deltaX, deltaY)
    override fun mouseClicked(event: McMouseButtonEvent, doubleClick: Boolean): Boolean = this.mouseClicked(event.into(), doubleClick)
    override fun mouseReleased(event: McMouseButtonEvent): Boolean = this.mouseReleased(event.into())
    override fun mouseDragged(event: McMouseButtonEvent, dragX: Double, dragY: Double): Boolean = this.mouseDragged(event.into(), dragX, dragY)
    override fun isValidClickButton(event: McMouseButtonInfo): Boolean = this.isValidClickButton(event.into())
    override fun keyPressed(event: McKeyEvent): Boolean = this.keyPressed(event.into())
    override fun keyReleased(event: McKeyEvent): Boolean = this.keyReleased(event.into())
    override fun charTyped(event: McCharacterEvent): Boolean = this.charTyped(event.into())
    //?} else {
    /*override fun onClick(mouseX: Double, mouseY: Double) {
        val isDoubleClick = System.currentTimeMillis() - lastClickTime < 250
        this.onClick(mouseButtonEvent(mouseX, mouseY, lastButton), isDoubleClick)
    }
    override fun onRelease(mouseX: Double, mouseY: Double) = onRelease(mouseButtonEvent(mouseX, mouseY, lastButton))
    override fun onDrag(mouseX: Double, mouseY: Double, deltaX: Double, deltaY: Double) = onDrag(mouseButtonEvent(mouseX, mouseY, lastButton), deltaX, deltaY)
    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val isDoubleClick = System.currentTimeMillis() - lastClickTime < 250 && lastButton == button
        lastButton = button
        lastClickTime = System.currentTimeMillis()
        return this.mouseClicked(mouseButtonEvent(mouseX, mouseY, button), isDoubleClick)
    }
    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean = this.mouseReleased(mouseButtonEvent(mouseX, mouseY, button))
    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean = this.mouseDragged(mouseButtonEvent(mouseX, mouseY, button), dragX, dragY)
    override fun isValidClickButton(button: Int): Boolean = this.isValidClickButton(MouseButtonInfo(button, 0))
    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int) = keyPressed(keyEvent(keyCode.toChar(), scanCode, modifiers))
    override fun keyReleased(keyCode: Int, scanCode: Int, modifiers: Int) = this.keyReleased(keyEvent(keyCode.toChar(), scanCode, modifiers))
    override fun charTyped(codePoint: Char, modifiers: Int) = this.charTyped(characterEvent(codePoint, modifiers))

    *///?}
}
