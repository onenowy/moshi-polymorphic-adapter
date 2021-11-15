plugins {
    id("common-with-publish")
}

project.description = "Generate Moshi Polymorphic Adapters for Kotlin sealed class using reflection"

dependencies {
    implementation(project(":moshi-polymorphic-adapter"))
    kaptTest(Dependencies.Moshi.codegen)
    implementation(Dependencies.Kotlin.reflect)
}