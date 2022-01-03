import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("publish")
    id("com.github.ben-manes.versions")
}

java{
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
    implementation(Dependencies.Moshi.moshi)
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.truth)
}