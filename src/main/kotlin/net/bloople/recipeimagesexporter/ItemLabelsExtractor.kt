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
    private val chunkedItems: List<List<Item>> = items.chunked(chunkSize)
    private val labelsPaths = ArrayList<Path>()

    val widths = HashMap<Item, Int>()
    val labels = HashMap<Item, BufferedImage>()

    private fun exportLabels(items: List<Item>, path: Path) {
        widths += items.associateWith { textRenderer.getWidth(it.name) * 2 }

        val width = widths.values.max()
        val height = items.size * 18

        val nativeImage = renderToTexture(width, height, 0.7765f, 0.7765f, 0.7765f, 2.0) {
            var y = 0
            for(item in items) {
                renderText(textRenderer, item.name, 0, y, 0x000000)
                y += 9
            }
        }

        nativeImage.use { it.writeTo(path.toFile()) }
    }

    fun exportLabels(client: MinecraftClient) {
        client.sendMessage("Generating labels")
        var labelsCount = 0
        for((index, chunk) in chunkedItems.withIndex()) {
            val labelsPath = exportDir.resolve("labels_$index.png")
            exportLabels(chunk, labelsPath)
            labelsPaths.add(labelsPath)
            labelsCount += chunk.size
            client.sendMessage("Generated $labelsCount labels")
        }
    }

    fun importLabels() {
        for((chunkIndex, labelsPath) in labelsPaths.withIndex()) {
            val labelsImage = ImageIO.read(labelsPath.toFile()).asARGB()

            for((index, item) in chunkedItems[chunkIndex].withIndex()) {
                val y = index * 18
                val labelWidth = widths[item]!!

                val image = BufferedImage(labelWidth, 18, BufferedImage.TYPE_INT_ARGB).apply {
                    raster.setRect(0, 0, labelsImage.getData(0, y, width, height))
                }

                labels[item] = image
            }
        }
    }

    companion object {
        private const val chunkSize = 100
    }
}