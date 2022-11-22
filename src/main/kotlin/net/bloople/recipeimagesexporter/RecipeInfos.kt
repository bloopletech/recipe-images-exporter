package net.bloople.recipeimagesexporter

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType

interface RecipeInfo {
    val recipeBasePath: String
    val recipePath: String
    val itemStacks: List<ItemStack>
    val items: List<Item>
        get() {
            return itemStacks.map { it.item }.distinct()
        }
    fun imageGenerator(itemsData: ItemsData): RecipeImageGenerator
}

data class CraftingRecipeInfo(
    override val recipeBasePath: String,
    override val recipePath: String,
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

    override fun imageGenerator(itemsData: ItemsData): RecipeImageGenerator {
        return CraftingRecipeImageGenerator(this, itemsData)
    }
}

data class StonecuttingRecipeInfo(
    override val recipeBasePath: String,
    override val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slot, output).distinct()

    override fun imageGenerator(itemsData: ItemsData): RecipeImageGenerator {
        return StonecuttingRecipeImageGenerator(this, itemsData)
    }
}

data class SmeltingRecipeInfo(
    override val recipeBasePath: String,
    override val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slot, output).distinct()

    override fun imageGenerator(itemsData: ItemsData): RecipeImageGenerator {
        return SmeltingRecipeImageGenerator(this, itemsData)
    }
}

data class BlastingRecipeInfo(
    override val recipeBasePath: String,
    override val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slot, output).distinct()

    override fun imageGenerator(itemsData: ItemsData): RecipeImageGenerator {
        return BlastingRecipeImageGenerator(this, itemsData)
    }
}

data class SmokingRecipeInfo(
    override val recipeBasePath: String,
    override val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slot, output).distinct()

    override fun imageGenerator(itemsData: ItemsData): RecipeImageGenerator {
        return SmokingRecipeImageGenerator(this, itemsData)
    }
}

data class CampfireCookingRecipeInfo(
    override val recipeBasePath: String,
    override val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slot, output).distinct()

    override fun imageGenerator(itemsData: ItemsData): RecipeImageGenerator {
        return CampfireCookingRecipeImageGenerator(this, itemsData)
    }
}

data class SmithingRecipeInfo(
    override val recipeBasePath: String,
    override val recipePath: String,
    val slotBase: ItemStack,
    val slotAddition: ItemStack,
    val output: ItemStack
) : RecipeInfo {
    override val itemStacks: List<ItemStack>
        get() = listOf(slotBase, slotAddition, output).distinct()

    override fun imageGenerator(itemsData: ItemsData): RecipeImageGenerator {
        return SmithingRecipeImageGenerator(this, itemsData)
    }
}

class RecipeInfos(recipeManager: RecipeManager) {
    private val craftingGroups = recipeManager.listAllOfType(RecipeType.CRAFTING)
        .filterNot { it.isIgnoredInRecipeBook }.map { convertCraftingRecipe(it) }
    private val smeltingGroups = recipeManager.listAllOfType(RecipeType.SMELTING)
        .filterNot { it.isIgnoredInRecipeBook }.map { convertSmeltingRecipe(it) }
    private val blastingGroups = recipeManager.listAllOfType(RecipeType.BLASTING)
        .filterNot { it.isIgnoredInRecipeBook }.map { convertBlastingRecipe(it) }
    private val smokingGroups = recipeManager.listAllOfType(RecipeType.SMOKING)
        .filterNot { it.isIgnoredInRecipeBook }.map { convertSmokingRecipe(it) }
    private val campfireCookingGroups = recipeManager.listAllOfType(RecipeType.CAMPFIRE_COOKING)
        .filterNot { it.isIgnoredInRecipeBook }.map { convertCampfireCookingRecipe(it) }
    private val stonecuttingGroups = recipeManager.listAllOfType(RecipeType.STONECUTTING)
        .filterNot { it.isIgnoredInRecipeBook }.map { convertStonecuttingRecipe(it) }
    private val smithingGroups = recipeManager.listAllOfType(RecipeType.SMITHING)
        .filterNot { it.isIgnoredInRecipeBook }.map { convertSmithingRecipe(it) }

    val groups = listOf(
        craftingGroups,
        smeltingGroups,
        blastingGroups,
        smokingGroups,
        campfireCookingGroups,
        stonecuttingGroups,
        smithingGroups
    ).flatten()

    val all = groups.flatten()

    val items = all.flatMap { it.items }.distinctBy { it.identifier }.sortedBy { it.identifier }

    val itemStacks = (all.flatMap { it.itemStacks } + items.map { ItemStack(it) }).distinctBy { it.uniqueKey }
        .sortedBy { it.uniqueKey }
}
