package me.owdding.lib.rendering

import earth.terrarium.olympus.client.pipelines.pips.OlympusPictureInPictureRenderState
import net.minecraft.client.gui.navigation.ScreenRectangle
import org.joml.Matrix3x2f

abstract class MeowddingPipState<T : OlympusPictureInPictureRenderState<T>>() : OlympusPictureInPictureRenderState<T> {
    abstract val x0: Int
    abstract val y0: Int
    abstract val x1: Int
    abstract val y1: Int

    open val scale: Float = 1f

    abstract val scissorArea: ScreenRectangle?
    abstract val pose: Matrix3x2f

    open val shrinkToScissor: Boolean = true

    open val bounds: ScreenRectangle? by lazy {
        if (scissorArea != null && shrinkToScissor) {
            scissorArea!!.intersection(ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose))
        } else {
            ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose)
        }
    }

    override fun x0() = x0
    override fun y0() = y0
    override fun x1() = x1
    override fun y1() = y1
    override fun scissorArea() = scissorArea
    override fun pose() = pose
    override fun scale() = scale
    override fun bounds() = bounds
}
