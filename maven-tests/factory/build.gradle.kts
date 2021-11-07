plugins {
    kotlin("jvm")
    kotlin("kapt")
    id(Dependencies.VersionPlugin.versionPlugin) version Dependencies.VersionPlugin.version
}

dependencies {
    testImplementation(Dependencies.MoshiPolymorphicAdapter.moshi_polymorphic_adapter)
    kaptTest(Dependencies.Moshi.codegen)
    testImplementation(Dependencies.Moshi.moshi)
    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.truth)
}