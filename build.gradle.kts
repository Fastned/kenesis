import io.gitlab.arturbosch.detekt.getSupportedKotlinVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	jacoco
	`java-library`
	kotlin("jvm")

	id("org.owasp.dependencycheck")
	id("org.sonarqube")
	id("io.gitlab.arturbosch.detekt")
	id("com.adarshr.test-logger")
	id("com.vanniktech.maven.publish")
	id("com.dipien.semantic-version")
}

group = "io.github.fastned"

repositories {
	mavenCentral()
	mavenLocal()
}

val detektVersion: String by project
val kotlinLogging: String by project
val slf4jApiVersion: String by project
val classGraphVersion: String by project
val semanticReleasePluginVersion: String by project

buildscript {
	dependencies {
		classpath("com.dipien:semantic-version-gradle-plugin:2.0.0")
	}
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")

    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLogging")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.slf4j:slf4j-api:$slf4jApiVersion")
    implementation("io.github.classgraph:classgraph:$classGraphVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.slf4j:slf4j-simple:$slf4jApiVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

val jacoco: String by project

jacoco {
    toolVersion = jacoco
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

	coordinates(group.toString(), "kenesis", version.toString())

	pom {
		name = "Kenesis"
		description = "Test library to provide mocked values to use in different tests."
		inceptionYear = "2025"
		url = "https://github.com/Fastned/kenesis"
		licenses {
			license {
				name = "The Apache License, Version 2.0"
				url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
				distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
			}
		}
		developers {
			developer {
				id = "kotlin-fastned"
				name = "Fastned"
				url = "https://github.com/Fastned"
				email = "dev@fastned.nl"
				organization = "Fastned"
				organizationUrl = "https://fastned.nl"
			}
		}
		scm {
			url = "https://github.com/Fastned/kenesis"
			connection = "scm:git:git://github.com/Fastned/kenesis"
			developerConnection = "scm:git:ssh://git@github.com/Fastned/kenesis.git"
		}
	}
}
