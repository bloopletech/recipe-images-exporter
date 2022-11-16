package net.bloople.recipeimageexporter

import net.minecraft.item.ItemStack
import net.minecraft.recipe.BlastingRecipe
import net.minecraft.recipe.CampfireCookingRecipe
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.recipe.SmeltingRecipe
import net.minecraft.recipe.SmithingRecipe
import net.minecraft.recipe.SmokingRecipe
import net.minecraft.recipe.StonecuttingRecipe

data class CraftingRecipeInfo(
    val recipe: CraftingRecipe,
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
)

data class StonecuttingRecipeInfo(
    val recipe: StonecuttingRecipe,
    val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
)

data class SmeltingRecipeInfo(
    val recipe: SmeltingRecipe,
    val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
)

data class BlastingRecipeInfo(
    val recipe: BlastingRecipe,
    val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
)

data class SmokingRecipeInfo(
    val recipe: SmokingRecipe,
    val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
)

data class CampfireCookingRecipeInfo(
    val recipe: CampfireCookingRecipe,
    val recipePath: String,
    val slot: ItemStack,
    val output: ItemStack
)

data class SmithingRecipeInfo(
    val recipe: SmithingRecipe,
    val recipePath: String,
    val slotBase: ItemStack,
    val slotAddition: ItemStack,
    val output: ItemStack
)
