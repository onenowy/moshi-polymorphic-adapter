plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("publish")
    id(Dependencies.VersionPlugin.versionPlugin) version Dependencies.VersionPlugin.version
}

project.description = "Generate a PolymorphicAdapter for Moshi using reflection"

dependencies {
    implementation(project(":moshi-polymorphic-adapter"))
    implementation(Dependencies.Moshi.moshi)
    kaptTest(Dependencies.Moshi.codegen)
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.truth)
    implementation(Dependencies.Kotlin.reflect)
}