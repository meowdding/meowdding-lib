package me.owdding.lib.layouts

import earth.terrarium.olympus.client.components.compound.LayoutWidget
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.client.gui.layouts.LayoutElement

fun LayoutElement.asWidget(): AbstractWidget = when (this) {
    is AbstractWidget -> this
    is Layout -> LayoutWidget(this).withStretchToContentSize()
    else -> throw IllegalArgumentException("Cant convert $this into a widget")
}

fun Layout.setPos(x: Int, y: Int): Layout {
    this.setPosition(x, y)
    return this
}

fun LayoutElement.withPadding(padding: Int): AbstractWidget = PaddedWidget(this, padding)
fun LayoutElement.withPadding(paddingTop: Int, paddingRight: Int, paddingBottom: Int, paddingLeft: Int): AbstractWidget =
    PaddedWidget(this, paddingTop, paddingRight, paddingBottom, paddingLeft)
