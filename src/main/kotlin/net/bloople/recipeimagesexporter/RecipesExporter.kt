package net.bloople.recipeimagesexporter

import net.minecraft.client.MinecraftClient
import net.minecraft.util.Util
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

fun exportRecipes(client: MinecraftClient): CompletableFuture<Void> {
    client.sendMessage("Starting export of recipe images")

    lateinit var exportDir: Path

    val step1 = CompletableFuture.runAsync({
        exportDir = client.runDirectory.toPath().toAbsolutePath().resolve("recipe-images-exporter")
        exportDir.toFile().deleteRecursively()
        Files.createDirectories(exportDir)

        client.sendMessage("Deleted any existing export and created export directory $exportDir")
    }, Util.getIoWorkerExecutor())

    lateinit var recipeInfos: RecipeInfos
    lateinit var itemIconsExtractor: ItemIconsExtractor
    lateinit var itemLabelsExtractor: ItemLabelsExtractor

    val step2 = step1.thenRunAsync({
        recipeInfos = RecipeInfos(client.world!!.recipeManager)
        client.sendMessage("Gathered ${recipeInfos.allRecipeInfos.size} recipes for export")

        itemIconsExtractor = ItemIconsExtractor(recipeInfos.itemStacks, exportDir, client.itemRenderer, client.textRenderer)
        itemIconsExtractor.exportIcons()

        itemLabelsExtractor = ItemLabelsExtractor(recipeInfos.items, exportDir, client.textRenderer)
        itemLabelsExtractor.exportLabels()

        client.sendMessage("Generated icons and labels")
    }, client)

    val step3 = step2.thenRunAsync({
        itemIconsExtractor.importIcons()
        itemLabelsExtractor.importLabels()

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
        client.sendMessage("Exported ${recipeInfos.craftingRecipeInfos.size} crafting recipes")

        for(recipeInfo in recipeInfos.smeltingRecipeInfos) {
            SmeltingRecipeExporter(recipeInfo, exportDir, itemsData).export()
        }
        client.sendMessage("Exported ${recipeInfos.smeltingRecipeInfos.size} smelting recipes")

        for(recipeInfo in recipeInfos.blastingRecipeInfos) {
            BlastingRecipeExporter(recipeInfo, exportDir, itemsData).export()
        }
        client.sendMessage("Exported ${recipeInfos.blastingRecipeInfos.size} blasting recipes")

        for(recipeInfo in recipeInfos.smokingRecipeInfos) {
            SmokingRecipeExporter(recipeInfo, exportDir, itemsData).export()
        }
        client.sendMessage("Exported ${recipeInfos.smokingRecipeInfos.size} smoking recipes")

        for(recipeInfo in recipeInfos.campfireCookingRecipeInfos) {
            CampfireCookingRecipeExporter(recipeInfo, exportDir, itemsData).export()
        }
        client.sendMessage("Exported ${recipeInfos.campfireCookingRecipeInfos.size} campfire cooking recipes")

        for(recipeInfo in recipeInfos.stonecuttingRecipeInfos) {
            StonecuttingRecipeExporter(recipeInfo, exportDir, itemsData).export()
        }
        client.sendMessage("Exported ${recipeInfos.stonecuttingRecipeInfos.size} stonecutting recipes")

        for(recipeInfo in recipeInfos.smithingRecipeInfos) {
            SmithingRecipeExporter(recipeInfo, exportDir, itemsData).export()
        }
        client.sendMessage("Exported ${recipeInfos.smithingRecipeInfos.size} smithing recipes")

        client.sendMessage("Export complete")
    }, Util.getIoWorkerExecutor())

    return step3
}
