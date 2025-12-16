package io.github.fastned.library.kenesis.generators

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

private const val EPOCH_YEAR = 1970
private const val MAX_YEAR = 2100

private const val INITIAL_MONTH = 1
private const val FINAL_MONTH = 12

private const val INITIAL_DAY = 1

private const val INITIAL_TIME = 0
private const val FINAL_HOUR = 23
private const val FINAL_MINUTE = 59
private const val FINAL_SECOND = 59

object DateGenerators {
    fun randomLocalDate(): LocalDate {
        val year = (EPOCH_YEAR..MAX_YEAR).random()
        val month = (INITIAL_MONTH..FINAL_MONTH).random()
        val day = (INITIAL_DAY..LocalDate.of(year, month, 1).lengthOfMonth()).random()
        return LocalDate.of(year, month, day)
    }

    fun randomLocalTime(): LocalTime {
        val hour = (INITIAL_TIME..FINAL_HOUR).random()
        val minute = (INITIAL_TIME..FINAL_MINUTE).random()
        val second = (INITIAL_TIME..FINAL_SECOND).random()
        return LocalTime.of(hour, minute, second)
    }

    fun randomLocalDateTime(): LocalDateTime {
        return LocalDateTime.of(randomLocalDate(), randomLocalTime())
    }

    fun randomZonedDateTime(): ZonedDateTime {
        val localDateTime = randomLocalDateTime()
        return ZonedDateTime.of(localDateTime, ZoneId.systemDefault())
    }

    fun randomInstant(): Instant {
        return randomLocalDateTime().toInstant(ZoneOffset.UTC)
    }
}
