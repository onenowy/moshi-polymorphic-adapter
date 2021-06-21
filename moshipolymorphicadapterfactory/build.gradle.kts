import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm")
    kotlin("kapt")
    id(Dependencies.VersionPlugin.versionPlugin) version Dependencies.VersionPlugin.version
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
}
dependencies {
    implementation(Dependencies.Moshi.moshi)
    kaptTest(Dependencies.Moshi.codegen)
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.truth)
}


