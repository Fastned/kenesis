package io.github.fastned.library.kenesis.config

/**
 * No-op annotation retained for source compatibility.
 *
 * Auto discovery does not read this annotation. Every [KenesisGenerator] on the classpath is discovered,
 * annotated or not, so applying it has no effect and omitting it changes nothing. It briefly gated
 * discovery, which silently broke generators written before it existed; it is kept only so code that
 * already applies it still compiles.
 */
@Deprecated("Has no effect: all KenesisGenerator implementations are auto discovered without it.")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class KenesisAutoDiscover
