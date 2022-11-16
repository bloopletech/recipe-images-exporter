package net.bloople.recipeimageexporter

import net.bloople.recipeimageexporter.RecipeImageExporterMod.LOGGER
import net.minecraft.recipe.CampfireCookingRecipe


fun convertCampfireCookingRecipe(recipe: CampfireCookingRecipe): List<CampfireCookingRecipeInfo> {
    LOGGER.info("exporting ${recipe.id}")
    if(recipe.isIgnoredInRecipeBook) return emptyList()

    val recipePath = "campfire_cooking/${recipe.id.namespace}/${recipe.id.path}"

    val ingredient = recipe.ingredients[0]

    val recipeInfos = ArrayList<CampfireCookingRecipeInfo>()

    for((index, matchingStack) in ingredient.matchingStacks.withIndex()) {
        recipeInfos.add(CampfireCookingRecipeInfo(
            recipe,
            if(ingredient.matchingStacks.size > 1) "${recipePath}_$index" else recipePath,
            matchingStack,
            recipe.output
        ))
    }
    return recipeInfos
}
