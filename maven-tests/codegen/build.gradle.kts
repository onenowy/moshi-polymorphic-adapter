plugins {
    id("common-maven-tests")
}

dependencies {
    kaptTest(Dependencies.MoshiPolymorphicAdapter.codegen)
}