package io.github.fastned.library.kenesis.generators

import io.github.fastned.library.kenesis.config.KenesisGenerator

class UnannotatedGeneratorClass(val value: Int)

class UnannotatedGeneratorClassGenerator : KenesisGenerator<UnannotatedGeneratorClass> {
    override fun generate() = UnannotatedGeneratorClass(999)
}
