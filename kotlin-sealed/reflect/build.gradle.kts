plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("publish")
    id(Dependencies.VersionPlugin.versionPlugin) version Dependencies.VersionPlugin.version
}

project.description = "Generate Moshi Polymorphic Adapters for Kotlin sealed class using reflection"

dependencies {
    implementation(project(":moshi-polymorphic-adapter"))
    implementation(Dependencies.Moshi.moshi)
    kaptTest(Dependencies.Moshi.codegen)
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.truth)
    implementation(Dependencies.Kotlin.reflect)
}