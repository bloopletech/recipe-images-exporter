package net.bloople.recipeimagesexporter

import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
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
        private const val smeltingLeftImage = "iVBORw0KGgoAAAANSUhEUgAAAgAAAADACAIAAAD9bUwwAAADfElEQVR42u3d3W2jQBSA0ZuEetwAbUwjSFMgL2iKoASayAMvliIi/IPtYc55WWm9UiKQ9rvXg+WveZ4DgPZ8uwQAberWP5ZlcS0AbAAACAAAAgCAAAAgAADUqdt6YRiGF/z4lFJE9H3vTgDYAAB46wZwPaEfJ+ccEaUUdwLABgCAAAAgAAAIAAACAIAAACAAAAgAAAIAgAAAIAAAAgCAAAAgAAAIAAACAIAAACAAAAgAAAIAgAAAIAAACAAAAgCAAABwrM4leI1hGD7wt0opRUTf924Q2AAAsAFw2MT9OXLOEVFKcWvABgCAAAAgAACcz+YZwPpu9foeMQA2AADOvgGsz4Z7PgTABgCAAJzLNE3TNLkOgAAAIAD2AAABAKDdADwyI9cyX9sDAAEAQADaYw8ABACA07rh+wDWzwavo/Hf75C69e+r2APCt2UBNgAABKAtzgMAAQDgVG44A/h/EL7vhOCOefxde0A4DwBsAAAIQFucBwACAED1dp0B7Bl79/+bet9JdwYA2AAAaGMDeGQKvn466DNn8D2/m9kfsAEA0OoGcN8EXS+zP2ADAKDtDeD/J3m2PgNs9gewAQBQzwbwrCd5apmpzf6ADQCAtjeA/XzWF8AGAED9G8CznuT55P3A7A/YAACwAeybjh951ewPYAMAQAAAEAAADrJ5BjAMwwt+fEopvAsPYAMA4P0bwPWEfpycc0SUUtwJABsAAAIAgAAA8FydS/Aa62nKeuYBYAMAwAZwdutnHTzvBNgAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAAABAEAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAAABAEAAABAAAN6m23ohpRQROWfXCMAGAMB5fM3zHBHLsrgWADYAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAAABAEAAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAEAAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAAABAEAAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAAABABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAAABAEAAABAAAAQAAAEAQAAAEAAABAAAAQBAAAAQAAAEAAABABAAAAQAAAEAQAAAOJsuIsZxHMfRtQBoys/lcvG/P0CDfgHwVaHgtdjolwAAAABJRU5ErkJggg=="
        private val baseImage = ImageIO.read(decodeBase64(smeltingLeftImage)).asARGB()
        private val rightImage = ImageIO.read(decodeBase64(CraftingRecipeImageGenerator.craftingRightImage)).asARGB()
    }
}
