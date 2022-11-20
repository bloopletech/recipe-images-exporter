package net.bloople.recipeimagesexporter

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType

interface RecipeInfo {
    val recipeBasePath: String
    val itemStacks: List<ItemStack>
    val items: List<Item>
        get() {
            return itemStacks.map { it.item }.distinct()
        }
}

data class CraftingRecipeInfo(
    override val recipeBasePath: String,
    val recipePath: String,
    val slot1: ItemStack?,
    val slot2: ItemStack?,
    val slot3: ItemStack?,
    val slot4: ItemStack?,
    val slot5: ItemStack?,
    val slot6: ItemStack?,
    val slot7: ItemStack?,
    val slot8: ItemStack?,
    val slot9: ItemStack?,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() {
            return arrayOf(
                slot1,
                slot2,
                slot3,
                slot4,
                slot5,
                slot6,
                slot7,
                slot8,
                slot9,
                output
            ).filterNotNull().distinct()
        }
}

data class StonecuttingRecipeInfo(
    override val recipeBasePath: String,
    val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slot, output).distinct()
}

data class SmeltingRecipeInfo(
    override val recipeBasePath: String,
    val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slot, output).distinct()
}

data class BlastingRecipeInfo(
    override val recipeBasePath: String,
    val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slot, output).distinct()
}

data class SmokingRecipeInfo(
    override val recipeBasePath: String,
    val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slot, output).distinct()
}

data class CampfireCookingRecipeInfo(
    override val recipeBasePath: String,
    val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slot, output).distinct()
}

data class SmithingRecipeInfo(
    override val recipeBasePath: String,
    val recipePath: String,
    val slotBase: ItemStack,
    val slotAddition: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slotBase, slotAddition, output).distinct()
}

class RecipeInfos(recipeManager: RecipeManager) {
    val craftingRecipeInfos = recipeManager.listAllOfType(RecipeType.CRAFTING)
        .filterNot { it.isIgnoredInRecipeBook }.flatMap { convertCraftingRecipe(it) }
    val smeltingRecipeInfos = recipeManager.listAllOfType(RecipeType.SMELTING)
        .filterNot { it.isIgnoredInRecipeBook }.flatMap { convertSmeltingRecipe(it) }
    val blastingRecipeInfos = recipeManager.listAllOfType(RecipeType.BLASTING)
        .filterNot { it.isIgnoredInRecipeBook }.flatMap { convertBlastingRecipe(it) }
    val smokingRecipeInfos = recipeManager.listAllOfType(RecipeType.SMOKING)
        .filterNot { it.isIgnoredInRecipeBook }.flatMap { convertSmokingRecipe(it) }
    val campfireCookingRecipeInfos = recipeManager.listAllOfType(RecipeType.CAMPFIRE_COOKING)
        .filterNot { it.isIgnoredInRecipeBook }.flatMap { convertCampfireCookingRecipe(it) }
    val stonecuttingRecipeInfos = recipeManager.listAllOfType(RecipeType.STONECUTTING)
        .filterNot { it.isIgnoredInRecipeBook }.flatMap { convertStonecuttingRecipe(it) }
    val smithingRecipeInfos = recipeManager.listAllOfType(RecipeType.SMITHING)
        .filterNot { it.isIgnoredInRecipeBook }.flatMap { convertSmithingRecipe(it) }

    val allRecipeInfos = listOf(
        craftingRecipeInfos,
        smeltingRecipeInfos,
        blastingRecipeInfos,
        smokingRecipeInfos,
        campfireCookingRecipeInfos,
        stonecuttingRecipeInfos,
        smithingRecipeInfos
    ).flatten()

    val items = allRecipeInfos.flatMap { it.items }.distinctBy { it.identifier }.sortedBy { it.identifier }

    val itemStacks = (allRecipeInfos.flatMap { it.itemStacks } + items.map { ItemStack(it) })
        .distinctBy { it.uniqueKey }.sortedBy { it.uniqueKey }
}
