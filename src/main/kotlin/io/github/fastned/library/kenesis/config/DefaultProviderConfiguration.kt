package io.github.fastned.library.kenesis.config

import io.github.fastned.library.kenesis.generators.DateGenerators
import io.github.fastned.library.kenesis.generators.StringGenerators
import io.github.oshai.kotlinlogging.KotlinLogging
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.random.Random

private const val CHAR_RANGE_START = 32
private const val CHAR_RANGE_END = 127

class DefaultProviderConfiguration : Provider() {
    private val logger = KotlinLogging.logger {}

    fun loadBaseConfiguration() {
        addProvider(Int::class, Random::nextInt)
        addProvider(Long::class, Random::nextLong)
        addProvider(Float::class, Random::nextFloat)
        addProvider(Double::class, Random::nextDouble)
        addProvider(Boolean::class, Random::nextBoolean)
        addProvider(String::class, StringGenerators::randomString)
        addProvider(UUID::class, UUID::randomUUID)
        addProvider(Byte::class) { Random.nextInt().toByte() }
        addProvider(Short::class) { Random.nextInt().toShort() }
        addProvider(Char::class) { Random.nextInt(CHAR_RANGE_START, CHAR_RANGE_END).toChar() }
        addProvider(UInt::class) { Random.nextInt().toUInt() }
        addProvider(ULong::class) { Random.nextLong().toULong() }
        addProvider(UByte::class) { Random.nextInt().toUByte() }
        addProvider(UShort::class) { Random.nextInt().toUShort() }
        addProvider(LocalDate::class, DateGenerators::randomLocalDate)
        addProvider(LocalTime::class, DateGenerators::randomLocalTime)
        addProvider(LocalDateTime::class, DateGenerators::randomLocalDateTime)
        addProvider(ZonedDateTime::class, DateGenerators::randomZonedDateTime)
        addProvider(OffsetDateTime::class, DateGenerators::randomOffsetDateTime)
        addProvider(Instant::class, DateGenerators::randomInstant)
        addProvider(BigInteger::class) { BigInteger.valueOf(Random.nextLong()) }
        addProvider(BigDecimal::class) { BigDecimal.valueOf(Random.nextDouble()) }
        logger.info { "Kenesis base configuration initialized" }
    }
}
