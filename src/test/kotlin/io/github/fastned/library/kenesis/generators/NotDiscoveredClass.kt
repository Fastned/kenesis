package io.github.fastned.library.kenesis.generators

import io.github.fastned.library.kenesis.config.KenesisGenerator

class NotDiscoveredClass(val value: Int)

class NotDiscoveredClassGenerator : KenesisGenerator<NotDiscoveredClass> {
    override fun generate() = NotDiscoveredClass(999)
}
