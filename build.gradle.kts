plugins {
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.10"
    `maven-publish`
}

group = "de.jackBeBack"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktor_version: String by project

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}