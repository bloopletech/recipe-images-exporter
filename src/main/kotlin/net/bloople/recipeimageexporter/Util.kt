package net.bloople.recipeimageexporter

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.AlphaComposite
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Rectangle
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.awt.image.Raster
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*
import kotlin.math.PI
import kotlin.reflect.KClass

fun <T> Array<out T>.getOrLast(index: Int): T? {
    if(isEmpty()) return null
    return if (index in 0..lastIndex) get(index) else get(lastIndex)
}

val Item.identifier: Identifier get() = Registry.ITEM.getId(this)

val Identifier.itemResourceLocation: Identifier get() = Identifier(namespace, "textures/item/$path.png")

val ItemStack.uniqueKey: String get() = "${item.identifier}:$count"

fun decodeBase64(input: String): InputStream {
    return ByteArrayInputStream(Base64.getDecoder().decode(input))
}

fun Raster.createChild(parentX: Int, parentY: Int, width: Int, height: Int): Raster {
    return this.createChild(parentX, parentY, width, height, parentX, parentY, null)
}

fun BufferedImage.asARGB(): BufferedImage {
    if(type == BufferedImage.TYPE_INT_ARGB) return this
    return BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).apply {
        val graphics = graphics
        graphics.drawImage(this@asARGB, 0, 0, null)
        graphics.dispose()
    }
}

fun BufferedImage.blankClone(): BufferedImage {
    return BufferedImage(width, height, type)
}

fun BufferedImage.getData(x: Int, y: Int, width: Int, height: Int): Raster {
    return this.getData(Rectangle(x, y, width, height)).createTranslatedChild(0, 0)
}

// Based on https://stackoverflow.com/a/46211880
fun BufferedImage.scaleImage(w2: Int, h2: Int): BufferedImage {
    // Create a new image of the proper size
    val scalex = w2 / width.toDouble()
    val scaley = h2 / height.toDouble()
    val after = BufferedImage(w2, h2, type)
    val scaleInstance = AffineTransform.getScaleInstance(scalex, scaley)
    val scaleOp = AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_BICUBIC)
    scaleOp.filter(this, after)
    return after
}

// Based on https://stackoverflow.com/a/47994302
fun BufferedImage.rotateImage(degrees: Double): BufferedImage {
    val affineTransform = AffineTransform.getRotateInstance(
        degrees * (PI / 180.0),
        width / 2.0,
        height / 2.0)
    val affineTransformOp = AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BICUBIC)
    return affineTransformOp.filter(this, null)
}

private fun createFlipTransform(mode: ImageFlipMode, imageWidth: Double, imageHeight: Double): AffineTransform {
    return when(mode) {
        ImageFlipMode.NORMAL -> AffineTransform()
        ImageFlipMode.TOP_BOTTOM -> {
            AffineTransform(doubleArrayOf(1.0, 0.0, 0.0, -1.0)).apply { translate(0.0, -imageHeight) }
        }
        ImageFlipMode.LEFT_RIGHT -> {
            AffineTransform(doubleArrayOf(-1.0, 0.0, 0.0, 1.0)).apply { translate(-imageWidth, 0.0) }
        }
        ImageFlipMode.TOP_BOTTOM_LEFT_RIGHT -> {
            AffineTransform(doubleArrayOf(-1.0, 0.0, 0.0, -1.0)).apply { translate(-imageWidth, -imageHeight) }
        }
    }
}

enum class ImageFlipMode {
    NORMAL,
    TOP_BOTTOM,
    LEFT_RIGHT,
    TOP_BOTTOM_LEFT_RIGHT
}

// Based on https://www.informit.com/articles/article.aspx?p=23667&seqNum=11

fun BufferedImage.flipImage(mode: ImageFlipMode): BufferedImage {
    val affineTransform =
        createFlipTransform(mode, width.toDouble(), height.toDouble())
    val affineTransformOp = AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BICUBIC)
    return affineTransformOp.filter(this, null)
}

inline fun <R> Graphics2D.use(block: Graphics2D.() -> R): R {
    try {
        return block(this)
    }
    finally {
        dispose()
    }
}

inline fun <R> Graphics2D.applyComposite(newComposite: AlphaComposite, block: Graphics2D.() -> R): R {
    val oldComposite = composite
    composite = newComposite
    return block(this).apply { composite = oldComposite }
}

fun Graphics2D.drawImage(image: Image) {
    drawImage(image, 0, 0, null)
}

fun getLogger(name: String): Logger {
    return LoggerFactory.getLogger("$MOD_ID/$name")
}

fun getLogger(clazz: KClass<*>): Logger {
    val fullName = if(clazz.isCompanion) clazz.java.declaringClass.name
    else clazz.qualifiedName

    val name = fullName!!.removePrefix("net.bloople.recipeimageexporter.")

    return getLogger(name)
}