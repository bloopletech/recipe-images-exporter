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

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
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
        private const val campfireCookingLeftImage = "iVBORw0KGgoAAAANSUhEUgAABAAAAAB4CAIAAADjQRdrAAAC8ElEQVR42u3bsW2DUBSG0euEeViANVgEiQFpEEMwAku4oA2xE8nAe/ecxoULW08p/L0/PNZ1DQAAIIcvRwAAAHk0+8u2bc4CAACqZwEAAAABAAAACAAAAEAAAAAAAgAAALiZ5uiNYRhO+Pi+7yOi67pUh+5sAQC4igUAAAASaX5/e79F/pxxHCNiWZaER+9sAQA4nwUAAAAEQFbzPM/z7BwAABAAAACAAKiRHQAAAAEAAAAIgHrZAQAAEAAAAIAAqJcdAAAAAQAAAAiAetkBAAAQAAAAgAColx0AAAABAAAAFKMp96tfdRm/f27Xdf56AAAojgUAAAAEAK94HgAAAAEAAADcWuMI/sczAAAAlMgCAAAAiRS8AHziDv6df+t39w8AQLksAAAAkIhnAP7A3T8AAKWzAAAAQCIWgLe4+wcAoA4WAAAASMQC8IK7fwAAamIBAACARCwAh9z9AwBQHwsAAAAkYgH4gbt/AABqZQEAAIBEDheAvu8jYhzHVMdxzt1/zrMFAOAOLAAAAJDIY13XiNi2zVkAAED1LAAAACAAAAAAAQAAAAgAAABAAAAAAAIAAAAQAAAAgAAAAAAEAAAAIAAAAAABAAAACAAAABAAAACAAAAAAAQAAAAgAAAAAAEAAAAIAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAAAgAAABAAAAAAAIAAAAQAAAAgAAAAAAEAAAAIAAAAAABAAAACAAAAEAAAAAAAgAAAAQAAAAgAAAAAAEAAAAIAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAACAAAAAAAQAAAAIAAAAQAAAAgAAAAAAEAAAAIAAAAAABAAAACAAAAEAAAAAAAgAAABAAAACAAAAAAAQAAAAIAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAACAAAAAAAQAAAAgAAABAAAAAAAIAAAAEAAAAIAAAAAABAAAACAAAAEAAAAAAF2siYpqmaZqcBQAAVO+7bVu//gEAIIknHjRVKxSIUvIAAAAASUVORK5CYII="
        private val baseImage = ImageIO.read(decodeBase64(campfireCookingLeftImage)).asARGB()
        private val rightImage = ImageIO.read(decodeBase64(CraftingRecipeImageGenerator.craftingRightImage)).asARGB()
    }
}
