package io.github.fastned.library.kenesis

import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class NullableGeneratorTest {
    @Test
    fun `Should not generate nullable parameters by default`() {
        val generated = kenesis<ClassWithNullables>()
        assertNull(generated.string)
        assertNull(generated.int)
        assertNull(generated.list)
    }

    @Test
    fun `Should generate nullable parameters if specified`() {
        val generated = kenesis<ClassWithNullables>(generateNullables = true)
        assertNotNull(generated.string)
        assertNotNull(generated.int)
        assertNotNull(generated.list)
    }

    @Test
    fun `Should generate nullable parameters also for subclasses`() {
        val generated = kenesis<ClassWithNullables>(generateNullables = true)
        assertNotNull(generated.subclass)
        assertNotNull(generated.subclass?.subString)
        assertNotNull(generated.subclass?.subInt)
        assertNotNull(generated.subclass?.subList)
    }

    data class ClassWithNullables(
        val string: String?,
        val int: Int?,
        val list: List<String>?,
        val subclass: SubclassWithNullables?,
    )

    data class SubclassWithNullables(
        val subString: String?,
        val subInt: Int?,
        val subList: List<String>?,
    )
}
