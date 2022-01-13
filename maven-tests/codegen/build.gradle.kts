plugins {
    id("common-maven-tests")
}

dependencies {
    kaptTest(libs.moshipolymorphic.codegen)
}