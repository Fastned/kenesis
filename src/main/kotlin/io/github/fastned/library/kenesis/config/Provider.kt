package io.github.fastned.library.kenesis.config

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

abstract class Provider(values: MutableMap<KType, () -> Any> = mutableMapOf()) : BaseProvider {

    private val logger = KotlinLogging.logger {}

    private val providers: MutableMap<KType, () -> Any> = values

    val types: Map<KType, () -> Any>
        get() = providers.toMap()

    /**
     * Add a provider for a specific type.
     * @param generator the function that generates the value
     * @param T the type of the value
     */
    override fun <T : Any> addProvider(kClass: KClass<T>, generator: () -> T) {
        val returnType = kClass.createType()
        logger.info { "Added provider of type $returnType" }
        providers[returnType] = generator
    }
}
