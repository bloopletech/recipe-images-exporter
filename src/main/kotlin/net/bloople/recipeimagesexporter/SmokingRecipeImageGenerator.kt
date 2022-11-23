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
        private const val smokingLeftImage = "iVBORw0KGgoAAAANSUhEUgAABAAAAADACAIAAAAV2IZzAAAFf0lEQVR42u3dzW3iUBSA0ZsZ15MGaMONWPImFaQtNshFuAQ3MQtrJDSOGfNneO+es0kExCCzyfcuz3yM4xgAAEAOv5wCAADIo5l/TNPkXAAAQPVMAAAAQAAAAAACAAAAEAAAAIAAAAAA3kyzdkfXdTs8fdu2EXE4HAo9fV9fXz/e/v39/fBneewxAQDIyQQAAAASaS7fPa/QP0/f9xExDENxJ86qPAAAJTIBAACARBqn4Dbz2v9yD8D5TOD83vPHL+cGl4+ztDzOlleyfM0mGAAA2ZgAAABAIiYAd1lbyz+/fbn2v/b75eOs3X7PI+1kAADIxgQAAAASMQEoxuXV+rVvJDi3/Ftr/wAA2ZgAAABAIiYAN7r20/P3r7Vf3jNgLR8AgC1MAAAAIBETgBtt+R6AZxxnOQdYO8JtrwcAgLqZAAAAQCIf4zhGxDRN/9zRdV1EtG371Kfv+z4ihmHwTgAAwA5MAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAACAAAAAAAQAAAAgAAABAAAAAAAIAAAAQAAAAIAAAAIBqNU7BPrque8NX1bZtRBwOB28QAEASJgAAAJCICcCu5hX399H3fUQMw+CtAQBIwgQAAAAEAAAAIAAAAICire4BmD+tPn9GHAAAqIMJAAAAJLI6AZivDe/6MAAAUBMTAAAAEACZnE6n0+nkPAAAIAAAAAABUCNzAAAABAAAAJA1AO5ZIy9lfd0cAAAAAQAAAAiAepkDAAAgAAAAgOI12x86fzfwvDQ+/37u2tvfX7mvHAAA1pgAAACAAOAv+wEAABAAAABAka7YA3B5Ify2HQLXetVivP0AAADUwQQAAAAEAD+xHwAAAAEAAAAUY9MegC3L3tsfU+4n6e0BAACgdCYAAACQSHPtH1y7Cn5+daD7PWMNfstrs/YPAEAdTAAAACCR5v5D1H1hHGv/AADUxAQAAAASuXoCcPlKPmvfAVwia/8AANTHBAAAABLZNAF41JV8SllTt/YPAECtTAAAACCR5rGH812/AADwzkwAAAAgkU0TgEddyeed5wPW/gEAyMAEAAAAErniKkDPuPcdWPsHACAPEwAAABAAAACAAAAAAIq2ugeg67odnr5t2/ApfAAA2IsJAAAAJPKfqwDNK/TP0/d9RAzD4J0AAIAdmAAAAIAAAAAABAAAAFC0xinYx7ybYt7zAAAAr2ICAAAAiZgA7GT+rgPXOwIA4LVMAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAACAAAAAAAQAAAAgAAABAAAAAAAIAAAAQAAAAIAAAAAABAAAACAAAAEAAAAAAAgAAABAAAACAAAAAAAQAAAAgAAAAAAEAAAAIAAAAEAAAAIAAAAAAKtGs3dG2bUT0fe8cAQBANUwAAAAgkY9xHCNimibnAgAAqmcCAAAAAgAAABAAAACAAAAAAAQAAAAgAAAAAAEAAAAIAAAAQAAAAAACAAAAEAAAAIAAAAAAAQAAAAgAAABAAAAAAAIAAAAQAAAAgAAAAAAEAAAAIAAAAAABAAAACAAAAEAAAACAAAAAAAQAAAAgAAAAAAEAAAAIAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAACAAAABAAAAAAAIAAAAQAAAAgAAAAAAEAAAAIAAAAAABAAAACAAAAEAAAAAAAgAAABAAAAAgAAAAAAEAAAAIAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAACAAAAAAAQAAAAgAAABAAAAAgAAAAAAEAAAAIAAAAAABAAAACAAAAEAAAAAAAgAAABAAAACAAAAAAAQAAAAgAAAAQAAAAAACAAAAEAAAAIAAAAAABAAAAPBiTUQcj8fj8ehcAABA9X5/fn767x8AAJL4A/OcFbFtQzs2AAAAAElFTkSuQmCC"
        private val baseImage = ImageIO.read(decodeBase64(smokingLeftImage)).asARGB()
        private val rightImage = ImageIO.read(decodeBase64(CraftingRecipeImageGenerator.craftingRightImage)).asARGB()
    }
}
