package io.github.fastned.library.kenesis

import io.github.fastned.library.kenesis.core.KenesisFactory
import kotlin.reflect.KProperty1

inline fun <reified T : Any> kenesis(
    generateNullables: Boolean = false,
    useDefaultValues: Boolean = true,
    customParameters: Map<KProperty1<T, *>, Any?> = emptyMap(),
) = KenesisFactory.instance(
    targetClass = T::class,
    generateNullables = generateNullables,
    useDefaultValues = useDefaultValues,
    customParams = customParameters,
)
