package net.bloople.recipeimageexporter

import net.bloople.recipeimageexporter.RecipeImageExporterMod.LOGGER
import net.minecraft.recipe.RecipeManager
import net.minecraft.resource.ResourceManager
import java.nio.file.Files
import java.nio.file.Path

class RecipesExporter(recipeManager: RecipeManager, private val resourceManager: ResourceManager) {
    private val recipeInfos = RecipeInfos(recipeManager)

    fun export() {
        val curDir = Path.of("").toAbsolutePath();
        val exportDir = curDir.resolve("recipe-image-exporter")
        exportDir.toFile().deleteRecursively()
        Files.createDirectories(exportDir)

        LOGGER.info("=============")
        LOGGER.info("Unique Items:")
        recipeInfos.uniqueItems.forEach { LOGGER.info(it.identifier.toString()) }

        LOGGER.info("End of export!")

        for(recipeInfo in recipeInfos.craftingRecipeInfos) {
            CraftingRecipeExporter(recipeInfo, exportDir).export()
        }

//        for(recipeInfo in smeltingRecipeInfos) {
//            SmeltingRecipeExporter(recipeInfo, exportDir).export()
//        }
//
//        for(recipeInfo in blastingRecipeInfos) {
//            BlastingRecipeExporter(recipeInfo, exportDir).export()
//        }
//
//        for(recipeInfo in smokingRecipeInfos) {
//            SmokingRecipeExporter(recipeInfo, exportDir).export()
//        }
//
//        for(recipeInfo in campfireCookingRecipeInfos) {
//            CampfireCookingRecipeExporter(recipeInfo, exportDir).export()
//        }
//
//        for(recipeInfo in stonecuttingRecipeInfos) {
//            StonecuttingRecipeExporter(recipeInfo, exportDir).export()
//        }
//
//        for(recipeInfo in smithingRecipeInfos) {
//            SmithingRecipeExporter(recipeInfo, exportDir).export()
//        }
    }
}