rootProject.name = "kenesis"

pluginManagement {
    val kotlinVersion: String by settings
    val dependencyCheckVersion: String by settings
    val sonarqubeVersion: String by settings
    val detektVersion: String by settings
    val testLoggerVersion: String by settings
    val mavenPublishPluginVersion: String by settings
    val semanticReleasePluginVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        id("org.owasp.dependencycheck") version dependencyCheckVersion
        id("org.sonarqube") version sonarqubeVersion
        id("io.gitlab.arturbosch.detekt") version detektVersion
        id("com.adarshr.test-logger") version testLoggerVersion
        id("com.vanniktech.maven.publish") version mavenPublishPluginVersion
    }
}

