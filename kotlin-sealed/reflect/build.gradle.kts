plugins {
    id("common-with-publish")
}

project.description = "Generate Moshi Polymorphic Adapters for Kotlin sealed class using reflection"

dependencies {
    implementation(project(":moshi-polymorphic-adapter"))
    testImplementation(libs.moshi.reflect)
    kaptTest(libs.moshi.codegen)
    implementation(libs.kotlin.reflect)
}