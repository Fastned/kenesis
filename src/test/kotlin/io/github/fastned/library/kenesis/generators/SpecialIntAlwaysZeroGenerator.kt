package io.github.fastned.library.kenesis.generators

import io.github.fastned.library.kenesis.CustomProviderTest
import io.github.fastned.library.kenesis.config.KenesisGenerator

class SpecialIntAlwaysZeroGenerator : KenesisGenerator<CustomProviderTest.SpecialIntAlwaysZero> {
    override fun generate(): CustomProviderTest.SpecialIntAlwaysZero {
        return CustomProviderTest.SpecialIntAlwaysZero(0)
    }
}
