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
    private val chunkedItemStacks: List<List<ItemStack>> = itemStacks.chunked(chunkSize)
    private val iconsPaths = ArrayList<Path>()
    private val labelIconsPaths = ArrayList<Path>()
    private val transparentIconsPaths = ArrayList<Path>()
    private val iconsStrides = ArrayList<Int>()
    private var maskColorRed = 0
    private var maskColorGreen = 0
    private var maskColorBlue = 0
    private var maskColor = 0

    lateinit var slotIcons: Map<String, BufferedImage>
    lateinit var labelIcons: Map<String, BufferedImage>
    lateinit var transparentIcons: Map<String, BufferedImage>

    private fun exportIcons(r: Float, g: Float, b: Float, iconsStride: Int, itemStacks: List<ItemStack>, path: Path) {
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

    private fun findIconsMaskColor() {
        val pixelsPixels = ArrayList<IntArray>(iconsPaths.size)

        for(iconsPath in iconsPaths) {
            val iconsImage = ImageIO.read(iconsPath.toFile()).asARGB()

            val pixels = IntArray(iconsImage.width * iconsImage.height)
            for(x in 0 until iconsImage.width) {
                for(y in 0 until iconsImage.height) {
                    pixels[x * y] = iconsImage.getRGB(x, y)
                }
            }
            pixelsPixels.add(pixels)
        }

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

    private fun importIcons(paths: List<Path>): Map<String, BufferedImage> {
        val icons = HashMap<String, BufferedImage>()

        for((chunkIndex, iconsPath) in paths.withIndex()) {
            val iconsImage = ImageIO.read(iconsPath.toFile()).asARGB()

            val iconsStride = iconsStrides[chunkIndex]
            for((index, itemStack) in chunkedItemStacks[chunkIndex].withIndex()) {
                val x = (index % iconsStride) * 34
                val y = (index / iconsStride) * 34

                val image = BufferedImage(34, 34, BufferedImage.TYPE_INT_ARGB).apply {
                    raster.setRect(0, 0, iconsImage.getData(x, y, width, height))
                }

                icons[itemStack.uniqueKey] = image
            }
        }

        return icons
    }

    fun exportIcons(client: MinecraftClient) {
        client.sendMessage("Generating icons")
        var iconsCount = 0
        for((index, chunk) in chunkedItemStacks.withIndex()) {
            val iconsPath = exportDir.resolve("icons_$index.png")
            val iconsStride = ceil(sqrt(chunk.size.toDouble())).toInt()
            exportIcons(0.5451f, 0.5451f, 0.5451f, iconsStride, chunk, iconsPath)
            iconsPaths.add(iconsPath)
            iconsStrides.add(iconsStride)
            iconsCount += chunk.size
            client.sendMessage("Generated $iconsCount icons")
        }

        client.sendMessage("Generating label icons")
        var labelIconsCount = 0
        for((index, chunk) in chunkedItemStacks.withIndex()) {
            val labelIconsPath = exportDir.resolve("label_icons_$index.png")
            exportIcons(0.7765f, 0.7765f, 0.7765f, iconsStrides[index], chunk, labelIconsPath)
            labelIconsPaths.add(labelIconsPath)
            labelIconsCount += chunk.size
            client.sendMessage("Generated $labelIconsCount label icons")
        }

        findIconsMaskColor()

        client.sendMessage("Generating transparent icons")
        var transparentIconsCount = 0
        for((index, chunk) in chunkedItemStacks.withIndex()) {
            val transparentIconsPath = exportDir.resolve("transparent_icons_$index.png")
            exportIcons(
                maskColorRed / 255.0f,
                maskColorGreen / 255.0f,
                maskColorBlue / 255.0f,
                iconsStrides[index],
                chunk,
                transparentIconsPath
            )
            transparentIconsPaths.add(transparentIconsPath)
            transparentIconsCount += chunk.size
            client.sendMessage("Generated $transparentIconsCount transparent icons")
        }
    }

    fun importIcons() {
        slotIcons = importIcons(iconsPaths)
        labelIcons = importIcons(labelIconsPaths)

        for(transparentIconPath in transparentIconsPaths) {
            val unmaskedIconsImage = ImageIO.read(transparentIconPath.toFile()).asARGB()
            applyMaskColor(unmaskedIconsImage)
            ImageIO.write(unmaskedIconsImage, "PNG", transparentIconPath.toFile())
        }
        transparentIcons = importIcons(transparentIconsPaths)
    }

    companion object {
        private const val chunkSize = 1000
    }
}