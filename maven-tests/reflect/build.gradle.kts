plugins {
    id("common-maven-tests")
}

dependencies {
    testImplementation(Dependencies.Moshi.reflect)
    testImplementation(Dependencies.MoshiPolymorphicAdapter.reflect)
}