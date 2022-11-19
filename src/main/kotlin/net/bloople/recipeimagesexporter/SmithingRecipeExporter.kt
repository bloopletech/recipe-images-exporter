package net.bloople.recipeimagesexporter

import net.bloople.recipeimagesexporter.RecipeImagesExporterMod.LOGGER
import net.minecraft.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO


class SmithingRecipeExporter(
    private val recipeInfo: SmithingRecipeInfo,
    private val exportDir: Path,
    private val itemsData: ItemsData
) : Exporter {
    override fun export() {
        LOGGER.info("exporting ${recipeInfo.recipePath}")
        loadImages()

        val recipeFilePath = exportDir.resolve("${recipeInfo.recipePath}.png")
        Files.createDirectories(recipeFilePath.parent)

        val cropWidth = max(
            264 + rightImage.width,
            recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 44 + 8)
        val cropHeight = 154

        val outputImage = BufferedImage(cropWidth, cropHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, baseImage.getData(0, 0, width, height))
            raster.setRect(width - 2, 0, rightImage.getData(0, 0, 2, height))

            createGraphics().use {
                drawImage(itemsData.slotImage(recipeInfo.slotBase), 11, 11, null)
                drawImage(itemsData.slotImage(recipeInfo.slotAddition), 109, 11, null)
                drawImage(itemsData.outputImage(recipeInfo.output), 225, 11, null)

                val x = 8
                var y = 50

                for(item in recipeInfo.items) {
                    drawImage(itemsData.slotLabelImage(ItemStack(item, 1)), x, y, null)
                    drawImage(itemsData.labelImage(item), 44, y + 6, null)
                    y += 34
                }
            }
        }

        ImageIO.write(outputImage, "PNG", recipeFilePath.toFile())
    }

    companion object {
        private const val smithingLeftImage = "iVBORw0KGgoAAAANSUhEUgAAAgAAAACaCAIAAABQeGehAAAChklEQVR42u3dwUkEMRSA4bc69UwD00YaGUiBuYQpIiWkCQ9eRHBRJKMz+b6LoIL4EP/33F18tNYCgPm8GAHAnJb3N713swBwAQAgAAAIAAACAIAAAHBNy1cf2Pf9hC+fUoqIbdsuOj5TMltwAQBwlwvg4340Ts45Io7juPQQTclswQUAgABwX7XWWqs5gAAAcEnLjbfUT+/xTI8REzZVcAEAIABMdgd4PAAEAAABwB0ACAAAAoA7ABAAAAQAdwAgAACc58KvBP7pmvn88+/6ita/Wsa9ThhcAAAIAPPxeAAIAAD/zmIEjOMxAHABAOACOGu79P8Axn3X3/mzvt0fXAAAuABwcwAuAABcANj9ARcAAC4A7P6ACwAAFwB2f8AFAIALwGZqwoALAIDfXwAppYjIOZvRE3NO6Zzd308guAAAGOLRWouI3rtZALgAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAAABAEAAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAABAEAAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAAABAEAAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAEAAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAAABAEAAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAEAAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAAABAEAAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAEAAjABAAAAQAAAEAAABAEAAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAABGWiKilFJKMQuAqbyu6+q3P8CE3gAiBGqJSiETaQAAAABJRU5ErkJggg=="
        private lateinit var baseImage: BufferedImage
        private lateinit var rightImage: BufferedImage

        private fun loadImages() {
            if(!::baseImage.isInitialized) baseImage = ImageIO.read(decodeBase64(smithingLeftImage)).asARGB()
            if(!::rightImage.isInitialized) rightImage = ImageIO.read(decodeBase64(CraftingRecipeExporter.craftingRightImage)).asARGB()
        }
    }
}

