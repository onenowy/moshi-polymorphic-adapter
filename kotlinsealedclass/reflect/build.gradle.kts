plugins {
    kotlin("jvm")
    kotlin("kapt")
    id(Dependencies.VersionPlugin.versionPlugin) version Dependencies.VersionPlugin.version
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
dependencies {
    compileOnly(project(":moshipolymorphicadapter"))
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.truth)
    kaptTest(Dependencies.Moshi.codegen)
    implementation(Dependencies.Kotlin.reflect)
}