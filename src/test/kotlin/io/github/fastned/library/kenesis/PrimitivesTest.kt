package io.github.fastned.library.kenesis

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class PrimitivesTest {

    @Test
    fun `Should generate random Int`() {
        assertDoesNotThrow {
            kenesis<Int>()
        }
    }

    @Test
    fun `Should generate a random Long`() {
        assertDoesNotThrow {
            kenesis<Long>()
        }
    }

    @Test
    fun `Should generate a random Float`() {
        assertDoesNotThrow {
            kenesis<Float>()
        }
    }

    @Test
    fun `Should generate a random Double`() {
        assertDoesNotThrow {
            kenesis<Double>()
        }
    }

    @Test
    fun `Should generate a random Boolean`() {
        assertDoesNotThrow {
            kenesis<Boolean>()
        }
    }

    @Test
    fun `Should generate a data class with primitive types`() {
        assertDoesNotThrow {
            kenesis<TestClassWithPrimitives>()
        }
    }

    @Test
    fun `Should generate a string`() {
        assertDoesNotThrow {
            kenesis<String>()
        }
    }

    data class TestClassWithPrimitives(
        val intValue: Int,
        val longValue: Long,
        val floatValue: Float,
        val doubleValue: Double,
        val booleanValue: Boolean,
    )
}
