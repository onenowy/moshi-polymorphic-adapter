plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("publish")
    id(Dependencies.VersionPlugin.versionPlugin) version Dependencies.VersionPlugin.version
}

description = "Generate a PolymorphicAdapter for using reflection"

dependencies {
    implementation(project(":moshi-polymorphic-adapter"))
    implementation(Dependencies.Moshi.moshi)
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.truth)
    kaptTest(Dependencies.Moshi.codegen)
    implementation(Dependencies.Kotlin.reflect)
}