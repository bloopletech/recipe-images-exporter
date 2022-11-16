package net.bloople.recipeimageexporter;

import net.bloople.recipeimageexporter.RecipeImageExporterMod.LOGGER
import java.nio.file.Files
import java.nio.file.Path


class CraftingRecipeExporter(
    private val recipeInfo: CraftingRecipeInfo,
    private val exportDir: Path
) : Exporter {
    override fun export() {
        LOGGER.info("exporting ${recipeInfo.recipePath}")
        val recipeFilePath = exportDir.resolve("${recipeInfo.recipePath}.txt")
        Files.createDirectories(recipeFilePath.parent)

        Files.newBufferedWriter(recipeFilePath).use {
            if(recipeInfo.slot1 == null) it.appendLine("slot 1: empty")
            else it.appendLine("slot 1: ${recipeInfo.slot1.count} ${recipeInfo.slot1.item.identifier}")

            if(recipeInfo.slot2 == null) it.appendLine("slot 2: empty")
            else it.appendLine("slot 2: ${recipeInfo.slot2.count} ${recipeInfo.slot2.item.identifier}")

            if(recipeInfo.slot3 == null) it.appendLine("slot 3: empty")
            else it.appendLine("slot 3: ${recipeInfo.slot3.count} ${recipeInfo.slot3.item.identifier}")

            if(recipeInfo.slot4 == null) it.appendLine("slot 4: empty")
            else it.appendLine("slot 4: ${recipeInfo.slot4.count} ${recipeInfo.slot4.item.identifier}")

            if(recipeInfo.slot5 == null) it.appendLine("slot 5: empty")
            else it.appendLine("slot 5: ${recipeInfo.slot5.count} ${recipeInfo.slot5.item.identifier}")

            if(recipeInfo.slot6 == null) it.appendLine("slot 6: empty")
            else it.appendLine("slot 6: ${recipeInfo.slot6.count} ${recipeInfo.slot6.item.identifier}")

            if(recipeInfo.slot7 == null) it.appendLine("slot 7: empty")
            else it.appendLine("slot 7: ${recipeInfo.slot7.count} ${recipeInfo.slot7.item.identifier}")

            if(recipeInfo.slot8 == null) it.appendLine("slot 8: empty")
            else it.appendLine("slot 8: ${recipeInfo.slot8.count} ${recipeInfo.slot8.item.identifier}")

            if(recipeInfo.slot9 == null) it.appendLine("slot 9: empty")
            else it.appendLine("slot 9: ${recipeInfo.slot9.count} ${recipeInfo.slot9.item.identifier}")

            it.appendLine("output slot: ${recipeInfo.output.count} ${recipeInfo.output.item.identifier}")
        }
    }
}
