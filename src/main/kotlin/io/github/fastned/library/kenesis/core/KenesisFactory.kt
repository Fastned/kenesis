package io.github.fastned.library.kenesis.core

import io.github.fastned.library.kenesis.config.DefaultProviderConfiguration
import io.github.fastned.library.kenesis.config.KenesisGenerator
import io.github.fastned.library.kenesis.utils.getEnumType
import io.github.fastned.library.kenesis.utils.loadCustomGenerators
import io.github.fastned.library.kenesis.utils.randomEnumValue
import io.github.oshai.kotlinlogging.KotlinLogging
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

    @Suppress("TooGenericExceptionThrown")
    fun <T> instance(
        targetClass: KClass<out Any>,
        generateNullables: Boolean = false,
        useDefaultValues: Boolean = true,
        customParams: Map<KProperty1<T, *>, Any?> = emptyMap(),
    ) = runCatching {
        val type = targetClass.createType()
        resolveInstance(type, targetClass, generateNullables, useDefaultValues, customParams)
    }
        .getOrElse { error ->
            logger.error(error) { "Error while creating instance of class [${targetClass.qualifiedName}]" }
            throw RuntimeException(
                "Could not generate random value for class [${targetClass.qualifiedName}]",
                error
            )
        }

    @Suppress("UNCHECKED_CAST")
    private fun <T> resolveInstance(
        type: KType,
        targetClass: KClass<out Any>,
        generateNullables: Boolean,
        useDefaultValues: Boolean,
        customParams: Map<KProperty1<T, *>, Any?>,
    ): T = when {
        builtInProviders.containsKey(type) -> {
            logger.debug { "Using built-in provider for type $type" }
            builtInProviders[type]!!() as T
        }

        customGenerators.containsKey(type) -> {
            logger.debug { "Using custom generator for type $type" }
            customGenerators[type]!!.generate() as T
        }

        else -> instantiateViaConstructor(targetClass, generateNullables, useDefaultValues, customParams)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> instantiateViaConstructor(
        targetClass: KClass<out Any>,
        generateNullables: Boolean,
        useDefaultValues: Boolean,
        customParams: Map<KProperty1<T, *>, Any?>,
    ): T {
        val primaryConstructor = validatePrimaryConstructor(targetClass)
        val constructorArguments = primaryConstructor.parameters
            .filter { parameter -> shouldGenerateConstructorParameter(parameter, useDefaultValues, customParams) }
            .associateWith(generateConstructorParameter(customParams, generateNullables, useDefaultValues))
        return primaryConstructor.callBy(constructorArguments) as T
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
        useDefaultValues: Boolean,
    ): (KParameter) -> Any? = { parameter ->
        if (customParams.keys.any { customParam -> customParam.name == parameter.name }) {
            customParams.entries.first { it.key.name == parameter.name }.value
        } else {
            parameter.type.randomValue(
                generateNulls = generateNullables,
                useDefaultValues = useDefaultValues,
            )
        }
    }

    private fun validatePrimaryConstructor(targetClass: KClass<out Any>): KFunction<Any> {
        return requireNotNull(targetClass.primaryConstructor) {
            "Target class [${targetClass.qualifiedName}] has no primary constructor"
        }
    }

    private fun KType.randomValue(
        generateNulls: Boolean = false,
        useDefaultValues: Boolean = true,
    ): Any? {
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
                    useDefaultValues = useDefaultValues,
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
