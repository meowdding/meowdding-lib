package me.owdding.lib.rendering

import earth.terrarium.olympus.client.pipelines.pips.OlympusPictureInPictureRenderState
import net.minecraft.client.gui.navigation.ScreenRectangle
import org.joml.Matrix3x2f

public abstract class MeowddingPipState<T : OlympusPictureInPictureRenderState<T>>() : OlympusPictureInPictureRenderState<T> {
    public abstract val x0: Int
    public abstract val y0: Int
    public abstract val x1: Int
    public abstract val y1: Int

    public open val scale: Float = 1f

    public abstract val scissorArea: ScreenRectangle?
    public abstract val pose: Matrix3x2f

    public open val shrinkToScissor: Boolean = true

    public open val bounds: ScreenRectangle? by lazy {
        if (scissorArea != null && shrinkToScissor) {
            scissorArea!!.intersection(ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose))
        } else {
            ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose)
        }
    }

    override fun x0(): Int = x0
    override fun y0(): Int = y0
    override fun x1(): Int = x1
    override fun y1(): Int = y1
    override fun scissorArea(): ScreenRectangle? = scissorArea
    override fun pose(): Matrix3x2f = pose
    override fun scale(): Float = scale
    override fun bounds(): ScreenRectangle? = bounds
}
