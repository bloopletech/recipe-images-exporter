package net.bloople.recipeimagesexporter

import net.minecraft.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max
import javax.imageio.ImageIO


class SmithingRecipeImageGenerator(
    private val recipeInfo: SmithingRecipeInfo,
    private val itemsData: ItemsData
) : RecipeImageGenerator {
    override val width = max(264 + rightImage.width, recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 44 + 8)
    override val height = 154

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, baseImage.getData(0, 0, width, height))
            raster.setRect(width - 2, 0, rightImage.getData(0, 0, 2, height))

            createGraphics().use {
                drawImage(itemsData.slotImage(recipeInfo.slotBase), 11, 11, null)
                drawImage(itemsData.slotImage(recipeInfo.slotAddition), 109, 11, null)
                drawImage(itemsData.outputImage(recipeInfo.output), 225, 11, null)

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
        private const val smithingLeftImage = "iVBORw0KGgoAAAANSUhEUgAABAAAAACaCAIAAAC4za3iAAADqUlEQVR42u3dwW3CQBBA0SFxPTRAG27Ekgv0xXIRLsFNcOCIsIhiw+7Oe5cciBQxyiF/BsJlXdcAAABy+DECAADIo3t82bbNLAAAoHkuAAAAIAAAAAABAAAACAAAAEAAAAAAhelePTAMwwd+fN/3EXG73SodnymZLQBAXVwAAAAgkW7/4cd+9DzjOEbEsixVD9GUzBYAoBYuAAAAIADgtXme53k2BwAAAQAAABSta/WJPa+o/aeXMyZsqgAAdXEBAAAAAQDv8X4AAAABAAAACADa5Q4AACAAAAAAAUC73AEAAAQAAAAgAGiXOwAAgAAAAACKUPEnAf91zbz//a1+ou23lvE+JxgAoEwuAAAAIADgCN4PAAAgAAAAgK/pjIDzeA8AAEBpXAAAACCRii8A+9vl55ee59xGn/Gs33lZv90/AECZXAAAACAR7wHgYHb/AAAlcwEAAIBEXAA4jN0/AED5XAAAACARFwAOYPcPAFALFwAAAEjEBYB/sfsHAKiLCwAAACTS7AXAZtqEAQB45gIAAACJvLwA9H0fEeM4mtGOnFP6zO7fbyAAwBlcAAAAIJHLuq4RsW2bWQAAQPNcAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAACAAAAAAAQAAAAgAAABAAAAAAAIAAAAQAAAAIAAAAAABAAAACAAAAEAAAAAAAgAAABAAAACAAAAAAAQAAAAgAAAAAAEAAAAIAAAAEAAAAIAAAAAABAAAACAAAAAAAQAAAAgAAABAAAAAAAIAAAAQAAAAgAAAAAAEAAAACAAAAEAAAAAAAgAAABAAAACAAAAAAAQAAAAgAAAAAAEAAAAIAAAAQAAAAAACAAAABAAAACAAAAAAAQAAAAgAAABAAAAAAAIAAAAQAAAAgAAAAAAEAAAAIAAAAAABAAAACAAAABAAAACAAAAAAAQAAAAgAAAAAAEAAAAIAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAAAgAAABAAAAAAAIAAAAQAAAAgAAAAAAEAAAAIAAAAAABAAAACAAAAEAAAAAAAgAAAAQAAAAgAAAAAAEAAAAIAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAACAAAAAAAQAAAAIAAAAQAAAAgAAAAAAEAAAAIAAAAAABAAAACAAAAEAAAAAAAgAAABAAAACAAAAAAAFgBAAAIAAAAAABAAAACAAAAEAAAAAAAgAAAPiSLiKmaZqmySwAAKB5v9fr1V//AACQxB15ZGqJ5m7BIwAAAABJRU5ErkJggg=="
        private val baseImage = ImageIO.read(decodeBase64(smithingLeftImage)).asARGB()
        private val rightImage = ImageIO.read(decodeBase64(CraftingRecipeImageGenerator.craftingRightImage)).asARGB()
    }
}

