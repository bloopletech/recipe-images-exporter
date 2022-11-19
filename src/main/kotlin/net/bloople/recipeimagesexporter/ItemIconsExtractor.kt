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
    private val iconsPath: Path,
    private val itemRenderer: ItemRenderer,
    private val textRenderer: TextRenderer
) {
    private val iconsStride = ceil(sqrt(itemStacks.size.toDouble())).toInt()

    val icons = HashMap<String, BufferedImage>()

    fun exportIcons() {
        val width = iconsStride * 34
        val height = iconsStride * 34
        val scaledWidth = iconsStride * 17

        val nativeImage = renderToTexture(width, height, 0.5451f, 0.5451f, 0.5451f, 2.0) {
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

        nativeImage.use { it.writeTo(iconsPath.toFile()) }
    }

    fun importIcons() {
        val iconsImage = ImageIO.read(iconsPath.toFile()).asARGB()

        for((index, itemStack) in itemStacks.withIndex()) {
            val x = (index % iconsStride) * 34
            val y = (index / iconsStride) * 34

            val image = BufferedImage(34, 34, BufferedImage.TYPE_INT_ARGB).apply {
                raster.setRect(0, 0, iconsImage.getData(x, y, width, height))
            }

            icons[itemStack.uniqueKey] = image
        }
    }

    fun extract() {
        exportIcons()
        importIcons()
    }
}