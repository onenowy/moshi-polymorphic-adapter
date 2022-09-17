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
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
        google()
        mavenCentral()
    }
}