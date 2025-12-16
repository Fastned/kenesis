import io.gitlab.arturbosch.detekt.getSupportedKotlinVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	jacoco
	`maven-publish`
	`java-library`

	kotlin("jvm") version libs.versions.kotlinVersion

	alias(libs.plugins.dependency.check.plugin)
	alias(libs.plugins.sonarqube.plugin)
	alias(libs.plugins.detekt.plugin)
	alias(libs.plugins.test.logger.plugin)
	alias(libs.plugins.release.plugin)
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

// Used by the CI pipeline to push a git tag based on the project version
tasks.register("printVersion") {
	group = "version"
	description = "Prints the project version"
	doLast {
		println("Version: ${project.version}")
	}
}

release {
	// Required for allowing the Gradle release plugin to work in a GitLab CI job
	failOnUnversionedFiles = false
	git {
		// The Gradle release plugin will push 3 commits on each release, we don't want to trigger pipelines for those
		pushOptions.add("--push-option=ci.skip")
	}
}

publishing {
	repositories {
        maven {
            name = project.name
        }
	}
	publications {
		create<MavenPublication>(project.name) {
			artifactId = project.name
			from(project.components["java"])
		}
	}
}
