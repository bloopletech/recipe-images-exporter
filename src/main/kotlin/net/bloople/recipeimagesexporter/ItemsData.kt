package net.bloople.recipeimagesexporter

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.awt.image.BufferedImage

data class ItemsData(
    val largeSlotIcons: Map<String, BufferedImage>,
    val labelIcons: Map<String, BufferedImage>,
    val transparentIcons: Map<String, BufferedImage>,
    val labels: Map<Item, BufferedImage>,
    val itemNameWidths: Map<Item, Int>
) {
    val slotIcons = largeSlotIcons.mapValues { it.value.scaleImage(30, 30) }
    val slotLabelIcons = labelIcons.mapValues { it.value.scaleImage(30, 30) }
    val transparentSlotIcons = transparentIcons.mapValues { it.value.scaleImage(30, 30) }

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
        return largeSlotIcons[itemStack.uniqueKey]!!
    }

    fun labelImage(item: Item): BufferedImage {
        return labels[item]!!
    }
}
