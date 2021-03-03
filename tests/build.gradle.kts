plugins {
    kotlin("jvm")
    kotlin("kapt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
dependencies {
    testImplementation(project(":moshipolymorphicadapterfactory"))
    testImplementation(project(":codegen"))
    kaptTest(project(":codegen"))
    kaptTest(Dependencies.Moshi.codegen)
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.truth)
    testImplementation(Dependencies.Moshi.moshi)
}