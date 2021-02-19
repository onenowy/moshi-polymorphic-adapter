plugins {
    kotlin("jvm")
    kotlin("kapt")
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
dependencies {
    api(project(":moshipolymorphicadapterfactory"))

    api(Dependencies.Kotlinpoet.kotlinpoet)
    api(Dependencies.Kotlinpoet.metadata)
    api(Dependencies.Kotlinpoet.metadataSpecs)
    api(Dependencies.Kotlinpoet.elementsClassInspector)

    api(Dependencies.Autoservice.annotations)
    kapt(Dependencies.Autoservice.processor)
    api(Dependencies.Incap.annotations)
    kapt(Dependencies.Incap.processor)
}
