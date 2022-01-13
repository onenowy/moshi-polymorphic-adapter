@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `kotlin-dsl`
    alias(libs.plugins.gradleversionplugin)
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
    implementation(libs.gradleversionplugin)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}