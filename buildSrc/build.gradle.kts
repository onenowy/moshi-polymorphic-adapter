@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `kotlin-dsl`
    alias(libs.plugins.gradleVersionPlugin)
}

repositories {
    gradlePluginPortal()
    google()
    maven(url = "https://maven-central-asia.storage-download.googleapis.com/maven2/")
    mavenCentral()
}

kotlinDslPluginOptions{
    jvmTarget.set("11")
}

dependencies {
    implementation(libs.kotlin.gradle)
    implementation(libs.gradleVersionPlugin)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    rejectVersionIf {
        listOf("ALPHA", "BETA").any { candidate.version.toUpperCase().contains(it) }
    }
}