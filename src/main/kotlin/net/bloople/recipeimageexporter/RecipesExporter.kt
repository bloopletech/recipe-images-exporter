package net.bloople.recipeimageexporter

import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.resource.ResourceManager
import java.nio.file.Files
import java.nio.file.Path

class RecipesExporter(private val recipeManager: RecipeManager, private val resourceManager: ResourceManager) {
    fun export() {
        val curDir = Path.of("").toAbsolutePath();
        val exportDir = curDir.resolve("recipe-image-exporter")
        exportDir.toFile().deleteRecursively()
        Files.createDirectories(exportDir)

        val craftingRecipeInfos = recipeManager.listAllOfType(RecipeType.CRAFTING).flatMap { convertCraftingRecipe(it) }

        for(recipeInfo in craftingRecipeInfos) {
            CraftingRecipeExporter(recipeInfo, exportDir).export()
        }

        val smeltingRecipeInfos = recipeManager.listAllOfType(RecipeType.SMELTING).flatMap { convertSmeltingRecipe(it) }

        for(recipeInfo in smeltingRecipeInfos) {
            SmeltingRecipeExporter(recipeInfo, exportDir).export()
        }

        val blastingRecipeInfos = recipeManager.listAllOfType(RecipeType.BLASTING).flatMap { convertBlastingRecipe(it) }

        for(recipeInfo in blastingRecipeInfos) {
            BlastingRecipeExporter(recipeInfo, exportDir).export()
        }

        val smokingRecipeInfos = recipeManager.listAllOfType(RecipeType.SMOKING).flatMap { convertSmokingRecipe(it) }

        for(recipeInfo in smokingRecipeInfos) {
            SmokingRecipeExporter(recipeInfo, exportDir).export()
        }

        val campfireCookingRecipeInfos = recipeManager.listAllOfType(RecipeType.CAMPFIRE_COOKING).flatMap { convertCampfireCookingRecipe(it) }

        for(recipeInfo in campfireCookingRecipeInfos) {
            CampfireCookingRecipeExporter(recipeInfo, exportDir).export()
        }

        val stonecuttingRecipeInfos = recipeManager.listAllOfType(RecipeType.STONECUTTING).flatMap { convertStonecuttingRecipe(it) }

        for(recipeInfo in stonecuttingRecipeInfos) {
            StonecuttingRecipeExporter(recipeInfo, exportDir).export()
        }

        val smithingRecipeInfos = recipeManager.listAllOfType(RecipeType.SMITHING).flatMap { convertSmithingRecipe(it) }

        for(recipeInfo in smithingRecipeInfos) {
            SmithingRecipeExporter(recipeInfo, exportDir).export()
        }

    }
}