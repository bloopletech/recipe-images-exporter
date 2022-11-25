package net.bloople.recipeimagesexporter

import net.bloople.recipeimagesexporter.CraftingRecipeImageGenerator.Companion.slotBackground
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.item.ItemStack
import java.awt.Color
import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.sqrt

class ItemIconsExtractor(
    itemStacks: List<ItemStack>,
    private val exportDir: Path,
    private val itemRenderer: ItemRenderer,
    private val textRenderer: TextRenderer
) {
    private val chunks = itemStacks.chunked(chunkSize).mapIndexed { index, itemStacks -> Chunk(itemStacks, index) }
    lateinit var icons: Map<String, BufferedImage>

    fun exportIcons(client: MinecraftClient) {
        client.sendMessage("Generating icons")
        var iconsCount = 0
        for(chunk in chunks) {
            chunk.exportIcons(slotBackground)
            chunk.applyMaskColor()
            iconsCount += chunk.size
            client.sendMessage("Generated $iconsCount icons")
        }
    }

    fun importIcons() {
        for(chunk in chunks) chunk.eraseMaskColor()
        icons = HashMap<String, BufferedImage>().also {
            for(chunk in chunks) it += chunk.importIcons()
        }
    }

    private inner class Chunk(private val itemStacks: List<ItemStack>, index: Int) {
        private val iconsPath = exportDir.resolve("icons_$index.png")
        private val iconsStride = ceil(sqrt(itemStacks.size.toDouble())).toInt()
        val size = itemStacks.size
        private lateinit var maskColor: Color

        fun exportIcons(background: Color) {
            val width = iconsStride * 34
            val height = iconsStride * 34
            val scaledWidth = iconsStride * 17
            val r = background.red / 255.0f
            val g = background.green / 255.0f
            val b = background.blue / 255.0f

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

            nativeImage.use { it.writeTo(iconsPath.toFile()) }
        }

        private fun getImage(): BufferedImage {
            return ImageIO.read(iconsPath.toFile()).asARGB()
        }

        fun applyMaskColor() {
            maskColor = findMaskColor(getImage())
            exportIcons(maskColor)
        }

        fun eraseMaskColor() {
            val maskedImage = getImage().apply { applyMaskColor(maskColor, this) }
            ImageIO.write(maskedImage, "PNG", iconsPath.toFile())
        }

        fun importIcons(): Map<String, BufferedImage> {
            val iconsImage = ImageIO.read(iconsPath.toFile()).asARGB()

            return itemStacks.mapIndexed { index, itemStack ->
                val x = (index % iconsStride) * 34
                val y = (index / iconsStride) * 34

                val image = BufferedImage(34, 34, BufferedImage.TYPE_INT_ARGB).apply {
                    raster.setRect(0, 0, iconsImage.getData(x, y, width, height))
                }

                itemStack.uniqueKey to image
            }.toMap()
        }
    }

    companion object {
        private const val chunkSize = 1000
    }
}
