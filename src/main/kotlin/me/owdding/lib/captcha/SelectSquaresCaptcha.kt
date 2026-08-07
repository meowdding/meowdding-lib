package me.owdding.lib.captcha

import me.owdding.lib.MeowddingLib
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import org.joml.Vector2i

class SelectSquaresCaptcha(val entry: Entry) : CaptchaWidget(CaptchaType.SELECT_SQUARES, entry.description) {
    private val selectedSquares = mutableSetOf<Vector2i>()

    private val gridDimension = 8
    private val squareSize = 256 / gridDimension

    override fun extractWidgetRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTicks: Float) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, entry.texture, this.x, this.y, this.width, this.height)

        for (square in selectedSquares) {
            val drawX = this.x + (square.x * squareSize)
            val drawY = this.y + (square.y * squareSize)

            graphics.fill(drawX, drawY, drawX + squareSize, drawY + squareSize, 0x8000FF00.toInt())
        }

        for (i in 1 until gridDimension) {
            val drawX = this.x + (i * squareSize) - 1
            val drawY = this.y + (i * squareSize) - 1

            graphics.fill(drawX, this.y, drawX + 2, this.y + this.width, 0x80000000.toInt())
            graphics.fill(this.x, drawY, this.x + this.width, drawY + 2, 0x80000000.toInt())
        }
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        val mouseX = event.x
        val mouseY = event.y
        if (mouseX >= this.x && mouseX < this.x + 256 && mouseY >= this.y && mouseY < this.y + 256) {
            val relativeX = mouseX - this.x
            val relativeY = mouseY - this.y

            val gridX = (relativeX / squareSize).toInt()
            val gridY = (relativeY / squareSize).toInt()

            val clickedSquare = Vector2i(gridX, gridY)
            toggleSquareSelection(clickedSquare)

            return true
        }

        return super.mouseClicked(event, doubleClick)
    }

    private fun toggleSquareSelection(square: Vector2i) {
        if (!selectedSquares.add(square)) {
            selectedSquares.remove(square)
        }
    }

    override fun isCorrect() = selectedSquares.size == entry.solution.size && selectedSquares.containsAll(entry.solution)

    companion object : CaptchaHandler() {
        private val captchas = listOf(
            Entry(
                "polar_bear",
                "Select all Squares containing a Polar Bear",
                listOf(
                    Vector2i(0, 4), Vector2i(0, 5), Vector2i(0, 6),
                    Vector2i(1, 4), Vector2i(1, 5), Vector2i(1, 6),
                    Vector2i(2, 4), Vector2i(2, 5), Vector2i(2, 6),
                    Vector2i(3, 4), Vector2i(3, 5), Vector2i(3, 6),
                    Vector2i(4, 5), Vector2i(4, 6),
                    Vector2i(5, 5), Vector2i(5, 6),
                ),
            ),
            Entry(
                "hide_on_leaf",
                "Select all Squares containing a HideOnLeaf",
                listOf(Vector2i(5, 2))
            )
        ).toList()

        override fun selectRandom(): CaptchaWidget {
            val captcha = captchas.random()
            return SelectSquaresCaptcha(captcha)
        }

        data class Entry(val id: String, val description: String, val solution: List<Vector2i>) {
            val texture = MeowddingLib.id("captcha/select_squares/$id")
        }
    }
}
