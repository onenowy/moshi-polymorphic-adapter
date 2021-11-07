plugins {
    kotlin("jvm")
    id("publish")
    id(Dependencies.VersionPlugin.versionPlugin) version Dependencies.VersionPlugin.version
}

description = "Generate a PolymorphicAdapter for using reflection"

dependencies {
    implementation(project(":moshi-polymorphic-adapter"))
    implementation(Dependencies.Moshi.moshi)
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.truth)
    implementation(Dependencies.Kotlin.reflect)
}