@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `kotlin-dsl`
    alias(libs.plugins.gradleVersionPlugin)
}

repositories {
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
    google()
    mavenCentral()
    gradlePluginPortal()
}

kotlinDslPluginOptions{
    jvmTarget.set("17")
}

dependencies {
    implementation(libs.kotlin.gradle)
    implementation(libs.gradleVersionPlugin)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.kotlinPowerAssert)
}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
    rejectVersionIf {
        listOf("ALPHA", "BETA").any { candidate.version.toUpperCase().contains(it) }
    }
}