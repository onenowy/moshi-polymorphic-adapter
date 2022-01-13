plugins {
    id("common-maven-tests")
}

dependencies {
    kaptTest(libs.moshiPolymorphic.codegen)
}