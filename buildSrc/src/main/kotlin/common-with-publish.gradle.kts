plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("publish")
    id("com.github.ben-manes.versions")
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