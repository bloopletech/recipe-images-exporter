package net.bloople.recipeimageexporter

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

fun getLogger(name: String): Logger {
    return LoggerFactory.getLogger("$MOD_ID/$name")
}

fun getLogger(clazz: KClass<*>): Logger {
    val fullName = if(clazz.isCompanion) clazz.java.declaringClass.name
    else clazz.qualifiedName

    val name = fullName!!.removePrefix("net.bloople.recipeimageexporter.")

    return getLogger(name)
}