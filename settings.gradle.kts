@file:Suppress("UnstableApiUsage")

include(":kotlin-sealed:codegen")
include(":kotlin-sealed:reflect")
include(":moshi-polymorphic-adapter")
rootProject.name = "moshi-polymorphic-adapter"
include(":maven-tests:factory")
include(":maven-tests:codegen")
include(":maven-tests:reflect")

dependencyResolutionManagement {
    repositories {
        google()
        maven(url = "https://maven-central-asia.storage-download.googleapis.com/maven2/")
        mavenCentral()
    }
}