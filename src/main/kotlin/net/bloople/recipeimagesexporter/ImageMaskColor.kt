package net.bloople.recipeimagesexporter

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.random.Random
import kotlin.random.nextUInt

fun findMaskColor(input: BufferedImage): Color {
    val pixels = getPixels(input)

    var red: Int
    var green: Int
    var blue: Int
    var argb: Int
    while(true) {
        red = Random.nextUInt(256u).toInt()
        green = Random.nextUInt(256u).toInt()
        blue = Random.nextUInt(256u).toInt()
        argb = 0xFF shl 24 or (red shl 16) or (green shl 8) or (blue shl 0)

        if(!pixels.contains(argb)) break
    }

    return Color(red, green, blue, 255)
}

private fun getPixels(input: BufferedImage): IntArray {
    return IntArray(input.width * input.height).apply {
        for(x in 0 until input.width) {
            for(y in 0 until input.height) {
                this[x * y] = input.getRGB(x, y)
            }
        }
    }
}

fun applyMaskColor(color: Color, destination: BufferedImage) {
    destination.apply {
        for(x in 0 until width) {
            for(y in 0 until height) {
                if(getRGB(x, y) == color.rgb) setRGB(x, y, 0)
            }
        }
    }
}