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

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
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
