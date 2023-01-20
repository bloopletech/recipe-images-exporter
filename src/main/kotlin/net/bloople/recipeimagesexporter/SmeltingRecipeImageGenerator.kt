package net.bloople.recipeimagesexporter

import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.registry.Registries
import java.awt.image.BufferedImage
import java.lang.Integer.max
import javax.imageio.ImageIO


class SmeltingRecipeImageGenerator(
    private val recipeInfo: SmeltingRecipeInfo,
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
                val coalStack = ItemStack(Registries.ITEM.get(Identifier("coal")), 1)
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
        private const val smeltingLeftImage = "iVBORw0KGgoAAAANSUhEUgAABAAAAADACAIAAAAV2IZzAAAE4ElEQVR42u3dzXGjMBiA4S+71OMGaINGmFGBXBiKcAk0kYNmZ3JYvLC2MZKe55JMnB8PXPLqs+Sv+/0eAABAG365BAAA0I4uf1jX1bUAAIDqmQAAAIAAAAAABAAAACAAAAAAAQAAAFxMt/XAOI4n/PlhGCKi73t3AgAATmACAAAADekeP5xX6N8npRQRy7K4EwAAcAITAAAAEAAAAIAAAAAABAAAACAAAAAAAQAAAAgAAABAAAAAAAIAAAAQAAAAgAAAAAAEAAAACAAAAEAAAAAAAgAAABAAAACAAAAAAAQAAAAgAAAAAAEAAAAIAAAAQAAAAAACAAAABAAAACAAAACASnQuwTnGcbzgsxqGISL6vneDAAAaYQIAAAANMQE4VV5xv46UUkQsy+LWAAA0wgQAAAAEAAAAIAAAAICibe4ByK9Wz68RBwAA6mACAAAADdmcAOSz4Z0PAwAANTEBAAAAAdCSeZ7neXYdAAAQAAAAgACokTkAAAACAAAAaDUAnlkjL2V93RwAAAABAAAACIB6mQMAACAAAACA4nX7vzW/N3BeGs+f/3T069dX7jMHAIAtJgAAACAA+MN+AAAABAAAAFCkA3sAHi+E/98OgaM+tRhvPwAAAHUwAQAAAAHA39gPAACAAAAAAIqxaw/AnmXv/d9T7ivp7QEAAKB0JgAAANCQ7ugPHF0F/3k60PPesQa/57lZ+wcAoA4mAAAA0JDu+V9R98E41v4BAKiJCQAAADTk8ATg8Uk+W+8BXCJr/wAA1McEAAAAGrJrAvCqk3xKWVO39g8AQK1MAAAAoCHda3+d9/oFAIArMwEAAICG7JoAvOoknyvPB6z9AwDQAhMAAABoyIFTgN7x6BVY+wcAoB0mAAAAIAAAAAABAAAAFG1zD8A4jif8+WEYwqvwAQDgLCYAAADQkH+cApRX6N8npRQRy7K4EwAAcAITAAAAEAAAAIAAAAAAita5BOfIuynyngcAAPgUEwAAAGiICcBJ8nsdOO8IAIDPMgEAAAABAAAACAAAAEAAAAAAAgAAABAAAACAAAAAAAQAAAAgAAAAAAEAAAAIAAAAQAAAAIAAAAAABAAAACAAAAAAAQAAAAgAAABAAAAAAAIAAAAQAAAAgAAAAAAEAAAAIAAAAEAAAAAAAgAAAKhEt/XAMAwRkVJyjQAAoBomAAAA0JCv+/0eEeu6uhYAAFA9EwAAABAAAACAAAAAAAQAAAAgAAAAAAEAAAAIAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAAAgAAABAAAAAAAIAAAAQAAAAgAAAAAAEAAAAIAAAAAABAAAACAAAAEAAAAAAAgAAAAQAAAAgAAAAAAEAAAAIAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAACAAAAAAAQAAAAIAAAAQAAAAgAAAAAAEAAAAIAAAAAABAAAACAAAAEAAAAAAAgAAABAAAACAAAAAAAEAAAAIAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAACAAAAAAAQAAAAgAAABAAAAAAAIAAAAEAAAAIAAAAAABAAAACAAAAEAAAAAAAgAAABAAAACAAAAAAAQAAAAgAAAAAAEAAAACAAAAEAAAAIAAAAAABAAAACAAAACAD+siYpqmaZpcCwAAqN7v2+3mv38AAGjEN+eEoeCDKRi7AAAAAElFTkSuQmCC"
        private val baseImage = ImageIO.read(decodeBase64(smeltingLeftImage)).asARGB()
        private val rightImage = ImageIO.read(decodeBase64(CraftingRecipeImageGenerator.craftingRightImage)).asARGB()
    }
}
