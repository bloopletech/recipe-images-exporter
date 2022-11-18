package net.bloople.recipeimagesexporter;

import net.bloople.recipeimagesexporter.RecipeImagesExporterMod.LOGGER
import java.nio.file.Files
import java.nio.file.Path


class SmithingRecipeExporter(
    private val recipeInfo: SmithingRecipeInfo,
    private val exportDir: Path
) : Exporter {
    override fun export() {
        LOGGER.info("exporting ${recipeInfo.recipePath}")
        val recipeFilePath = exportDir.resolve("${recipeInfo.recipePath}.txt")
        Files.createDirectories(recipeFilePath.parent)

        Files.newBufferedWriter(recipeFilePath).use {
            it.appendLine("slot base: ${recipeInfo.slotBase.count} ${recipeInfo.slotBase.item.identifier}")
            it.appendLine("slot addition: ${recipeInfo.slotAddition.count} ${recipeInfo.slotAddition.item.identifier}")
            it.appendLine("output slot: ${recipeInfo.output.count} ${recipeInfo.output.item.identifier}")
        }
    }
}
