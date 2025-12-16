package io.github.fastned.library.kenesis.config

interface KenesisGenerator<T> {
    fun generate(): T
}
