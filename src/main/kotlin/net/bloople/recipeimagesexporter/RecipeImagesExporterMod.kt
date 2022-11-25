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
import java.util.concurrent.CompletionException


const val MOD_ID = "recipeimagesexporter"

@Suppress("UNUSED")
@EnvironmentInterface(value=EnvType.CLIENT, itf=ClientModInitializer::class)
object RecipeImagesExporterMod: ClientModInitializer {
    private var exportRunning = false

    @Environment(value=EnvType.CLIENT)
    override fun onInitializeClient() {
        LOGGER.info("onInitializeClient()")

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

        LOGGER.info("end onInitializeClient()")
    }

    private fun export(client: MinecraftClient) {
        try {
            if(exportRunning) {
                client.sendMessage("Please wait for the current export to complete before starting another one")
                return
            }

            exportRunning = true
            RecipesExporter().export(client).thenRunAsync { exportRunning = false }.exceptionallyAsync { exception ->
                handleException(client, if(exception is CompletionException) exception.cause else exception)
                return@exceptionallyAsync null
            }
        }
        catch(exception: Exception) {
            handleException(client, exception)
        }
    }

    private fun handleException(client: MinecraftClient, throwable: Throwable?) {
        LOGGER.error("Caught exception during export", throwable)
        exportRunning = false
        client.sendMessage("Export failed due to an error, see log for details")
    }

    val LOGGER: Logger = getLogger(this::class)
}