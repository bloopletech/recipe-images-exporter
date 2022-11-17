package net.bloople.recipeimageexporter;

import net.bloople.recipeimageexporter.RecipeImageExporterMod.LOGGER
import net.minecraft.item.ItemStack
import java.awt.image.BufferedImage
import java.nio.file.Files
import javax.imageio.ImageIO


class CraftingRecipeExporter(
    private val recipeInfo: CraftingRecipeInfo,
    private val recipesExporter: RecipesExporter
) : Exporter {
    override fun export() {
        LOGGER.info("exporting ${recipeInfo.recipePath}")
        val recipeFilePath = recipesExporter.exportDir.resolve("${recipeInfo.recipePath}.png")
        Files.createDirectories(recipeFilePath.parent)

        val image = ImageIO.read(decodeBase64(craftingRecipeImage)).asARGB()

        image.createGraphics().use {
            if(recipeInfo.slot1 != null) drawImage(slotImage(recipeInfo.slot1), 11, 11, null)
            if(recipeInfo.slot2 != null) drawImage(slotImage(recipeInfo.slot2), 47, 11, null)
            if(recipeInfo.slot3 != null) drawImage(slotImage(recipeInfo.slot3), 83, 11, null)
            if(recipeInfo.slot4 != null) drawImage(slotImage(recipeInfo.slot4), 11, 47, null)
            if(recipeInfo.slot5 != null) drawImage(slotImage(recipeInfo.slot5), 47, 47, null)
            if(recipeInfo.slot6 != null) drawImage(slotImage(recipeInfo.slot6), 83, 47, null)
            if(recipeInfo.slot7 != null) drawImage(slotImage(recipeInfo.slot7), 11, 83, null)
            if(recipeInfo.slot8 != null) drawImage(slotImage(recipeInfo.slot8), 47, 83, null)
            if(recipeInfo.slot9 != null) drawImage(slotImage(recipeInfo.slot9), 83, 83, null)
            drawImage(outputImage(recipeInfo.output), 166, 46, null)
        }

        ImageIO.write(image, "PNG", recipeFilePath.toFile())
    }

    private fun slotImage(itemStack: ItemStack): BufferedImage {
        return recipesExporter.getIcon(itemStack).scaleImage(30, 30)
    }

    private fun outputImage(itemStack: ItemStack): BufferedImage {
        return recipesExporter.getIcon(itemStack)
    }

    companion object {
        const val craftingRecipeImage = "iVBORw0KGgoAAAANSUhEUgAAANgAAAB8BAMAAAD5g2MmAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAVUExURdvb25ubm1tbW8bGxjc3N4uLi////9MBu0EAAAAJcEhZcwAADsIAAA7CARUoSoAAAAC7SURBVGje7dexDcUgDEVRskJGYAWvkBW8AvuP8JtfRKAECmTBy72lKU4RiOQU2BGJnSlZWBkMrI9ddT4y2QLzqjIyAQMDAwMDAwMDAwOru57y7TF/qIDNxbzUjUx2wOatucthzRFX/5/rYq6LuS7mupgrYdVEF3NZTOib6b6zb/wbTRUzVcxUMQMzdmp26h7WHHH1v4O1D+T2ULbGXgMDAwMDAwMDAwNbFJu35q6GBQUG1sGOHFc6w6icf+mb4aZ8jxexAAAAAElFTkSuQmCC"
    }
}
