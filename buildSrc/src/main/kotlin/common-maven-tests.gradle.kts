plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.github.ben-manes.versions")
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