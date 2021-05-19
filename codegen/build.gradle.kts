plugins {
    kotlin("jvm")
    kotlin("kapt")
    id(Dependencies.Shadow.shadow) version Dependencies.Shadow.version
    id(Dependencies.VersionPlugin.versionPlugin) version Dependencies.VersionPlugin.version
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf(
            "-Xopt-in=com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview"
        )
    }
}
val shade: Configuration = configurations.maybeCreate("implementationShaded")
configurations.getByName("implementation").extendsFrom(shade)
dependencies {
    implementation(project(":moshipolymorphicadapterfactory"))

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
val shadowJar = tasks.shadowJar.apply {
    configure {
        configurations = listOf(shade)
        archiveClassifier.set("shade")
        transformers.add(com.github.jengelman.gradle.plugins.shadow.transformers.ServiceFileTransformer())
    }
}