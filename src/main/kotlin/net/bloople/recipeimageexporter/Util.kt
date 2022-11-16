package net.bloople.recipeimageexporter

import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

fun <T> Array<out T>.getOrLast(index: Int): T? {
    if(isEmpty()) return null
    return if (index in 0..lastIndex) get(index) else get(lastIndex)
}

val Item.identifier: Identifier get() = Registry.ITEM.getId(this)

val Identifier.itemResourceLocation: Identifier get() = Identifier(namespace, "textures/item/$path.png")

fun getLogger(name: String): Logger {
    return LoggerFactory.getLogger("$MOD_ID/$name")
}

fun getLogger(clazz: KClass<*>): Logger {
    val fullName = if(clazz.isCompanion) clazz.java.declaringClass.name
    else clazz.qualifiedName

    val name = fullName!!.removePrefix("net.bloople.recipeimageexporter.")

    return getLogger(name)
}