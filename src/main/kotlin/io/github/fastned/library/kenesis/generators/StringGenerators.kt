package io.github.fastned.library.kenesis.generators

object StringGenerators {
    fun randomString(length: Int = 20): String {
        val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }
}
