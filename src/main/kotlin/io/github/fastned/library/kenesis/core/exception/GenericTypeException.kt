package io.github.fastned.library.kenesis.core.exception

class GenericTypeException(
    message: String,
    cause: Throwable? = null,
) : IllegalArgumentException(message, cause)
