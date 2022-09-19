import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply{
    plugin(libs.plugins.kotlinPowerAssert.get().pluginId)
}
plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("dependencies-versions")
    id("common-settings")
}

repositories {
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
    google()
    maven(url = "https://maven-central-asia.storage-download.googleapis.com/maven2/")
    mavenCentral()
}

dependencies {
    testImplementation(libs.moshiPolymorphic.adapter)
    kaptTest(libs.moshi.codegen)
    testImplementation(libs.moshi.moshi)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.truth)
}