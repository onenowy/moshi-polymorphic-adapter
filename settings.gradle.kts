include(":kotlin-sealed:codegen")
include(":kotlin-sealed:reflect")
include(":moshi-polymorphic-adapter")
rootProject.name = "moshi-polymorphic-adapter"
include(":maven-tests:factory")
include(":maven-tests:codegen")
include(":maven-tests:reflect")

includeBuild("build-logic")

enableFeaturePreview("VERSION_CATALOGS")
