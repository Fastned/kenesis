package io.github.fastned.library.kenesis.config

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.fastned.library.kenesis.generators.DateGenerators
import io.github.fastned.library.kenesis.generators.StringGenerators
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.random.Random

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
        addProvider(LocalDate::class, DateGenerators::randomLocalDate)
        addProvider(LocalTime::class, DateGenerators::randomLocalTime)
        addProvider(LocalDateTime::class, DateGenerators::randomLocalDateTime)
        addProvider(ZonedDateTime::class, DateGenerators::randomZonedDateTime)
        addProvider(Instant::class, DateGenerators::randomInstant)
        addProvider(BigInteger::class) { BigInteger.valueOf(Random.nextLong()) }
        addProvider(BigDecimal::class) { BigDecimal.valueOf(Random.nextDouble()) }
        logger.info { "Kenesis base configuration initialized" }
    }
}
