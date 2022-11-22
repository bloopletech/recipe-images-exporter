package net.bloople.recipeimagesexporter

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.*
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import net.minecraft.command.CommandRegistryAccess
import org.slf4j.Logger


const val MOD_ID = "recipeimagesexporter"
const val MOD_NAME = "RecipeImagesExporter"

@Suppress("UNUSED")
@EnvironmentInterface(value=EnvType.CLIENT, itf=ClientModInitializer::class)
object RecipeImagesExporterMod: ClientModInitializer {
    private var exportRunning = false

    @Environment(value=EnvType.CLIENT)
    override fun onInitializeClient() {
        LOGGER.info("$MOD_NAME onInitializeClient()")

        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback {
            dispatcher: CommandDispatcher<FabricClientCommandSource?>, _: CommandRegistryAccess? ->
            dispatcher.register(
                ClientCommandManager.literal("export-recipe-images").executes {
                    context: CommandContext<FabricClientCommandSource> ->
                    export(context.source.client)
                    0
                }
            )
        })

        LOGGER.info("$MOD_NAME end onInitializeClient()")
    }

    private fun export(client: MinecraftClient) {
        if(exportRunning) {
            client.sendMessage("Please wait for the current export to complete before starting another one")
            return
        }

        exportRunning = true
        RecipesExporter().export(client).thenRunAsync { exportRunning = false }
    }

    val LOGGER: Logger = getLogger(this::class)
}