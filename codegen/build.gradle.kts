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
        freeCompilerArgs += listOf(
            "-Xopt-in=com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview"
        )
    }
}
val shade: Configuration = configurations.maybeCreate("compileShaded")
configurations.getByName("compileOnly").extendsFrom(shade)
dependencies {
    api(project(":moshipolymorphicadapterfactory"))

    api(Dependencies.KotlinPoet.kotlinpoet)
    shade(Dependencies.KotlinPoet.metadata) {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "com.squareup", module = "kotlinpoet")
    }
    shade(Dependencies.KotlinPoet.metadataSpecs) {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "com.squareup", module = "kotlinpoet")
    }

    api(Dependencies.AutoService.annotations)
    kapt(Dependencies.AutoService.processor)
    api(Dependencies.Incap.annotations)
    kapt(Dependencies.Incap.processor)
}
val relocateShadowJar = tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation>("relocateShadowJar") {
    target = tasks.shadowJar.get()
}
val shadowJar = tasks.shadowJar.apply {
    configure {
        dependsOn(relocateShadowJar)
        archiveClassifier.set("shade")
        configurations = listOf(shade)
        transformers.add(com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer())
    }
}