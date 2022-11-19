package net.bloople.recipeimagesexporter

import net.fabricmc.api.*
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.recipe.RecipeManager
import org.lwjgl.glfw.GLFW
import org.slf4j.Logger


const val MOD_ID = "recipeimagesexporter"
const val MOD_NAME = "RecipeImagesExporter"

@Suppress("UNUSED")
@EnvironmentInterface(value=EnvType.CLIENT, itf=ClientModInitializer::class)
object RecipeImagesExporterMod: ClientModInitializer {
    private lateinit var keyBinding: KeyBinding

    @Environment(value=EnvType.CLIENT)
    override fun onInitializeClient() {
        LOGGER.info("$MOD_NAME onInitializeClient()")

        keyBinding = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.$MOD_ID.export",  // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM,  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_R,  // The keycode of the key
                "category.$MOD_ID.export" // The translation key of the keybinding's category.
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: MinecraftClient ->
            while(keyBinding.wasPressed()) {
                export(client)
            }
        })

        LOGGER.info("$MOD_NAME end onInitializeClient()")
    }

    private fun export(client: MinecraftClient) {
        RecipesExporter(
            client.world!!.recipeManager,
            client.runDirectory,
            client.itemRenderer,
            client.textRenderer
        ).export()
    }

    val LOGGER: Logger = getLogger(this::class)
}