package net.bloople.recipeimagesexporter

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.item.Item
import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO

class ItemLabelsExtractor(
    items: List<Item>,
    private val exportDir: Path,
    private val textRenderer: TextRenderer
) {
    private val chunks = items.chunked(chunkSize).mapIndexed { index, itemStacks -> Chunk(itemStacks, index) }

    lateinit var widths: Map<Item, Int>
    lateinit var labels: Map<Item, BufferedImage>

    fun exportLabels(client: MinecraftClient) {
        client.sendMessage("Generating labels")
        var labelsCount = 0
        for(chunk in chunks) {
            chunk.exportLabels()
            labelsCount += chunk.size
            client.sendMessage("Generated $labelsCount labels")
        }
    }

    fun importLabels() {
        widths = HashMap<Item, Int>().also {
            for(chunk in chunks) it += chunk.widths
        }

        labels = HashMap<Item, BufferedImage>().also {
            for(chunk in chunks) it += chunk.importLabels()
        }
    }

    private inner class Chunk(private val items: List<Item>, index: Int) {
        private val labelsPath = exportDir.resolve("labels_$index.png")
        val size = items.size
        val widths by lazy { items.associateWith { textRenderer.getWidth(it.name) * 2 } }

        fun exportLabels() {
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

        fun importLabels(): Map<Item, BufferedImage> {
            val labelsImage = ImageIO.read(labelsPath.toFile()).asARGB()

            return items.mapIndexed { index, item ->
                val y = index * 18
                val labelWidth = widths[item]!!

                val image = BufferedImage(labelWidth, 18, BufferedImage.TYPE_INT_ARGB).apply {
                    raster.setRect(0, 0, labelsImage.getData(0, y, width, height))
                }

                item to image
            }.toMap()
        }
    }

    companion object {
        private const val chunkSize = 100
    }
}
