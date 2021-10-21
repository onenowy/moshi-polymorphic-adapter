plugins {
    kotlin("jvm")
    kotlin("kapt")
    id(Dependencies.VersionPlugin.versionPlugin) version Dependencies.VersionPlugin.version
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xopt-in=com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview",
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }
}

dependencies {
    implementation(project(":moshi-polymorphic-adapter"))
    implementation(Dependencies.Kotlin.reflect)
    implementation(Dependencies.KotlinPoet.kotlinpoet)
    implementation(Dependencies.KotlinPoet.metadata)
    compileOnly(Dependencies.AutoService.annotations)
    kapt(Dependencies.AutoService.processor)
    compileOnly(Dependencies.Incap.annotations)
    kapt(Dependencies.Incap.processor)
    testImplementation(Dependencies.KotlinCompileTest.test)
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.truth)
}