package net.bloople.recipeimagesexporter

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
    private val itemStacks: List<ItemStack>,
    exportDir: Path,
    private val itemRenderer: ItemRenderer,
    private val textRenderer: TextRenderer
) {
    private val iconsPath = exportDir.resolve("icons.png")
    private val labelIconsPath = exportDir.resolve("label_icons.png")
    private val transparentIconsPath = exportDir.resolve("transparent_icons.png")
    private val iconsStride = ceil(sqrt(itemStacks.size.toDouble())).toInt()
    private var maskColorRed = 0
    private var maskColorGreen = 0
    private var maskColorBlue = 0
    private var maskColor = 0

    lateinit var slotIcons: Map<String, BufferedImage>
    lateinit var labelIcons: Map<String, BufferedImage>
    lateinit var transparentIcons: Map<String, BufferedImage>

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

    private fun findIconsMaskColor() {
        val iconsImage = ImageIO.read(iconsPath.toFile()).asARGB()

        val pixels = IntArray(iconsImage.width * iconsImage.height)
        for(x in 0 until iconsImage.width) {
            for(y in 0 until iconsImage.height) {
                pixels[x * y] = iconsImage.getRGB(x, y)
            }
        }

        maskColor = 0
        while(true) {
            maskColorRed = Random.nextUInt(256u).toInt()
            maskColorGreen = Random.nextUInt(256u).toInt()
            maskColorBlue = Random.nextUInt(256u).toInt()
            maskColor = 0xFF shl 24 or (maskColorRed shl 16) or (maskColorGreen shl 8) or (maskColorBlue shl 0)
            if(!pixels.contains(maskColor)) break
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
        findIconsMaskColor()
        exportIcons(maskColorRed / 255.0f, maskColorGreen / 255.0f, maskColorBlue / 255.0f, transparentIconsPath)
    }

    fun importIcons() {
        slotIcons = importIcons(iconsPath)
        labelIcons = importIcons(labelIconsPath)

        val unmaskedIconsImage = ImageIO.read(transparentIconsPath.toFile()).asARGB()
        applyMaskColor(unmaskedIconsImage)
        ImageIO.write(unmaskedIconsImage, "PNG", transparentIconsPath.toFile())
        transparentIcons = importIcons(transparentIconsPath)
    }

    fun extract() {
        exportIcons()
        importIcons()
    }
}