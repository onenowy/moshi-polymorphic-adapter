plugins {
    id("common-maven-tests")
}

dependencies {
    testImplementation(libs.moshi.reflect)
    testImplementation(libs.moshiPolymorphic.reflect)
}