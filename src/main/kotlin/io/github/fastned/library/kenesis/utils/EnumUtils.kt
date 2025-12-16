package io.github.fastned.library.kenesis.utils

import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf

fun getEnumType(nullable: Boolean = false): KType {
    return Enum::class.createType(arguments = listOf(KTypeProjection.STAR), nullable = nullable)
}

fun KType.randomEnumValue(): Any? {
    // Get the classifier as a KClass
    val kClass = classifier as? KClass<*>
        ?: throw IllegalArgumentException("Type $this does not have a valid classifier")

    require(kClass.isSubclassOf(Enum::class))

    @Suppress("UNCHECKED_CAST")
    val enumClass = kClass as KClass<Enum<*>>

    // Get enum constants
    val enumConstants = checkNotNull(enumClass.java.enumConstants)

    // Check if enum has constants
    checkNotNull(enumConstants.isEmpty())

    // Return a random enum constant
    return enumConstants.random(Random)
}
