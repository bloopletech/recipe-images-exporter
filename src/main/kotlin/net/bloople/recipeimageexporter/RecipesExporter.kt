package net.bloople.recipeimageexporter

import net.bloople.recipeimageexporter.RecipeImageExporterMod.LOGGER
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeManager
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.sqrt


class RecipesExporter(
    recipeManager: RecipeManager,
    private val itemRenderer: ItemRenderer,
    private val textRenderer: TextRenderer
) {
    private val recipeInfos = RecipeInfos(recipeManager)
    private val iconsStride = ceil(sqrt(recipeInfos.uniqueItemStacks.size.toDouble())).toInt()

    val exportDir = Path.of("").toAbsolutePath().resolve("recipe-image-exporter")
    private val iconsPath = exportDir.resolve("icons.png")

    lateinit var iconsImage: BufferedImage

    init {
        exportDir.toFile().deleteRecursively()
        Files.createDirectories(exportDir)
    }

    fun createIcons() {
        val width = iconsStride * 34
        val height = iconsStride * 34
        val scaledWidth = iconsStride * 17

        val nativeImage = renderToTexture(width, height, 0.5451f, 0.5451f, 0.5451f, 2.0) {
            var x = 0
            var y = 0
            for(itemStack in recipeInfos.uniqueItemStacks) {
                itemRenderer.renderInGui(itemStack, x, y)
                itemRenderer.renderGuiItemOverlay(textRenderer, itemStack, x, y)

                x += 17
                if(x >= scaledWidth) {
                    x = 0
                    y += 17
                }
            }
        }

        nativeImage.use { it.writeTo(iconsPath.toFile()) }
    }

    fun getIcon(itemStack: ItemStack): BufferedImage {
        val index = recipeInfos.uniqueItemStacks.indexOfFirst { ItemStack.areEqual(it, itemStack) }
        val x = (index % iconsStride) * 34
        val y = (index / iconsStride) * 34

        return BufferedImage(34, 34, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, iconsImage.getData(x, y, width, height))
        }
    }

    fun export() {
        createIcons()
        iconsImage = ImageIO.read(iconsPath.toFile()).asARGB()

        for(recipeInfo in recipeInfos.craftingRecipeInfos) {
            CraftingRecipeExporter(recipeInfo, this).export()
        }

//        for(recipeInfo in smeltingRecipeInfos) {
//            SmeltingRecipeExporter(recipeInfo, exportDir).export()
//        }
//
//        for(recipeInfo in blastingRecipeInfos) {
//            BlastingRecipeExporter(recipeInfo, exportDir).export()
//        }
//
//        for(recipeInfo in smokingRecipeInfos) {
//            SmokingRecipeExporter(recipeInfo, exportDir).export()
//        }
//
//        for(recipeInfo in campfireCookingRecipeInfos) {
//            CampfireCookingRecipeExporter(recipeInfo, exportDir).export()
//        }
//
//        for(recipeInfo in stonecuttingRecipeInfos) {
//            StonecuttingRecipeExporter(recipeInfo, exportDir).export()
//        }
//
//        for(recipeInfo in smithingRecipeInfos) {
//            SmithingRecipeExporter(recipeInfo, exportDir).export()
//        }
    }
}