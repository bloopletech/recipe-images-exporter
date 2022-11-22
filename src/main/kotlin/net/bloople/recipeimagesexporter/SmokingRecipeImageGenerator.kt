package net.bloople.recipeimagesexporter

import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.awt.image.BufferedImage
import java.lang.Integer.max
import javax.imageio.ImageIO


class SmokingRecipeImageGenerator(
    private val recipeInfo: SmokingRecipeInfo,
    private val itemsData: ItemsData
) : RecipeImageGenerator {
    override val width = max(178 + rightImage.width, recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 44 + 8)
    override val height = 192

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return  BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
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
        private const val smokingLeftImage = "iVBORw0KGgoAAAANSUhEUgAAAgAAAADACAYAAAByD9tnAAAEh0lEQVR42u3dzZGiUBSA0esUWZEGiVDFxghMiw1FEIRAEM5m3oaRBmlEfs5ZzVTbjtKL+e71td66rnsGAHApf1wCALieLP2h73tXAwBsAAAAAQAACAAAQAAAAAIAANizbOoGZVnu4oEWRREREXmeX/IHdb/ff/z64/HY9HFs9e8BYAMAAGy1ARhO4N9SVVVERLRte8nJ38QNgA0AALDNBoDvSJP/2BmA4WZgeLvh949tEube/5ix+3/3cY89XxsQABsAAMAG4FqbgLmT99jkP/X3ufc/d/Jf6/uchQCwAQAAbAD4hrkT+dT7FwyN3Z/JH8AGAACwATBpL5mof2vuGQKTO4ANAABgA8CSSXvtCfu39z+2CZi6XxsCABsAAGBjt67rnhERfd+/vEH6NECfBQAANgAAgAAAAAQAACAAAAABAAAIAABAAAAAAgAAEAAAgAAAAAQAACAAAAABAAD8k7kE55Y+zfHo0qdR5nnuhwpgAwAA2AAwOUEfVVVVERHRtq0fJoANAAAgAAAAAQAAvDZ5BiC9dpxegwUAbAAAgDNuANLvXTt9DQA2AACAAGCoaZpomsaFAEAAAAACwCYAAAQAALD7AFh7sj37pGwTAIANAAAgAK7EJgAAAQAAfEW29BvTOwSmaTb9/dXE+5uvn3kTcMXnDYANAAAgAK7JmQAABAAAsInFZwDmTq1rnRVYY9Le+ybgk88fAGwAAEAAsLdNgDMBAAgAAGB1b58BeHc6XXr7q74W7gwAADYAAMA+NgBrT6zD3xI4y2T97vMx+QNgAwAA7HsDsNYEfFUmfwBsAACAY2wA5p7an3oHQJM/ANgAAAB72gB8+tT+2Sdjkz8ANgAAwDE2AEt5hz+TPwA2AADAkTYAnz61f7ZNgckfABsAAOCYG4C5E+3atzP5A4ANAAAgAAAAAQAA/GjyDEBZlrt4oEVRRITX1gHABgAA+MwGYDiBf0tVVRER0batnxoA2AAAAAIAABAAAMD/Mpfg3NLZjXSGAgBsAADABoAzSu+b4LcnALABAAABAAAIAABAAAAAAgAAEAAAgAAAAAQAACAAAAABAAAIAABAAAAAAgAAEAAAgAAAAAQAACAAAAABAAAIAAAQAACAAAAABAAAIAAAAAEAAAgAAGC3sqkbFEURERFVVblaAGADAAAc1a3rumdERN/3rgYA2AAAAAIAABAAAIAAAAAEAAAgAAAAAQAACAAAQAAAAAIAABAAAIAAAAAEAAAgAAAAAQAACAAAEAAAgAAAAAQAACAAAAABAAAIAABAAAAAAgAAEAAAgAAAAAQAACAAAAABAAAIAABAAAAAAgAAEAAAIAAAAAEAAAgAAEAAAAACAAAQAACAAAAABAAAIAAAAAEAAAgAAEAAAAACAAAQAACAAAAABAAAIAAAQAAAAAIAABAAAIAAAAAEAAAgAAAAAQAACAAAQAAAAAIAABAAAIAAAAAEAAAgAAAAAQAACAAAQAAAAAIAAAQAACAAAAABAAAIAABAAAAAR5FFRNR1HXVduxoAcJUNgP/8AeB6/gInXhpjfdIUAwAAAABJRU5ErkJggg=="
        private val baseImage = ImageIO.read(decodeBase64(smokingLeftImage)).asARGB()
        private val rightImage = ImageIO.read(decodeBase64(CraftingRecipeImageGenerator.craftingRightImage)).asARGB()
    }
}
