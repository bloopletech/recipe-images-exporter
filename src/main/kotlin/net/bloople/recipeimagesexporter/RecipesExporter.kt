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

    fun export(client: MinecraftClient): CompletableFuture<Void> {
        client.sendMessage("Starting export of recipe images")

        val step1 = CompletableFuture.runAsync({
            client.sendMessage("Deleting any existing export")
            exportDir = client.runDirectory.toPath().toAbsolutePath().resolve("recipe-images-exporter")
            exportDir.toFile().deleteRecursively()
            Files.createDirectories(exportDir)

            client.sendMessage("Deleted any existing export and created export directory $exportDir")
        }, Util.getIoWorkerExecutor())

        lateinit var recipeInfos: RecipeInfos
        lateinit var itemIconsExtractor: ItemIconsExtractor
        lateinit var itemLabelsExtractor: ItemLabelsExtractor

        val step2 = step1.thenRunAsync({
            client.sendMessage("Gathering icons and labels")
            recipeInfos = RecipeInfos(client.world!!.recipeManager)
            client.sendMessage("Gathered ${recipeInfos.all.size} recipes for export")
            client.sendMessage("Generating icons and labels")

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

            val itemsData = ItemsData(
                itemIconsExtractor.slotIcons,
                itemIconsExtractor.labelIcons,
                itemIconsExtractor.transparentIcons,
                itemLabelsExtractor.labels,
                itemLabelsExtractor.widths
            )

            var count = 0
            for(groupChunk in recipeInfos.groups.chunked(100)) {
                for(group in groupChunk) exportGroup(group, itemsData)
                count += groupChunk.size
                client.sendMessage("Exported $count crafting recipes")
            }

            client.sendMessage("Export complete")
        }, Util.getIoWorkerExecutor())

        return step3
    }

    private fun exportGroup(group: List<RecipeInfo>, itemsData: ItemsData) {
        for(recipeInfo in group) writeRecipeImage(recipeInfo, recipeInfo.imageGenerator(itemsData).generate())

        if(group.size == 1) return
        val imageGenerators = group.map { it.imageGenerator(itemsData) }
        val maxWidth = imageGenerators.maxOf { it.width }
        val maxHeight = imageGenerators.maxOf { it.height }
        val images = imageGenerators.map { it.generate(maxWidth, maxHeight) }
        writeRecipesAnimation(group[0], images)
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

