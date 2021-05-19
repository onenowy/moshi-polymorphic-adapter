plugins {
    kotlin("jvm")
    kotlin("kapt")
    id(Dependencies.VersionPlugin.versionPlugin) version Dependencies.VersionPlugin.version
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}
dependencies {
    implementation(Dependencies.Moshi.moshi)
    api(project(":moshipolymorphicadapterfactory"))
    kaptTest(Dependencies.Moshi.codegen)
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.truth)
    api(Dependencies.Kotlin.reflect)
}