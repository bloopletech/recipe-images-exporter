package net.bloople.recipeimagesexporter

import net.bloople.recipeimagesexporter.mixins.SmithingRecipeAccessor
import net.minecraft.item.ItemStack
import net.minecraft.recipe.*


class CraftingRecipeAdapter(val slots: Array<Array<ItemStack>?> = arrayOfNulls(9)) : RecipeGridAligner<Ingredient> {
    override fun acceptAlignedInput(
        inputs: MutableIterator<Ingredient>,
        slot: Int,
        amount: Int,
        gridX: Int,
        gridY: Int
    ) {
        val itemStacks = inputs.next().matchingStacks
        if(itemStacks.isNotEmpty()) slots[slot] = itemStacks
    }
}

fun convertCraftingRecipe(recipe: CraftingRecipe): List<CraftingRecipeInfo> {
    val recipePath = "crafting/${recipe.id.namespace}/${recipe.id.path}"

    val ingredients = recipe.ingredients

    val adapter = CraftingRecipeAdapter()
    adapter.alignRecipeToGrid(3, 3, -1, recipe, recipe.ingredients.iterator(), 0)

    val maxStacksSize = ingredients.maxOf { it.matchingStacks.size }

    val recipeInfos = ArrayList<CraftingRecipeInfo>()

    repeat(maxStacksSize) { index ->
        recipeInfos.add(
            CraftingRecipeInfo(
                recipePath,
                if(maxStacksSize > 1) "${recipePath}_$index" else recipePath,
                adapter.slots[0]?.getOrLast(index),
                adapter.slots[1]?.getOrLast(index),
                adapter.slots[2]?.getOrLast(index),
                adapter.slots[3]?.getOrLast(index),
                adapter.slots[4]?.getOrLast(index),
                adapter.slots[5]?.getOrLast(index),
                adapter.slots[6]?.getOrLast(index),
                adapter.slots[7]?.getOrLast(index),
                adapter.slots[8]?.getOrLast(index),
                recipe.output
            )
        )
    }

    return recipeInfos
}

fun convertSmeltingRecipe(recipe: SmeltingRecipe): List<SmeltingRecipeInfo> {
    val recipePath = "smelting/${recipe.id.namespace}/${recipe.id.path}"

    val ingredient = recipe.ingredients[0]

    val recipeInfos = ArrayList<SmeltingRecipeInfo>()

    for((index, matchingStack) in ingredient.matchingStacks.withIndex()) {
        recipeInfos.add(SmeltingRecipeInfo(
            recipePath,
            if(ingredient.matchingStacks.size > 1) "${recipePath}_$index" else recipePath,
            matchingStack,
            recipe.output
        ))
    }
    return recipeInfos
}

fun convertBlastingRecipe(recipe: BlastingRecipe): List<BlastingRecipeInfo> {
    val recipePath = "blasting/${recipe.id.namespace}/${recipe.id.path}"

    val ingredient = recipe.ingredients[0]

    val recipeInfos = ArrayList<BlastingRecipeInfo>()

    for((index, matchingStack) in ingredient.matchingStacks.withIndex()) {
        recipeInfos.add(BlastingRecipeInfo(
            recipePath,
            if(ingredient.matchingStacks.size > 1) "${recipePath}_$index" else recipePath,
            matchingStack,
            recipe.output
        ))
    }
    return recipeInfos
}

fun convertSmokingRecipe(recipe: SmokingRecipe): List<SmokingRecipeInfo> {
    val recipePath = "smoking/${recipe.id.namespace}/${recipe.id.path}"

    val ingredient = recipe.ingredients[0]

    val recipeInfos = ArrayList<SmokingRecipeInfo>()

    for((index, matchingStack) in ingredient.matchingStacks.withIndex()) {
        recipeInfos.add(SmokingRecipeInfo(
            recipePath,
            if(ingredient.matchingStacks.size > 1) "${recipePath}_$index" else recipePath,
            matchingStack,
            recipe.output
        ))
    }
    return recipeInfos
}

fun convertCampfireCookingRecipe(recipe: CampfireCookingRecipe): List<CampfireCookingRecipeInfo> {
    val recipePath = "campfire_cooking/${recipe.id.namespace}/${recipe.id.path}"

    val ingredient = recipe.ingredients[0]

    val recipeInfos = ArrayList<CampfireCookingRecipeInfo>()

    for((index, matchingStack) in ingredient.matchingStacks.withIndex()) {
        recipeInfos.add(CampfireCookingRecipeInfo(
            recipePath,
            if(ingredient.matchingStacks.size > 1) "${recipePath}_$index" else recipePath,
            matchingStack,
            recipe.output
        ))
    }
    return recipeInfos
}

fun convertStonecuttingRecipe(recipe: StonecuttingRecipe): List<StonecuttingRecipeInfo> {
    val recipePath = "stonecutting/${recipe.id.namespace}/${recipe.id.path}"

    val ingredient = recipe.ingredients[0]

    val recipeInfos = ArrayList<StonecuttingRecipeInfo>()

    for((index, matchingStack) in ingredient.matchingStacks.withIndex()) {
        recipeInfos.add(StonecuttingRecipeInfo(
            recipePath,
            if(ingredient.matchingStacks.size > 1) "${recipePath}_$index" else recipePath,
            matchingStack,
            recipe.output
        ))
    }
    return recipeInfos
}

fun convertSmithingRecipe(recipe: SmithingRecipe): List<SmithingRecipeInfo> {
    val recipePath = "smithing/${recipe.id.namespace}/${recipe.id.path}"

    val recipeAccessor = recipe as SmithingRecipeAccessor

    val base = recipeAccessor.getBase() as Ingredient
    val addition = recipeAccessor.getAddition() as Ingredient

    val maxStacksSize = arrayOf(base, addition).maxOf { it.matchingStacks.size }

    val recipeInfos = ArrayList<SmithingRecipeInfo>()

    repeat(maxStacksSize) { index ->
        recipeInfos.add(
            SmithingRecipeInfo(
                recipePath,
                if(maxStacksSize > 1) "${recipePath}_$index" else recipePath,
                base.matchingStacks.getOrLast(index)!!,
                addition.matchingStacks.getOrLast(index)!!,
                recipe.output
            )
        )
    }

    return recipeInfos
}
