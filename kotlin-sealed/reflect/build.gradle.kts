plugins {
    kotlin("jvm")
    kotlin("kapt")
    id(Dependencies.VersionPlugin.versionPlugin) version Dependencies.VersionPlugin.version
}

dependencies {
    implementation(project(":moshi-polymorphic-adapter"))
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.truth)
    kaptTest(Dependencies.Moshi.codegen)
    implementation(Dependencies.Kotlin.reflect)
}