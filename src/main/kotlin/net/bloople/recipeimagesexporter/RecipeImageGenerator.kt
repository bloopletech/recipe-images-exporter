package net.bloople.recipeimagesexporter

import java.awt.image.BufferedImage

interface RecipeImageGenerator {
    val width: Int
    val height: Int
    fun generate(imageWidth: Int = width, imageHeight: Int = height): BufferedImage
}