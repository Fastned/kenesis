package io.github.fastned.library.kenesis.utils

import io.github.classgraph.ClassGraph
import io.github.fastned.library.kenesis.config.KenesisGenerator
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

fun loadCustomGenerators(): Map<KType, KenesisGenerator<*>> {
    val valueGenerators = ClassGraph().enableClassInfo().scan().use { scanResult ->
        scanResult.getClassesImplementing(KenesisGenerator::class.java).loadClasses().toSet()
    }

    return valueGenerators.associate {
        val type = extractGenericType(it).createType()
        val instance = it.getDeclaredConstructor().newInstance() as KenesisGenerator<*>
        type to instance
    }
}

private fun extractGenericType(clazz: Class<*>): KClass<*> {
    return clazz.genericInterfaces.filterIsInstance<ParameterizedType>()
        .firstOrNull { (it.rawType as? Class<*>) == KenesisGenerator::class.java }
        ?.actualTypeArguments[0]?.let {
        when (it) {
            is Class<*> -> it.kotlin
            is ParameterizedType -> (it.rawType as Class<*>).kotlin
            else -> throw IllegalArgumentException("Cannot determine type parameter for ${clazz.name}")
        }
    } ?: throw IllegalArgumentException("Cannot find KenesisValueGenerator interface in ${clazz.name}")
}
