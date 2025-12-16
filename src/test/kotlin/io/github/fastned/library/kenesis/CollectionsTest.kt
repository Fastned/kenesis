package io.github.fastned.library.kenesis

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertTrue

class CollectionsTest {
    @Test
    fun `Should generate an instance with a list inside`() {
        val instance = assertDoesNotThrow { kenesis<ClassWithList>() }
        assertTrue { instance.stringList.isNotEmpty() }
    }

    @Test
    fun `Should generate an instance with a set inside`() {
        val instance = assertDoesNotThrow { kenesis<ClassWithSet>() }
        assertTrue { instance.stringSet.isNotEmpty() }
    }

    @Test
    fun `Should generate an instance with a map inside`() {
        val instance = assertDoesNotThrow { kenesis<ClassWithMap>() }
        assertTrue { instance.stringMap.isNotEmpty() }
    }

    data class ClassWithList(
        val stringList: List<String>,
    )

    data class ClassWithSet(
        val stringSet: Set<String>,
    )

    data class ClassWithMap(
        val stringMap: Map<String, Int>,
    )
}
