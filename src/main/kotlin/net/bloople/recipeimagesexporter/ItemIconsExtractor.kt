package net.bloople.recipeimagesexporter

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.item.ItemStack
import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.sqrt

class ItemIconsExtractor(
    private val itemStacks: List<ItemStack>,
    exportDir: Path,
    private val itemRenderer: ItemRenderer,
    private val textRenderer: TextRenderer
) {
    private val iconsPath = exportDir.resolve("icons.png")
    private val labelIconsPath = exportDir.resolve("label-icons.png")
    private val iconsStride = ceil(sqrt(itemStacks.size.toDouble())).toInt()

    lateinit var icons: Map<String, BufferedImage>
    lateinit var labelIcons: Map<String, BufferedImage>

    private fun exportIcons(r: Float, g: Float, b: Float, path: Path) {
        val width = iconsStride * 34
        val height = iconsStride * 34
        val scaledWidth = iconsStride * 17

        val nativeImage = renderToTexture(width, height, r, g, b, 2.0) {
            var x = 0
            var y = 0
            for(itemStack in itemStacks) {
                itemRenderer.renderInGui(itemStack, x, y)
                itemRenderer.renderGuiItemOverlay(textRenderer, itemStack, x, y)

                x += 17
                if(x >= scaledWidth) {
                    x = 0
                    y += 17
                }
            }
        }

        nativeImage.use { it.writeTo(path.toFile()) }
    }

    private fun importIcons(path: Path): Map<String, BufferedImage> {
        val iconsImage = ImageIO.read(path.toFile()).asARGB()
        val icons = HashMap<String, BufferedImage>()

        for((index, itemStack) in itemStacks.withIndex()) {
            val x = (index % iconsStride) * 34
            val y = (index / iconsStride) * 34

            val image = BufferedImage(34, 34, BufferedImage.TYPE_INT_ARGB).apply {
                raster.setRect(0, 0, iconsImage.getData(x, y, width, height))
            }

            icons[itemStack.uniqueKey] = image
        }

        return icons
    }

    fun exportIcons() {
        exportIcons(0.5451f, 0.5451f, 0.5451f, iconsPath)
        exportIcons(0.7765f, 0.7765f, 0.7765f, labelIconsPath)
    }

    fun importIcons() {
        icons = importIcons(iconsPath)
        labelIcons = importIcons(labelIconsPath)
    }

    fun extract() {
        exportIcons()
        importIcons()
    }
}