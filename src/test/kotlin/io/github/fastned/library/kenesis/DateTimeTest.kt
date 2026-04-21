package io.github.fastned.library.kenesis

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime

class DateTimeTest {

    @Test
    fun `Should generate a random LocalDate`() {
        assertDoesNotThrow {
            kenesis<LocalDate>()
        }
    }

    @Test
    fun `Should generate a random LocalTime`() {
        assertDoesNotThrow {
            kenesis<LocalTime>()
        }
    }

    @Test
    fun `Should generate a random LocalDateTime`() {
        assertDoesNotThrow {
            kenesis<LocalDateTime>()
        }
    }

    @Test
    fun `Should generate a random ZonedDateTime`() {
        assertDoesNotThrow {
            kenesis<ZonedDateTime>()
        }
    }

    @Test
    fun `Should generate a random Instant`() {
        assertDoesNotThrow {
            kenesis<Instant>()
        }
    }

    @Test
    fun `Should generate a random OffsetDateTime`() {
        val result = assertDoesNotThrow {
            kenesis<OffsetDateTime>()
        }
        val validRangeInSeconds = (-18 * 3600)..(18 * 3600)
        assertTrue(result.offset.totalSeconds in validRangeInSeconds)
    }

    @Test
    fun `Should generate a data class with date time types`() {
        assertDoesNotThrow {
            kenesis<TestClassWithDateTimeTypes>()
        }
    }

    data class TestClassWithDateTimeTypes(
        val localDate: LocalDate,
        val localTime: LocalTime,
        val localDateTime: LocalDateTime,
        val zonedDateTime: ZonedDateTime,
        val instant: Instant,
        val offsetDateTime: OffsetDateTime,
    )
}
