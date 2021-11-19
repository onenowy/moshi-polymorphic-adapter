plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.github.ben-manes.versions")
}

java{
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

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