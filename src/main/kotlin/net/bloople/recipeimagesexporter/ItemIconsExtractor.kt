package net.bloople.recipeimagesexporter

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.item.ItemStack
import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.random.nextUInt

class ItemIconsExtractor(
    itemStacks: List<ItemStack>,
    private val exportDir: Path,
    private val itemRenderer: ItemRenderer,
    private val textRenderer: TextRenderer
) {
    private val chunks = itemStacks.chunked(chunkSize).mapIndexed { index, itemStacks -> Chunk(itemStacks, index) }
    private var maskColorRed = 0
    private var maskColorGreen = 0
    private var maskColorBlue = 0
    private var maskColor = 0

    lateinit var slotIcons: Map<String, BufferedImage>
    lateinit var labelIcons: Map<String, BufferedImage>
    lateinit var transparentIcons: Map<String, BufferedImage>

    private fun findIconsMaskColor() {
        val pixelsPixels = chunks.map { it.getPixels() }

        maskColor = 0
        outer@ while(true) {
            maskColorRed = Random.nextUInt(256u).toInt()
            maskColorGreen = Random.nextUInt(256u).toInt()
            maskColorBlue = Random.nextUInt(256u).toInt()
            maskColor = 0xFF shl 24 or (maskColorRed shl 16) or (maskColorGreen shl 8) or (maskColorBlue shl 0)

            for(pixels in pixelsPixels) {
                if(pixels.contains(maskColor)) continue@outer
            }
            break
        }
    }

    private fun applyMaskColor(image: BufferedImage) {
        image.apply {
            for(x in 0 until width) {
                for(y in 0 until height) {
                    if(getRGB(x, y) == maskColor) setRGB(x, y, 0)
                }
            }
        }
    }

    fun exportIcons(client: MinecraftClient) {
        client.sendMessage("Generating icons")
        var iconsCount = 0
        for(chunk in chunks) {
            chunk.exportIcons(0.5451f, 0.5451f, 0.5451f, chunk.iconsPath)
            iconsCount += chunk.size
            client.sendMessage("Generated $iconsCount icons")
        }

        client.sendMessage("Generating label icons")
        var labelIconsCount = 0
        for(chunk in chunks) {
            chunk.exportIcons(0.7765f, 0.7765f, 0.7765f, chunk.labelIconsPath)
            labelIconsCount += chunk.size
            client.sendMessage("Generated $iconsCount label icons")
        }

        findIconsMaskColor()

        client.sendMessage("Generating transparent icons")
        var transparentIconsCount = 0
        for(chunk in chunks) {
            chunk.exportIcons(
                maskColorRed / 255.0f,
                maskColorGreen / 255.0f,
                maskColorBlue / 255.0f,
                chunk.transparentIconsPath
            )
            transparentIconsCount += chunk.size
            client.sendMessage("Generated $transparentIconsCount transparent icons")
        }
    }

    fun importIcons() {
        slotIcons = HashMap<String, BufferedImage>().also {
            for(chunk in chunks) it += chunk.importIcons(chunk.iconsPath)
        }

        labelIcons = HashMap<String, BufferedImage>().also {
            for(chunk in chunks) it += chunk.importIcons(chunk.labelIconsPath)
        }

        for(chunk in chunks) {
            val unmaskedIconsImage = ImageIO.read(chunk.transparentIconsPath.toFile()).asARGB()
            applyMaskColor(unmaskedIconsImage)
            ImageIO.write(unmaskedIconsImage, "PNG", chunk.transparentIconsPath.toFile())
        }

        transparentIcons = HashMap<String, BufferedImage>().also {
            for(chunk in chunks) it += chunk.importIcons(chunk.transparentIconsPath)
        }
    }

    inner class Chunk(private val itemStacks: List<ItemStack>, index: Int) {
        internal val iconsPath = exportDir.resolve("icons_$index.png")
        internal val labelIconsPath = exportDir.resolve("label_icons_$index.png")
        internal val transparentIconsPath = exportDir.resolve("transparent_icons_$index.png")
        private val iconsStride = ceil(sqrt(itemStacks.size.toDouble())).toInt()
        internal val size = itemStacks.size

        internal fun exportIcons(r: Float, g: Float, b: Float, path: Path) {
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

        internal fun getPixels(): IntArray {
            val iconsImage = ImageIO.read(iconsPath.toFile()).asARGB()

            return IntArray(iconsImage.width * iconsImage.height).apply {
                for(x in 0 until iconsImage.width) {
                    for(y in 0 until iconsImage.height) {
                        this[x * y] = iconsImage.getRGB(x, y)
                    }
                }
            }
        }

        internal fun importIcons(path: Path): Map<String, BufferedImage> {
            val iconsImage = ImageIO.read(path.toFile()).asARGB()

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
