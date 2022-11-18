package net.bloople.recipeimagesexporter;

import net.bloople.recipeimagesexporter.RecipeImagesExporterMod.LOGGER
import java.nio.file.Files
import java.nio.file.Path


class CampfireCookingRecipeExporter(
    private val recipeInfo: CampfireCookingRecipeInfo,
    private val exportDir: Path
) : Exporter {
    override fun export() {
        LOGGER.info("exporting ${recipeInfo.recipePath}")
        val recipeFilePath = exportDir.resolve("${recipeInfo.recipePath}.txt")
        Files.createDirectories(recipeFilePath.parent)

        Files.newBufferedWriter(recipeFilePath).use {
            it.appendLine("slot: ${recipeInfo.slot.count} ${recipeInfo.slot.item.identifier}")
            it.appendLine("output slot: ${recipeInfo.output.count} ${recipeInfo.output.item.identifier}")
        }
    }
}
