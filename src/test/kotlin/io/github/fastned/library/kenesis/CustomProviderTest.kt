package io.github.fastned.library.kenesis

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

class CustomProviderTest {
    @Test
    fun `Custom provider should be used`() {
        val specialClassInstance = assertDoesNotThrow { kenesis<SpecialIntAlwaysZero>() }
        assertEquals(expected = 0, actual = specialClassInstance.value, message = "Expected value to be 0")
    }

    @Test
    fun `Custom provider should be used in a class as well`() {
        val sampleClass: SampleClassWithNonDefaultProperties = kenesis()
        assertEquals(expected = 0, actual = sampleClass.alwaysZeroInt.value, message = "Expected alwaysZeroInt to be 0")
    }

    class SampleClassWithNonDefaultProperties(
        val alwaysZeroInt: SpecialIntAlwaysZero,
    )

    class SpecialIntAlwaysZero(
        val value: Int,
    )
}
