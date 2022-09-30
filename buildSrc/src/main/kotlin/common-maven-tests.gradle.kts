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
    google()
    mavenCentral()
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    testImplementation(libs.moshiPolymorphic.adapter)
    kaptTest(libs.moshi.codegen)
    testImplementation(libs.moshi.moshi)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.truth)
}