package net.bloople.recipeimagesexporter

import net.bloople.recipeimagesexporter.RecipeImagesExporterMod.LOGGER
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.awt.image.BufferedImage
import java.lang.Integer.max
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO


class StonecuttingRecipeExporter(
    private val recipeInfo: StonecuttingRecipeInfo,
    private val exportDir: Path,
    private val itemsData: ItemsData
) : Exporter {
    override fun export() {
        LOGGER.info("exporting ${recipeInfo.recipePath}")
        loadImages()

        val recipeFilePath = exportDir.resolve("${recipeInfo.recipePath}.png")
        Files.createDirectories(recipeFilePath.parent)

        val cropWidth = max(
            168 + rightImage.width,
            recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 44 + 8)
        val cropHeight = 164

        val outputImage = BufferedImage(cropWidth, cropHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, baseImage.getData(0, 0, width, height))
            raster.setRect(width - 2, 0, rightImage.getData(0, 0, 2, height))

            createGraphics().use {
                drawImage(itemsData.slotImage(recipeInfo.slot), 11, 47, null)
                drawImage(itemsData.slotImage(recipeInfo.slot), 54, 22, null)
                drawImage(itemsData.outputImage(recipeInfo.output), 119, 45, null)

                val x = 8
                var y = 94

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
        private const val stonecuttingLeftImage = "iVBORw0KGgoAAAANSUhEUgAAAgAAAACkCAIAAABuCAF8AAAC40lEQVR42u3dMW7iQBiG4R/kW3ENHyISFZLv4SIFN0BKT0czjTWipvQRfIkUNEkRCUWMGePnabbLRgPadz4Wks04jgHA+mwdAcA6Nfc/pmlyFgAWAAACAIAAACAAAAgAAAIAgAAAIAAACAAAAgCAAAAgAAAIAAACAEABTbXf2eFwmO3v6vveUwGwAACwACrw9flR6CunfIuI42nwJAAsAAAsgMrcb+vPdb5cPfyABQCAAAAgAAAIAAACAIAAACAAAAgAAAIAgAAAIAAACAAAAgCAAABQXuMInmXO32H5uLZtI2K323mAAAsAAAug2I27Hl3XRUTO2UMDWAAACACAAAAgAAAIAADv6893Ac3zrnbvUgewAACoYwH8vKGX413qABZAFYZhGIbBOQACAIAA2AEAAgCAANgBAAIAwKLU/uOgU75FxPlyfeEOCB9VAywAACyAmRxPVbwKbwcAFgAAFkBhfd/X9i3ZAYAFAIAFUOaWXTM7ALAAABCA9fE5YUAAAFikxhH8j/8DACwAACyA5d+sH3lZ390fsAAAsADWwd0fsAAAsADc/QEsAAAsAHd/AAsAAAvA3R/AAgDAAnD3B7AAALAA3P0BXhSAtm0jous6ZwTwlrwEBGAB/HZ/MSTn7IwALAAABAAAAQBAAAAQAADq5oNgT+OTE4AFAMACbMZxjIhpmpwFgAUAgAAAIAAACAAAAgCAAAAgAAAIAAACAIAAACAAAAgAAAIAgAAAIAAACAAAAgCAAAAgAAAIAAACAIAAAAgAAAIAgAAAIAAACAAAAgCAAAAgAAAIAAACAIAAACAAAAgAAAIAgAAAIAAACAAAAgCAAAAgAAAIAAACAIAAACAAAAIAgAAAIAAACAAAAgCAAAAgAAAIAAACAIAAACAAAAgAAAIAgAAAIAAACAAAAgCAAAAgAAAIAAACAIAAACAAAAIAgAAAIAAACAAA76ZJKaWUHATAquz3+61//QHW6Rtiu2pOgtDpeAAAAABJRU5ErkJggg=="
        private lateinit var baseImage: BufferedImage
        private lateinit var rightImage: BufferedImage

        private fun loadImages() {
            if(!::baseImage.isInitialized) baseImage = ImageIO.read(decodeBase64(stonecuttingLeftImage)).asARGB()
            if(!::rightImage.isInitialized) rightImage = ImageIO.read(decodeBase64(CraftingRecipeExporter.craftingRightImage)).asARGB()
        }
    }
}
