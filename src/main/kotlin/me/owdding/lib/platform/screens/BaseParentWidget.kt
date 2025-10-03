package me.owdding.lib.platform.screens

import earth.terrarium.olympus.client.components.base.BaseParentWidget
import net.msrandom.stub.Stub

@Stub
expect abstract class BaseParentWidget : BaseParentWidget {
    constructor()
    constructor(width: Int, height: Int)

    open fun onClick(event: MouseButtonEvent, doubleClick: Boolean)
    open fun onRelease(event: MouseButtonEvent)
    open fun onDrag(event: MouseButtonEvent, deltaX: Double, deltaY: Double)

    open fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean = false): Boolean
    open fun mouseReleased(event: MouseButtonEvent): Boolean
    open fun mouseDragged(event: MouseButtonEvent, deltaX: Double, deltaY: Double): Boolean

    protected open fun isValidClickButton(info: MouseButtonInfo): Boolean

    open fun keyPressed(event: KeyEvent): Boolean
    open fun keyReleased(event: KeyEvent): Boolean
    open fun charTyped(event: CharacterEvent): Boolean

}
