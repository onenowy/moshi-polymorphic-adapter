import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
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
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
    google()
    maven(url = "https://maven-central-asia.storage-download.googleapis.com/maven2/")
    mavenCentral()
}

dependencies {
    testImplementation(Dependencies.MoshiPolymorphicAdapter.moshi_polymorphic_adapter)
    kaptTest(Dependencies.Moshi.codegen)
    testImplementation(Dependencies.Moshi.moshi)
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.truth)
}