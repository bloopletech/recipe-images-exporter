package net.bloople.recipeimagesexporter

import net.minecraft.client.font.TextRenderer
import net.minecraft.item.Item
import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO

class ItemLabelsExtractor(
    private val items: List<Item>,
    private val labelsPath: Path,
    private val textRenderer: TextRenderer
) {
    lateinit var widths: Map<Item, Int>
    val labels = HashMap<Item, BufferedImage>()

    fun exportLabels() {
        widths = items.associateWith { textRenderer.getWidth(it.name) * 2 }

        val width = widths.values.max()
        val height = items.size * 18

        val nativeImage = renderToTexture(width, height, 0.7765f, 0.7765f, 0.7765f, 2.0) {
            var y = 0
            for(item in items) {
                renderText(textRenderer, item.name, 0, y, 0x000000)
                y += 9
            }
        }

        nativeImage.use { it.writeTo(labelsPath.toFile()) }
    }

    fun importLabels() {
        val labelsImage = ImageIO.read(labelsPath.toFile()).asARGB()

        for((index, item) in items.withIndex()) {
            val y = index * 18
            val labelWidth = widths[item]!!

            val image = BufferedImage(labelWidth, 18, BufferedImage.TYPE_INT_ARGB).apply {
                raster.setRect(0, 0, labelsImage.getData(0, y, width, height))
            }

            labels[item] = image
        }
    }

    fun extract() {
        exportLabels()
        importLabels()
    }
}