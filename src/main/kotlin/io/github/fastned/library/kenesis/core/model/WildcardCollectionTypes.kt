package io.github.fastned.library.kenesis.core.model

import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType

val wildcardMapType = Map::class.createType(arguments = listOf(KTypeProjection.STAR, KTypeProjection.STAR))
val wildcardNullableMapType = Map::class.createType(
    arguments = listOf(KTypeProjection.STAR, KTypeProjection.STAR),
    nullable = true
)
val wildcardCollectionType = Collection::class.createType(arguments = listOf(KTypeProjection.STAR))
val wildcardNullableCollectionType = Collection::class.createType(
    arguments = listOf(KTypeProjection.STAR),
    nullable = true
)
val wildcardListType = List::class.createType(arguments = listOf(KTypeProjection.STAR))
val wildcardNullableListType = List::class.createType(arguments = listOf(KTypeProjection.STAR), nullable = true)
val wildcardSetType = Set::class.createType(arguments = listOf(KTypeProjection.STAR))
val wildcardNullableSetType = Set::class.createType(arguments = listOf(KTypeProjection.STAR), nullable = true)
