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
        private const val smithingLeftImage = "iVBORw0KGgoAAAANSUhEUgAAAgAAAACaCAIAAABQeGehAAAChElEQVR42u3dzU3EMBCA0VlIPWkgbaSRSC7QFytFuAQ3wRUhseJHDjh+7whIiBHim2GJeNRaAxhZa80Q+IEXIwCY02KDAHABACAAAAgAAAIAgAAAMLjls3ccx3HBp9/3PSK2bRt0fKZktuACAOAuF8D7/aiflFJEnOc59BBNyWzBBQCAAHBfpZRSijmAAAAwpOXGW+qHt/hLjx4TNlVwAQAgAEx2B3g9AAQAAAHAHQAIAAACgDsAEAAABAB3ACAAAFxn4CeBv7tmPv/4uz7R+lfLuOeEwQUAgAAwH68HgAAA8O8sRkA/XgMAFwAALoCrtkv/D6DfV/2VX+vb/cEFAIALADcH4AIAwAWA3R9wAQDgAsDuD7gAAHABYPcHXAAAuABspiYMuAAA+P0FsO97RKSUzOiJOad0ze7vOxBcAAB08ai1RkRrzSwAXAAACAAAAgCAAAAgAAAIAAACAIAAACAAAAgAAAIAgAAAIAAACAAAAgCAAAAgAAAIAAACAIAAACAAAAgAgAAAIAAACAAAAgCAAAAgAAAIAAACAIAAACAAAAgAAAIAgAAAIAAACAAAAgCAAAAgAAAIAAACAIAAACAAAAgAAAIAIAAACAAAAgCAAAAgAAAIAAACAIAAACAAAAgAAAIAgAAAIAAACAAAAgCAAAAgAAAIAAACAIAAACAAAAgAAAIAIAAACAAAAgCAAAAgAAAIAAACAIAAACAAAAgAAAIAgAAAIAAACAAAAgCAAAAgAAAIAAACAIAAACAAAAgAAAIAIABGACAAAAgAAAIAgAAAIAAACAAAAgCAAAAgAAAIAAACAIAAACAAAAgAAD0tEZFzzjmbBcBUXtd19dMfYEJvkO5tSrmALx0AAAAASUVORK5CYII="
        private lateinit var baseImage: BufferedImage
        private lateinit var rightImage: BufferedImage

        private fun loadImages() {
            if(!::baseImage.isInitialized) baseImage = ImageIO.read(decodeBase64(smithingLeftImage)).asARGB()
            if(!::rightImage.isInitialized) rightImage = ImageIO.read(decodeBase64(CraftingRecipeExporter.craftingRightImage)).asARGB()
        }
    }
}

