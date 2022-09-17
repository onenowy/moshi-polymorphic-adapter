plugins {
    id("common-with-publish")
}
project.description = "Generate Moshi Polymorphic Adapters for Kotlin sealed class using annotation processor"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-opt-in=com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview",
            "-opt-in=kotlin.RequiresOptIn"
        )
    }
}

dependencies {
    implementation(project(":moshi-polymorphic-adapter"))
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinPoet.kotlinPoet)
    implementation(libs.kotlinPoet.metadata)
    implementation(libs.autoService.annotation)
    kapt(libs.autoService.processor)
    implementation(libs.incap.annotation)
    kapt(libs.incap.processor)
    testImplementation(libs.kotlinComplieTest)
}