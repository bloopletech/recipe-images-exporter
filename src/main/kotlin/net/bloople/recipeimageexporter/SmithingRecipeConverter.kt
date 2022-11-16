package net.bloople.recipeimageexporter

import net.bloople.recipeimageexporter.RecipeImageExporterMod.LOGGER
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.SmithingRecipe


fun convertSmithingRecipe(recipe: SmithingRecipe): List<SmithingRecipeInfo> {
    LOGGER.info("exporting ${recipe.id}")
    if(recipe.isIgnoredInRecipeBook) return emptyList()

    val recipePath = "smithing/${recipe.id.namespace}/${recipe.id.path}"

    val base = recipe.javaClass.getDeclaredField("base").get(recipe) as Ingredient
    val addition = recipe.javaClass.getDeclaredField("addition").get(recipe) as Ingredient

    val maxStacksSize = arrayOf(base, addition).maxOf { it.matchingStacks.size }

    val recipeInfos = ArrayList<SmithingRecipeInfo>()

    repeat(maxStacksSize) { index ->
        recipeInfos.add(
            SmithingRecipeInfo(
                recipe,
                if(maxStacksSize > 1) "${recipePath}_$index" else recipePath,
                base.matchingStacks.getOrLast(index)!!,
                addition.matchingStacks.getOrLast(index)!!,
                recipe.output
            )
        )
    }

    return recipeInfos
}
