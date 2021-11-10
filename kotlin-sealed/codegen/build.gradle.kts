plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("publish")
    id(Dependencies.VersionPlugin.versionPlugin) version Dependencies.VersionPlugin.version
}
project.description = "Generate Moshi Polymorphic Adapters for Kotlin sealed class using annotation processor"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xopt-in=com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview",
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }
}

dependencies {
    implementation(Dependencies.Moshi.moshi)
    implementation(project(":moshi-polymorphic-adapter"))
    implementation(Dependencies.Kotlin.reflect)
    implementation(Dependencies.KotlinPoet.kotlinpoet)
    implementation(Dependencies.KotlinPoet.metadata)
    implementation(Dependencies.AutoService.annotations)
    kapt(Dependencies.AutoService.processor)
    implementation(Dependencies.Incap.annotations)
    kapt(Dependencies.Incap.processor)
    testImplementation(Dependencies.KotlinCompileTest.test)
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.truth)
}