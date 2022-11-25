package net.bloople.recipeimagesexporter

import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient.IS_SYSTEM_MAC
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gl.SimpleFramebuffer
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.render.*
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.util.ScreenshotRecorder
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.math.Matrix4f


fun renderToTexture(
    width: Int,
    height: Int,
    r: Float,
    g: Float,
    b: Float,
    scaleFactor: Double,
    block: () -> Unit
): NativeImage {
    RenderSystem.assertOnRenderThread()

    val framebuffer = SimpleFramebuffer(width, height, true, IS_SYSTEM_MAC)
    framebuffer.setClearColor(r, g, b, 0.0f)
    framebuffer.resize(width, height, IS_SYSTEM_MAC)

    try {
        val matrixStack: MatrixStack = RenderSystem.getModelViewStack()
        matrixStack.push()
        RenderSystem.applyModelViewMatrix()
        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT or GlConst.GL_COLOR_BUFFER_BIT, IS_SYSTEM_MAC)
        framebuffer.beginWrite(true)
        BackgroundRenderer.clearFog()
        RenderSystem.enableTexture()
        RenderSystem.enableCull()

        RenderSystem.viewport(0, 0, width, height)

        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, IS_SYSTEM_MAC)
        val matrix4f = Matrix4f.projectionMatrix(
            0.0f,
            (width.toDouble() / scaleFactor).toFloat(),
            0.0f,
            (height.toDouble() / scaleFactor).toFloat(),
            1000.0f,
            3000.0f
        )
        RenderSystem.setProjectionMatrix(matrix4f)
        val matrixStack2 = RenderSystem.getModelViewStack()
        matrixStack2.loadIdentity()
        matrixStack2.translate(0.0, 0.0, -2000.0)
        RenderSystem.applyModelViewMatrix()
        DiffuseLighting.enableGuiDepthLighting()

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, ClickableWidget.WIDGETS_TEXTURE)

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        block()

        RenderSystem.disableBlend()

        RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, IS_SYSTEM_MAC)

        framebuffer.endWrite()
        matrixStack.pop()
        matrixStack.push()
        RenderSystem.applyModelViewMatrix()
        framebuffer.draw(width, height)
        matrixStack.pop()
        RenderSystem.applyModelViewMatrix()

        return ScreenshotRecorder.takeScreenshot(framebuffer)
    }
    finally {
        framebuffer.delete()
    }
}

fun renderText(textRenderer: TextRenderer, text: Text, x: Int, y: Int, color: Int) {
    val matrixStack = MatrixStack()

    matrixStack.translate(0.0, 0.0, 0.0)
    val immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)

    textRenderer.draw(
        text,
        x.toFloat(),
        y.toFloat(),
        color,
        false,
        matrixStack.peek().positionMatrix,
        immediate as VertexConsumerProvider,
        false,
        0,
        LightmapTextureManager.MAX_LIGHT_COORDINATE
    )
    immediate.draw()
}