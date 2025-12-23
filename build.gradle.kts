import io.gitlab.arturbosch.detekt.getSupportedKotlinVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
	jacoco
	`maven-publish`
	`java-library`
    signing

    kotlin("jvm") version libs.versions.kotlinVersion

	alias(libs.plugins.dependency.check.plugin)
	alias(libs.plugins.sonarqube.plugin)
	alias(libs.plugins.detekt.plugin)
	alias(libs.plugins.test.logger.plugin)
	alias(libs.plugins.release.plugin)
    alias(libs.plugins.vanniktech.plugin)
}

group = "io.github.fastned"

repositories {
	mavenCentral()
	mavenLocal()
}

dependencies {
    detektPlugins(libs.detekt.formatting)

    implementation(libs.kotlin.logging)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
    implementation(libs.logging.slf4j.api)
    implementation(libs.classgraph)

    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.logging.slf4j.simple)
    testRuntimeOnly(libs.junit.platform)
}

dependencyCheck {
    failBuildOnCVSS = 0f
    suppressionFile = "project-suppression.xml"
    autoUpdate = System.getProperty("dependencyCheckAutoUpdate")?.trim()?.lowercase() != "false"
}

detekt {
    buildUponDefaultConfig = true
    allRules = true
    autoCorrect = System.getProperty("autoCorrect") == "true"
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21

	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

sonar {
    properties {
        property("sonar.coverage.jacoco.xmlReportPaths", "../build/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.exclusions", "**/build.gradle.kts")
    }
}

tasks.withType<Test> {
	useJUnitPlatform()
	testLogging.showStandardStreams = true
}

tasks.withType<KotlinCompile> {
	compilerOptions {
		freeCompilerArgs.add("-Xjsr305=strict")
		jvmTarget.set(JvmTarget.JVM_21)
	}
}

configurations.matching { it.name == "detekt" }.all {
	resolutionStrategy.eachDependency {
		if (requested.group == "org.jetbrains.kotlin") {
			useVersion(getSupportedKotlinVersion())
		}
	}
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(
        groupId = "io.github.fastned",
        artifactId = "kenesis",
        version = project.version.toString(),
    )

    pom {
        name.set("Kenesis")
        description.set("A Kotlin library for generating instances of data classes with nullable properties.")
        url.set("https://github.com/fastned/kenesis")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/Fastned/kenesis.git")
            developerConnection.set("scm:git:ssh://github.com/js-fastned/kenesis.git")
            url.set("https://github.com/Fastned/kenesis")
        }
    }
}
