package net.bloople.recipeimagesexporter

import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.awt.image.BufferedImage
import java.lang.Integer.max
import javax.imageio.ImageIO


class BlastingRecipeImageGenerator(
    private val recipeInfo: BlastingRecipeInfo,
    private val itemsData: ItemsData
) : RecipeImageGenerator {
    override val width = max(178 + rightImage.width, recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 44 + 8)
    override val height = 192

    override fun export(imageWidth: Int, imageHeight: Int): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, baseImage.getData(0, 0, width, height))
            raster.setRect(width - 2, 0, rightImage.getData(0, 0, 2, height))

            createGraphics().use {
                drawImage(itemsData.slotImage(recipeInfo.slot), 11, 11, null)
                val coalStack = ItemStack(Registry.ITEM.get(Identifier("coal")), 1)
                drawImage(itemsData.slotImage(coalStack), 11, 83, null)
                drawImage(itemsData.outputImage(recipeInfo.output), 130, 46, null)

                val x = 8
                var y = 122

                for(item in recipeInfo.items) {
                    drawImage(itemsData.slotLabelImage(ItemStack(item, 1)), x, y, null)
                    drawImage(itemsData.labelImage(item), 44, y + 6, null)
                    y += 34
                }
            }
        }
    }

    companion object {
        private const val blastingLeftImage = "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"
        private val baseImage = ImageIO.read(decodeBase64(blastingLeftImage)).asARGB()
        private val rightImage = ImageIO.read(decodeBase64(CraftingRecipeImageGenerator.craftingRightImage)).asARGB()
    }
}
