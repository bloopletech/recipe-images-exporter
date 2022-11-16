package net.bloople.recipeimageexporter

import net.bloople.recipeimageexporter.RecipeImageExporterMod.LOGGER
import net.minecraft.item.ItemStack
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeGridAligner


fun convertCraftingRecipe(recipe: CraftingRecipe): List<CraftingRecipeInfo> {
    LOGGER.info("exporting ${recipe.id}")
    if(recipe.isIgnoredInRecipeBook) return emptyList()

    val recipePath = "crafting/${recipe.id.namespace}/${recipe.id.path}"

    val ingredients = recipe.ingredients

    val adapter = CraftingRecipeAdapter()
    adapter.alignRecipeToGrid(3, 3, -1, recipe, recipe.ingredients.iterator(), 0)

    val maxStacksSize = ingredients.maxOf { it.matchingStacks.size }

    val recipeInfos = ArrayList<CraftingRecipeInfo>()

    repeat(maxStacksSize) { index ->
        recipeInfos.add(
            CraftingRecipeInfo(
                recipe,
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

class CraftingRecipeAdapter(val slots: Array<Array<ItemStack>?> = arrayOfNulls(9)) : RecipeGridAligner<Ingredient> {
    override fun acceptAlignedInput(
        inputs: MutableIterator<Ingredient>,
        slot: Int,
        amount: Int,
        gridX: Int,
        gridY: Int
    ) {
        val itemStacks = inputs.next().matchingStacks
        if(itemStacks.isNotEmpty()) {
            slots[slot] = itemStacks
//            val itemStack = itemStacks[0]
//
//            LOGGER.info("acceptAlignedInput slot: $slot, amount: $amount, gridX: $gridX, gridY: $gridY, itemStack: ${itemStack.count} ${itemStack.item.identifier}")
        }
    }
}