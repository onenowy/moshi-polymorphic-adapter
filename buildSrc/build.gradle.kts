plugins {
    `kotlin-dsl`
    id("com.github.ben-manes.versions") version "0.39.0"
}
repositories {
    gradlePluginPortal()
    google()
    maven(url = "https://maven-central-asia.storage-download.googleapis.com/maven2/")
    mavenCentral()
}

object Version {
    const val kotlin = "1.5.31"
    const val version_plugin = "0.39.0"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}")
    implementation("com.github.ben-manes:gradle-versions-plugin:${Version.version_plugin}")
}