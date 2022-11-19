package net.bloople.recipeimagesexporter

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.recipe.RecipeManager
import java.nio.file.Files
import java.nio.file.Path


class RecipesExporter(
    recipeManager: RecipeManager,
    private val itemRenderer: ItemRenderer,
    private val textRenderer: TextRenderer
) {
    private val recipeInfos = RecipeInfos(recipeManager)
    private val exportDir: Path = Path.of("").toAbsolutePath().resolve("recipe-images-exporter")

    fun export() {
        exportDir.toFile().deleteRecursively()
        Files.createDirectories(exportDir)

        val itemIconsExtractor = ItemIconsExtractor(recipeInfos.itemStacks, exportDir, itemRenderer, textRenderer)
        itemIconsExtractor.extract()

        val itemLabelsExtractor = ItemLabelsExtractor(recipeInfos.uniqueItems, exportDir, textRenderer)
        itemLabelsExtractor.extract()

        val itemsData = ItemsData(
            itemIconsExtractor.slotIcons,
            itemIconsExtractor.labelIcons,
            itemIconsExtractor.transparentIcons,
            itemLabelsExtractor.labels,
            itemLabelsExtractor.widths
        )

        for(recipeInfo in recipeInfos.craftingRecipeInfos) {
            CraftingRecipeExporter(recipeInfo, exportDir, itemsData).export()
        }

        for(recipeInfo in recipeInfos.smeltingRecipeInfos) {
            SmeltingRecipeExporter(recipeInfo, exportDir, itemsData).export()
        }

        for(recipeInfo in recipeInfos.blastingRecipeInfos) {
            BlastingRecipeExporter(recipeInfo, exportDir, itemsData).export()
        }

        for(recipeInfo in recipeInfos.smokingRecipeInfos) {
            SmokingRecipeExporter(recipeInfo, exportDir, itemsData).export()
        }
//
//        for(recipeInfo in campfireCookingRecipeInfos) {
//            CampfireCookingRecipeExporter(recipeInfo, exportDir).export()
//        }

        for(recipeInfo in recipeInfos.stonecuttingRecipeInfos) {
            StonecuttingRecipeExporter(recipeInfo, exportDir, itemsData).export()
        }

        for(recipeInfo in recipeInfos.smithingRecipeInfos) {
            SmithingRecipeExporter(recipeInfo, exportDir, itemsData).export()
        }
    }
}