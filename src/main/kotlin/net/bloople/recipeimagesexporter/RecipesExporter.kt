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

        val itemIconsExtractor = ItemIconsExtractor(
            recipeInfos.itemStacks,
            exportDir.resolve("icons.png"),
            itemRenderer,
            textRenderer
        )
        itemIconsExtractor.extract()

        val itemLabelsExtractor = ItemLabelsExtractor(
            recipeInfos.uniqueItems,
            exportDir.resolve("labels.png"),
            textRenderer
        )
        itemLabelsExtractor.extract()

        val itemsData = ItemsData(itemIconsExtractor.icons, itemLabelsExtractor.labels, itemLabelsExtractor.widths)

        for(recipeInfo in recipeInfos.craftingRecipeInfos) {
            CraftingRecipeExporter(recipeInfo, exportDir, itemsData).export()
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