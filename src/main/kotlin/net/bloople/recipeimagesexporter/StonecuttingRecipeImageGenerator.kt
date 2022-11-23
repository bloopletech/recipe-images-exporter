package net.bloople.recipeimagesexporter

import net.minecraft.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max
import javax.imageio.ImageIO


class StonecuttingRecipeImageGenerator(
    private val recipeInfo: StonecuttingRecipeInfo,
    private val itemsData: ItemsData
) : RecipeImageGenerator {
    override val width = max(168 + rightImage.width, recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 44 + 8)
    override val height = 164

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, baseImage.getData(0, 0, width, height))
            raster.setRect(width - 2, 0, rightImage.getData(0, 0, 2, height))

            createGraphics().use {
                drawImage(itemsData.slotImage(recipeInfo.slot), 11, 47, null)
                drawImage(itemsData.transparentSlotImage(recipeInfo.slot), 54, 22, null)
                drawImage(itemsData.outputImage(recipeInfo.output), 119, 45, null)

                val x = 8
                var y = 94

                for(item in recipeInfo.items) {
                    drawImage(itemsData.slotLabelImage(ItemStack(item, 1)), x, y, null)
                    drawImage(itemsData.labelImage(item), 44, y + 6, null)
                    y += 34
                }
            }
        }
    }

    companion object {
        private const val stonecuttingLeftImage = "iVBORw0KGgoAAAANSUhEUgAABAAAAACkCAIAAACGvcs/AAAEJ0lEQVR42u3dMU7jUBSG0QvjOhvKNryIaS15Hy4o2AHS9HQ0biyLmtJL8CYoXCERJYxC8vzuOc10g/VC8/kP9sOyLAEAAOTw6AgAACCPZvtnXVdnAQAA1bMAAACAAAAAAAQAAAAgAAAAAAEAAAAIAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAACAAAABAAAAAAAIAAADYv6bYK+u67mY/axgGvwoAAGRgAQAAgESawq/v39PfX/qfx/kjIp5fJr8EAADkYQEAAIBEml1c5Xa3/rpe3959/AAAZGMBAAAAAQAAAAgAAABAAAAAAAIAAAAQAAAAgAAAAAAEAAAAIAAAAAABAAAACAAAAEAAAACAAAAAAAQAAACwf40juJau6wq8qrZtI+J4PPqAAAAICwAAAKRiAbiy7Y57Ofq+j4h5nn00AACEBQAAAAQAAAAgAAAAAAEAAAAIAAAAoDgnnwJ0m6fae0o9AADckgUAAAASOfMegN9+qr2n1AMAwC1ZAL6YpmmaJucAAIAAAAAABECN7AAAAAgAAABAANTLDgAAgAAAAAB2rCn8+sb5IyJe397vdQHbCOBVZQAA1MECAAAAiZS+ADy/FPEtfDsAAAB1sAAAAEAi5S4AwzCUdkl2AAAA9s4CAAAAiRS3AJT/6H07AAAA+2UBAAAAAcA53hMMAIAAAAAAitY4gv/jbwAAANgjCwAAACRS3AJw3zvrl3yt371/AAD2ywIAAACJ+BuAH3DvHwCAvbMAAABAIhaAi7j3DwBAHSwAAACQiAXgDPf+AQCoiQUAAAASsQCc5N4/AAD1sQAAAEAiFoBvuPcPAECtLAAAAJCIBeAL9/4BAEgaAG3bRkTf984IAACq4StAAACQyMkFYPsyzDzPzggAAKphAQAAAAEAAAAIAAAAQAAAAAACAAAAKIwXgV2NNycAAFA+CwAAACTysCxLRKzr6iwAAKB6FgAAABAAAACAAAAAAAQAAAAgAAAAAAEAAAAIAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAAAgAAABAAAAAAAIAAAAQAAAAgAAAAAAEAAAAIAAAAAABAAAACAAAAEAAAAAAAgAAAAQAAAAgAAAAAAEAAAAIAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAACAAAAAAAQAAAAIAAAAQAAAAgAAAAAAEAAAAIAAAAAABAAAACAAAAEAAAAAAAgAAABAAAACAAAAAAAEAAAAIAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAACAAAAAAAQAAAAgAAABAAAAAAAIAAAAEAAAAIAAAAAABAAAACAAAAEAAAAAAAgAAABAAAACAAAAAAAQAAAAgAAAAAAEAAAACAAAAEAAAAIAAAAAABAAAACAAAACAO2vGcRzH0UEAAEAGfw6Hg1MAAIAkPgFAS2b4uVKNgQAAAABJRU5ErkJggg=="
        private val baseImage = ImageIO.read(decodeBase64(stonecuttingLeftImage)).asARGB()
        private val rightImage = ImageIO.read(decodeBase64(CraftingRecipeImageGenerator.craftingRightImage)).asARGB()
    }
}
