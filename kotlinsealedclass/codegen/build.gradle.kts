plugins {
    kotlin("jvm")
    kotlin("kapt")
    id(Dependencies.Shadow.shadow) version Dependencies.Shadow.version
    id(Dependencies.VersionPlugin.versionPlugin) version Dependencies.VersionPlugin.version
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-Xopt-in=com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview"
        )
    }
}
val shade: Configuration = configurations.maybeCreate("compileShaded")
configurations.getByName("implementation").extendsFrom(shade)
dependencies {
    compileOnly(project(":moshipolymorphicadapter"))

    shade(Dependencies.Metadata.metadata) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    }
    implementation(Dependencies.KotlinPoet.kotlinpoet)
    shade(Dependencies.KotlinPoet.metadata) {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "com.squareup", module = "kotlinpoet")
    }
    compileOnly(Dependencies.AutoService.annotations)
    kapt(Dependencies.AutoService.processor)
    compileOnly(Dependencies.Incap.annotations)
    kapt(Dependencies.Incap.processor)
}
val relocateShadowJar = tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation>("relocateShadowJar") {
    target = tasks.shadowJar.get()
}

val codegenShadowJar = tasks.shadowJar.apply {
    configure {
        dependsOn(relocateShadowJar)
        configurations = listOf(shade)
        archiveClassifier.set("shade")
        relocate(
            "com.squareup.kotlinpoet.metadata",
            "com.onenowy.moshipolymorphicadapter.kotlinpoet.metadata"
        )
        relocate("kotlinx.metadata", "com.onenowy.moshipolymorphicadapter.kotlinx.metadata")
        transformers.add(com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer())
    }
}
