plugins {
    kotlin("jvm")
    kotlin("kapt")
    id(Dependencies.VersionPlugin.versionPlugin) version Dependencies.VersionPlugin.version
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf(
            "-Xopt-in=com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview"
        )
    }
}

dependencies {
    compileOnly(project(":moshipolymorphicadapterfactory"))
    compileOnly(Dependencies.KotlinPoet.kotlinpoet)
    compileOnly(Dependencies.KotlinPoet.metadata)
    compileOnly(Dependencies.AutoService.annotations)
    kapt(Dependencies.AutoService.processor)
    compileOnly(Dependencies.Incap.annotations)
    kapt(Dependencies.Incap.processor)
}
