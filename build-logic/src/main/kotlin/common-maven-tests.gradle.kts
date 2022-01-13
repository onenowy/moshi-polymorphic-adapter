import Dependencies_versions_gradle.Libs.libs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
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
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
    google()
    maven(url = "https://maven-central-asia.storage-download.googleapis.com/maven2/")
    mavenCentral()
}

dependencies {
    testImplementation(libs.moshipolymorphic.adapter)
    kaptTest(libs.moshi.codegen)
    testImplementation(libs.moshi.moshi)
    testImplementation(libs.test.junit)
    testImplementation(libs.test.truth)
}