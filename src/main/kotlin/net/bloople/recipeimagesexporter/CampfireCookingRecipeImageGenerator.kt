package net.bloople.recipeimagesexporter

import net.minecraft.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max
import javax.imageio.ImageIO


class CampfireCookingRecipeImageGenerator(
    private val recipeInfo: CampfireCookingRecipeInfo,
    private val itemsData: ItemsData
) : RecipeImageGenerator {
    override val width = max(166 + rightImage.width, recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 44 + 8)
    override val height = 120

    override fun export(imageWidth: Int, imageHeight: Int): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, baseImage.getData(0, 0, width, height))
            raster.setRect(width - 2, 0, rightImage.getData(0, 0, 2, height))

            createGraphics().use {
                drawImage(itemsData.slotImage(recipeInfo.slot), 11, 11, null)
                drawImage(itemsData.slotImage(recipeInfo.output), 127, 11, null)

                val x = 8
                var y = 50

                for(item in recipeInfo.items) {
                    drawImage(itemsData.slotLabelImage(ItemStack(item, 1)), x, y, null)
                    drawImage(itemsData.labelImage(item), 44, y + 6, null)
                    y += 34
                }
            }
        }
    }

    companion object {
        private const val campfireCookingLeftImage = "iVBORw0KGgoAAAANSUhEUgAAAgAAAAB4CAIAAAAL9N0oAAACAUlEQVR42u3csW3DMBRF0e+Y82gBrcFFBHBANoSG4AhcwoUbNwaCwLas8JwmRQoDv8jlS4Bceu8BwHx+nABgTun+ZYzhFgAWAAACAIAAACAAAAgAAOeUnn1j27YPfHzOOSLWdZ3q6G4LWAAAfN8CeHxFvk8pJSL2fZ/w9G4LWAAACMDRWmutNXcABAAAAbADAAQAAAGwAwAEAAABsAMABAAAAbADAAQAAAGwAwAEAIAPSqd+jx/4uf7PPmABACAAM/H3AEAAADil5AR/428AgAUAgAVw/jf4b36t7+0PWAAAWABz8PYHLAAALABvfwALAAALwNsfwAIAwALw9gewAACwALz9ASwAAI5fADnniCilePu/3Jy3BSwAAL7CpfceEWMMtwCwAAAQAAAEAAABAEAAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAAABAEAAABAAAAQAAAEAQAAAEAAAAQBAAAAQAAAEAAABAEAAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAAABAEAAABAAAAQAAAEAQAAAEAAABABAAAAQAAAEAAABAEAAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAAABAEAAABAAAAQAAAEAQAAAEAAABABAAAAQAAAEAAABAOC/SRFRa621ugXAVK7LsvjpDzChGxZRVSsFwNU7AAAAAElFTkSuQmCC"
        private val baseImage = ImageIO.read(decodeBase64(campfireCookingLeftImage)).asARGB()
        private val rightImage = ImageIO.read(decodeBase64(CraftingRecipeImageGenerator.craftingRightImage)).asARGB()
    }
}
