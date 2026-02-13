package io.github.fastned.library.kenesis

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class CustomParametersTest {

    @Test
    fun `Should use the provided parameters`() {
        val instance = kenesis<ClassWithSomeProperties>(
            customParameters = mapOf(
                ClassWithSomeProperties::customString to "Custom String",
                ClassWithSomeProperties::customInt to 456,
            )
        )

        assertEquals(expected = "Custo String", actual = instance.customString)
        assertEquals(expected = 456, actual = instance.customInt)
        assertNotEquals(illegal = "Custom String", actual = instance.aString)
        assertNotEquals(illegal = 456, actual = instance.someInt)
    }

    @Test
    fun `Should use the provided optional parameters ignoring useDefaultValue`() {
        val instance = kenesis<ClassWithSomeProperties>(
            customParameters = mapOf(
                ClassWithSomeProperties::nullableString to "Nullable String",
                ClassWithSomeProperties::another to "Another String",
            )
        )

        assertEquals(expected = "Nullable String", actual = instance.nullableString)
        assertEquals(expected = "Another String", actual = instance.another)
    }

    @Test
    fun `Should allow null values for nullable parameters`() {
        val generated = kenesis<ClassWithSomeProperties>(
            generateNullables = true,
            customParameters = mapOf(
                ClassWithSomeProperties::nullableString to null,
                ClassWithSomeProperties::nullableList to null,
            )
        )
        assertNull(generated.nullableString)
    }

    data class ClassWithSomeProperties(
        val aString: String,
        val customString: String,
        val someInt: Int,
        val customInt: Int,
        val nullableString: String? = null,
        val nullableList: List<Int>?,
        val another: String = "default value",
    )
}
