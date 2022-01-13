import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("publish")
    id("dependencies-versions")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile>{
    kotlinOptions {
        jvmTarget = "11"
    }
}

repositories {
    google()
    maven(url = "https://maven-central-asia.storage-download.googleapis.com/maven2/")
    mavenCentral()
}

dependencies {
    implementation(libs.moshi.moshi)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.truth)
}