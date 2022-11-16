package net.bloople.recipeimageexporter

import net.bloople.recipeimageexporter.RecipeImageExporterMod.LOGGER
import net.minecraft.recipe.StonecuttingRecipe


fun convertStonecuttingRecipe(recipe: StonecuttingRecipe): List<StonecuttingRecipeInfo> {
    LOGGER.info("exporting ${recipe.id}")
    if(recipe.isIgnoredInRecipeBook) return emptyList()

    val recipePath = "stonecutting/${recipe.id.namespace}/${recipe.id.path}"

    val ingredient = recipe.ingredients[0]

    val recipeInfos = ArrayList<StonecuttingRecipeInfo>()

    for((index, matchingStack) in ingredient.matchingStacks.withIndex()) {
        recipeInfos.add(StonecuttingRecipeInfo(
            recipe,
            if(ingredient.matchingStacks.size > 1) "${recipePath}_$index" else recipePath,
            matchingStack,
            recipe.output
        ))
    }
    return recipeInfos
}
