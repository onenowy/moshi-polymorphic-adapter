import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply{
    plugin(libs.plugins.kotlinPowerAssert.get().pluginId)
}

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("publish")
    id("dependencies-versions")
    id("common-settings")
}

dependencies {
    implementation(libs.moshi.moshi)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.truth)
}