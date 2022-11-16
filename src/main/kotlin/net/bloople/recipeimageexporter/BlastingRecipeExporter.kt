package net.bloople.recipeimageexporter;

import net.bloople.recipeimageexporter.RecipeImageExporterMod.LOGGER
import java.nio.file.Files
import java.nio.file.Path


class BlastingRecipeExporter(
    private val recipeInfo: BlastingRecipeInfo,
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
