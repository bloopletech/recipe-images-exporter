package net.bloople.recipeimageexporter

import net.bloople.recipeimageexporter.RecipeImageExporterMod.LOGGER
import net.minecraft.recipe.BlastingRecipe
import net.minecraft.recipe.SmokingRecipe


fun convertSmokingRecipe(recipe: SmokingRecipe): List<SmokingRecipeInfo> {
    LOGGER.info("exporting ${recipe.id}")
    if(recipe.isIgnoredInRecipeBook) return emptyList()

    val recipePath = "smoking/${recipe.id.namespace}/${recipe.id.path}"

    val ingredient = recipe.ingredients[0]

    val recipeInfos = ArrayList<SmokingRecipeInfo>()

    for((index, matchingStack) in ingredient.matchingStacks.withIndex()) {
        recipeInfos.add(SmokingRecipeInfo(
            recipe,
            if(ingredient.matchingStacks.size > 1) "${recipePath}_$index" else recipePath,
            matchingStack,
            recipe.output
        ))
    }
    return recipeInfos
}
