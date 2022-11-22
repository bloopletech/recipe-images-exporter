package net.bloople.recipeimagesexporter

import net.minecraft.client.MinecraftClient
import net.minecraft.util.Util
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageOutputStream

class RecipesExporter {
    private lateinit var exportDir: Path
    private lateinit var itemsData: ItemsData

    fun export(client: MinecraftClient): CompletableFuture<Void> {
        client.sendMessage("Starting export of recipe images")

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
            client.sendMessage("Gathered ${recipeInfos.all.size} recipes for export")

            itemIconsExtractor = ItemIconsExtractor(
                recipeInfos.itemStacks,
                exportDir,
                client.itemRenderer,
                client.textRenderer
            )
            itemIconsExtractor.exportIcons()

            itemLabelsExtractor = ItemLabelsExtractor(recipeInfos.items, exportDir, client.textRenderer)
            itemLabelsExtractor.exportLabels()

            client.sendMessage("Generated icons and labels")
        }, client)

        val step3 = step2.thenRunAsync({
            itemIconsExtractor.importIcons()
            itemLabelsExtractor.importLabels()

            itemsData = ItemsData(
                itemIconsExtractor.slotIcons,
                itemIconsExtractor.labelIcons,
                itemIconsExtractor.transparentIcons,
                itemLabelsExtractor.labels,
                itemLabelsExtractor.widths
            )

            exportList(recipeInfos.crafting)
            exportGroups(recipeInfos.craftingGroups)
            client.sendMessage("Exported ${recipeInfos.crafting.size} crafting recipes")

            exportList(recipeInfos.smelting)
            exportGroups(recipeInfos.smeltingGroups)
            client.sendMessage("Exported ${recipeInfos.smelting.size} smelting recipes")

            exportList(recipeInfos.blasting)
            exportGroups(recipeInfos.blastingGroups)
            client.sendMessage("Exported ${recipeInfos.blasting.size} blasting recipes")

            exportList(recipeInfos.smoking)
            exportGroups(recipeInfos.smokingGroups)
            client.sendMessage("Exported ${recipeInfos.smoking.size} smoking recipes")

            exportList(recipeInfos.campfireCooking)
            exportGroups(recipeInfos.campfireCookingGroups)
            client.sendMessage("Exported ${recipeInfos.campfireCooking.size} campfire cooking recipes")

            exportList(recipeInfos.stonecutting)
            exportGroups(recipeInfos.stonecuttingGroups)
            client.sendMessage("Exported ${recipeInfos.stonecutting.size} stonecutting recipes")

            exportList(recipeInfos.smithing)
            exportGroups(recipeInfos.smithingGroups)
            client.sendMessage("Exported ${recipeInfos.smithingGroups.size} smithing recipes")

            client.sendMessage("Export complete")
        }, Util.getIoWorkerExecutor())

        return step3
    }

    private fun exportList(recipeInfos: List<RecipeInfo>) {
        for(recipeInfo in recipeInfos) writeRecipeImage(recipeInfo, recipeInfo.imageGenerator(itemsData).export())
    }

    private fun exportGroups(recipeInfoGroups: List<List<RecipeInfo>>) {
        for(recipeInfos in recipeInfoGroups) {
            if(recipeInfos.size == 1) continue
            val exporters = recipeInfos.map { it.imageGenerator(itemsData) }
            val width = exporters.maxOf { it.width }
            val height = exporters.maxOf { it.height }
            val images = exporters.map { it.export(width, height) }
            writeRecipesAnimation(recipeInfos[0], images)
        }
    }

    private fun writeRecipeImage(recipeInfo: RecipeInfo, image: BufferedImage) {
        val recipeFilePath = exportDir.resolve("${recipeInfo.recipePath}.png")
        Files.createDirectories(recipeFilePath.parent)
        ImageIO.write(image, "PNG", recipeFilePath.toFile())
    }

    private fun writeRecipesAnimation(firstRecipeInfo: RecipeInfo, images: List<BufferedImage>) {
        val recipeFilePath = exportDir.resolve("${firstRecipeInfo.recipeBasePath}.gif")
        Files.createDirectories(recipeFilePath.parent)
        FileImageOutputStream(recipeFilePath.toFile()).use { outStream ->
            GifSequenceWriter(outStream, images[0].type, 500, true).use {
                for(image in images) it.writeToSequence(image)
            }
        }
    }
}

