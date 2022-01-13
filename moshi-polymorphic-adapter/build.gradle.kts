plugins {
    id("common-with-publish")
}

project.description = "Polymorphic Adapter library for Moshi"

dependencies {
    kaptTest(libs.moshi.codegen)
}
