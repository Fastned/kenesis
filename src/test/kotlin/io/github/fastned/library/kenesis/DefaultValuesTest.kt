package io.github.fastned.library.kenesis

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class DefaultValuesTest {

    @Test
    fun `Should use default values by default`() {
        val instance = kenesis<ClassWithDefaultValues>()
        assertEquals(actual = instance.string, expected = "My value")

        assertEquals(actual = instance.nullableString, expected = "My other value")

        assertEquals(actual = instance.int, expected = 42)

        assertNull(instance.nullableInt)
    }

    @Test
    fun `Should not use default values when asking`() {
        val instance = kenesis<ClassWithDefaultValues>(useDefaultValues = false)
        assertNotEquals(actual = instance.string, illegal = "My value")

        assertNull(instance.nullableString)

        assertNotEquals(actual = instance.int, illegal = 42)

        assertNull(instance.nullableInt)
    }

    data class ClassWithDefaultValues(
        val string: String = "My value",
        val nullableString: String? = "My other value",
        val int: Int = 42,
        val nullableInt: Int? = null,
    )
}
