package net.bloople.recipeimagesexporter

import net.minecraft.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max
import javax.imageio.ImageIO


class CraftingRecipeImageGenerator(
    private val recipeInfo: CraftingRecipeInfo,
    private val itemsData: ItemsData
) : RecipeImageGenerator {
    override val width = max(214 + rightImage.width, recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 44 + 8)
    override val height = 122 + bottomImage.height + 6 + (recipeInfo.items.size * 30) + (max(recipeInfo.items.size - 1, 0) * 4)

    override fun generate(imageWidth: Int, imageHeight: Int): BufferedImage {
        return BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, baseImage.getData(0, 0, width, height))
            raster.setRect(0, height - 2, bottomImage.getData(0, 0, width, 2))
            raster.setRect(width - 2, 0, rightImage.getData(0, 0, 2, height))

            createGraphics().use {
                if(recipeInfo.slot1 != null) drawImage(itemsData.slotImage(recipeInfo.slot1), 11, 11, null)
                if(recipeInfo.slot2 != null) drawImage(itemsData.slotImage(recipeInfo.slot2), 47, 11, null)
                if(recipeInfo.slot3 != null) drawImage(itemsData.slotImage(recipeInfo.slot3), 83, 11, null)
                if(recipeInfo.slot4 != null) drawImage(itemsData.slotImage(recipeInfo.slot4), 11, 47, null)
                if(recipeInfo.slot5 != null) drawImage(itemsData.slotImage(recipeInfo.slot5), 47, 47, null)
                if(recipeInfo.slot6 != null) drawImage(itemsData.slotImage(recipeInfo.slot6), 83, 47, null)
                if(recipeInfo.slot7 != null) drawImage(itemsData.slotImage(recipeInfo.slot7), 11, 83, null)
                if(recipeInfo.slot8 != null) drawImage(itemsData.slotImage(recipeInfo.slot8), 47, 83, null)
                if(recipeInfo.slot9 != null) drawImage(itemsData.slotImage(recipeInfo.slot9), 83, 83, null)
                drawImage(itemsData.outputImage(recipeInfo.output), 166, 46, null)

                val x = 8
                var y = 122

                for(item in recipeInfo.items) {
                    drawImage(itemsData.slotLabelImage(ItemStack(item, 1)), x, y, null)
//                    val labelImage = recipesExporter.getLabel(item)
//                    raster.setRect(44, y + 7, labelImage.getData(0, 0, width - 44 - 2, labelImage.height))
                    drawImage(itemsData.labelImage(item), 44, y + 6, null)
                    y += 34
                }
            }
        }
    }

    companion object {
        private const val craftingTopLeftImage = "iVBORw0KGgoAAAANSUhEUgAABAAAAAQABAMAAACNMzawAAAAFVBMVEXb29ubm5tbW1vGxsY3NzeLi4v////TAbtBAAAHI0lEQVR42u3cwQmEMBRFUXv6LdjCb8H+S5h9xCEMImPeucssHweiC902ScmVkgMAAAEgAASAUgHsYz1zYjwAtAiAHjpmTowHgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgFYBsF/lQzIAlACgL3JhACAABID+EEAfYzMnAAAAwCJXwG2/iAEAAAA8BAIAAABRABoAAABIBtAAAABAMoAGAAAAkgE0AAAAkAygAQAAgCwAwwkAAACQDKABAAAAVwAAAADgNRAAAACIAlAAAABALoACAAAAcgEUAAAAkAugAAAAgFwABQAAAAAAAAAAvBGAX8QAAED0FeAXMQAA4CHQQyAAAAAAAAAAAPAjgPPL4reXRgGgtQAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAD0H4L5fxAgAvQ+AABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAABgAwAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACAAAbACAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAANgAAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAAwAYACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABAAANgBAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAACwAQACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABAIANABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAAGwAgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAAYAMABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAgAAGwAgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAAEgAASAABAAAkAACAABIAAEgAAQAAJAAAgAASAABIAAEAACQAAIAD3aB4UGTr2qmgFLAAAAAElFTkSuQmCC"
        const val craftingRightImage = "iVBORw0KGgoAAAANSUhEUgAAAAIAAAQABAMAAAA5H5UPAAAAFVBMVEXb29ubm5tbW1vGxsY3NzeLi4v////TAbtBAAAAG0lEQVQ4y2NgZBBiUBqFo3AUjsJROApH4UiCAKATh9ANBbyxAAAAAElFTkSuQmCC"
        private const val craftingBottomImage = "iVBORw0KGgoAAAANSUhEUgAABAAAAAACBAMAAAA0tLOoAAAAFVBMVEXb29ubm5tbW1vGxsY3NzeLi4v////TAbtBAAAAFUlEQVQ4y2NgVBoFIxkwCI2GwYgGAPfCh9AdT+awAAAAAElFTkSuQmCC"
        private val baseImage = ImageIO.read(decodeBase64(craftingTopLeftImage)).asARGB()
        private val rightImage = ImageIO.read(decodeBase64(craftingRightImage)).asARGB()
        private val bottomImage = ImageIO.read(decodeBase64(craftingBottomImage)).asARGB()
    }
}
