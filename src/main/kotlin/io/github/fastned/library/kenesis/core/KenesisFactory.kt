package io.github.fastned.library.kenesis.core

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.fastned.library.kenesis.config.DefaultProviderConfiguration
import io.github.fastned.library.kenesis.config.KenesisGenerator
import io.github.fastned.library.kenesis.utils.getEnumType
import io.github.fastned.library.kenesis.utils.loadCustomGenerators
import io.github.fastned.library.kenesis.utils.randomEnumValue
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability

private val wildcardMapType = Map::class.createType(arguments = listOf(KTypeProjection.STAR, KTypeProjection.STAR))
private val wildcardNullableMapType = Map::class.createType(
    arguments = listOf(KTypeProjection.STAR, KTypeProjection.STAR),
    nullable = true
)
private val wildcardCollectionType = Collection::class.createType(arguments = listOf(KTypeProjection.STAR))
private val wildcardNullableCollectionType = Collection::class.createType(
    arguments = listOf(KTypeProjection.STAR),
    nullable = true
)
private val wildcardListType = List::class.createType(arguments = listOf(KTypeProjection.STAR))
private val wildcardNullableListType = List::class.createType(arguments = listOf(KTypeProjection.STAR), nullable = true)
private val wildcardSetType = Set::class.createType(arguments = listOf(KTypeProjection.STAR))
private val wildcardNullableSetType = Set::class.createType(arguments = listOf(KTypeProjection.STAR), nullable = true)

private const val MINIMUM_COLLECTION_SIZE = 1
private const val MAXIMUM_COLLECTION_SIZE = 10

object KenesisFactory {
    private val logger = KotlinLogging.logger {}

    private val config = DefaultProviderConfiguration()

    private val builtInProviders: Map<KType, () -> Any>
        get() = config.types

    private val customGenerators: Map<KType, KenesisGenerator<*>> = loadCustomGenerators()

    init {
        logger.debug { "Initializing KenesisFactory with base configuration" }
        config.loadBaseConfiguration()
    }

    @Suppress("TooGenericExceptionCaught", "TooGenericExceptionThrown", "UNCHECKED_CAST")
    fun <T> instance(
        targetClass: KClass<out Any>,
        generateNullables: Boolean = false,
        useDefaultValues: Boolean = true,
        customParams: Map<KProperty1<T, *>, Any?> = emptyMap(),
    ): T {
        try {
            val type = targetClass.createType()
            logger.debug { "Checking the type: $type" }
            return if (builtInProviders.containsKey(type)) {
                builtInProviders[type]!!.let {
                    logger.debug { "Using custom provider for type $type" }
                    it() as T
                }
            } else if (customGenerators.containsKey(type)) {
                customGenerators[type]!!.let {
                    it.generate() as T
                }
            } else {
                val primaryConstructor = validatePrimaryConstructor(targetClass = targetClass)

                val constructorArguments = primaryConstructor.parameters
                    .filter { parameter ->
                        shouldGenerateConstructorParameter(parameter, useDefaultValues, customParams)
                    }
                    .associateWith(generateConstructorParameter(customParams, generateNullables))

                return primaryConstructor.callBy(constructorArguments) as T
            }
        } catch (e: Exception) {
            logger.error(e) { "Error while creating instance of class [${targetClass.qualifiedName}]" }
            throw RuntimeException("Could not generate random value for class [${targetClass.qualifiedName}]", e)
        }
    }

    private fun <T> shouldGenerateConstructorParameter(
        parameter: KParameter,
        useDefaultValues: Boolean,
        customParams: Map<KProperty1<T, *>, Any?>,
    ): Boolean {
        val isMandatory = !parameter.isOptional
        val dontUseDefaultValues = !useDefaultValues
        val customParamDefined = customParams.keys.any { it.name == parameter.name }
        return isMandatory || dontUseDefaultValues || customParamDefined
    }

    private fun <T> generateConstructorParameter(
        customParams: Map<KProperty1<T, *>, Any?>,
        generateNullables: Boolean,
    ): (KParameter) -> Any? = { parameter ->
        if (customParams.keys.any { customParam -> customParam.name == parameter.name }) {
            customParams.entries.first { it.key.name == parameter.name }.value
        } else {
            parameter.type.randomValue(generateNulls = generateNullables)
        }
    }

    private fun validatePrimaryConstructor(targetClass: KClass<out Any>): KFunction<Any> {
        return requireNotNull(targetClass.primaryConstructor) {
            "Target class [${targetClass.qualifiedName}] has no primary constructor"
        }
    }

    private fun KType.randomValue(generateNulls: Boolean = false): Any? {
        val nonNullableType = withNullability(false)

        return when {
            isMarkedNullable && !generateNulls -> {
                logger.debug { "Field marked as nullable" }
                null
            }

            builtInProviders.containsKey(nonNullableType) -> {
                logger.debug { "ProviderFound $nonNullableType." }
                builtInProviders[nonNullableType]!!.invoke()
            }

            customGenerators.containsKey(nonNullableType) -> {
                logger.debug { "Generator found $nonNullableType." }
                customGenerators[nonNullableType]!!.generate()
            }

            isSubtypeOf(getEnumType()) -> {
                logger.debug { "Enum subtype" }
                randomEnumValue()
            }

            isSubtypeOf(getEnumType(nullable = true)) -> {
                logger.debug { "Nullable enum subtype" }
                randomEnumValue()
            }

            isSubtypeOf(wildcardCollectionType) -> fillCollection(this)
            isSubtypeOf(wildcardNullableCollectionType) -> fillCollection(this)
            isSubtypeOf(wildcardMapType) -> fillMap(this)
            isSubtypeOf(wildcardNullableMapType) -> fillMap(this)

            classifier is KClass<*> -> {
                logger.debug { "is KClass $classifier" }
                instance(
                    targetClass = classifier as KClass<*>,
                    generateNullables = generateNulls,
                )
            }

            else -> TODO("Missing support for $this")
        }
    }

    private fun fillCollection(collectionType: KType): Any {
        val valueType = collectionType.arguments[0].type!!

        val randomValues = (MINIMUM_COLLECTION_SIZE..MAXIMUM_COLLECTION_SIZE).map { valueType.randomValue() }

        return when {
            collectionType.isSubtypeOf(wildcardListType) -> randomValues
            collectionType.isSubtypeOf(wildcardNullableListType) -> randomValues
            collectionType.isSubtypeOf(wildcardSetType) -> randomValues.toSet()
            collectionType.isSubtypeOf(wildcardNullableSetType) -> randomValues.toSet()
            collectionType.isSubtypeOf(wildcardCollectionType) -> randomValues
            collectionType.isSubtypeOf(wildcardNullableCollectionType) -> randomValues
            else -> TODO("No support for $collectionType")
        }
    }

    private fun fillMap(mapType: KType): Any {
        val keyType = mapType.arguments[0].type!!
        val valueType = mapType.arguments[1].type!!

        return (MINIMUM_COLLECTION_SIZE..MAXIMUM_COLLECTION_SIZE).associate {
            keyType.randomValue() to valueType.randomValue()
        }
    }
}
