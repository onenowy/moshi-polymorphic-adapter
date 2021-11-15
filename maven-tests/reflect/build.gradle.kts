plugins {
    id("common-maven-tests")
}

dependencies {
    testImplementation(Dependencies.MoshiPolymorphicAdapter.reflect)
}