package net.bloople.recipeimagesexporter

import net.bloople.recipeimagesexporter.CraftingRecipeImageGenerator.Companion.labelBackground
import net.bloople.recipeimagesexporter.CraftingRecipeImageGenerator.Companion.slotBackground
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.awt.image.BufferedImage

data class ItemsData(
    val icons: Map<String, BufferedImage>,
    val labels: Map<Item, BufferedImage>,
    val itemNameWidths: Map<Item, Int>
) {
    private val slotIcons = icons.mapValues { it.value.scaleImage(30, 30).fillBackground(slotBackground) }
    private val slotLabelIcons = icons.mapValues { it.value.scaleImage(30, 30).fillBackground(labelBackground) }
    private val transparentSlotIcons = icons.mapValues { it.value.scaleImage(30, 30) }

    fun slotImage(itemStack: ItemStack): BufferedImage {
        return slotIcons[itemStack.uniqueKey]!!
    }

    fun transparentSlotImage(itemStack: ItemStack): BufferedImage {
        return transparentSlotIcons[itemStack.uniqueKey]!!
    }

    fun slotLabelImage(itemStack: ItemStack): BufferedImage {
        return slotLabelIcons[itemStack.uniqueKey]!!
    }

    fun outputImage(itemStack: ItemStack): BufferedImage {
        return icons[itemStack.uniqueKey]!!
    }

    fun labelImage(item: Item): BufferedImage {
        return labels[item]!!
    }
}
