plugins {
    kotlin("jvm")
    kotlin("kapt")
    id(Dependencies.VersionPlugin.versionPlugin) version Dependencies.VersionPlugin.version
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-Xopt-in=com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview",
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }
}

dependencies {
    implementation(project(":moshipolymorphicadapter"))
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