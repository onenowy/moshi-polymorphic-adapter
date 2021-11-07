object Dependencies {

    object MoshiPolymorphicAdapter {
        const val version = "0.1.0-SNAPSHOT"
        const val group = "dev.onenowy.moshipolymorphicadapter"
        const val moshi_polymorphic_adapter = "$group:moshi-polymorphic-adapter:$version"
        const val reflect = "$group:kotlin-sealed-reflect:$version"
        const val codegen = "$group:kotlin-sealed-codegen:$version"
    }

    object Kotlin {
        private const val version = "1.5.31"
        const val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
    }

    object Test {
        private const val junit_version = "4.13.2"
        const val junit = "junit:junit:$junit_version"
        private const val truth_version = "1.1.3"
        const val truth = "com.google.truth:truth:$truth_version"
    }

    object Moshi {
        private const val version = "1.12.0"
        const val moshi = "com.squareup.moshi:moshi:$version"
        const val codegen = "com.squareup.moshi:moshi-kotlin-codegen:$version"
    }

    object AutoService {
        private const val version = "1.0"
        const val processor = "com.google.auto.service:auto-service:$version"
        const val annotations = "com.google.auto.service:auto-service-annotations:$version"
    }

    object Incap {
        private const val version = "0.3"
        const val annotations = "net.ltgt.gradle.incap:incap:$version"
        const val processor = "net.ltgt.gradle.incap:incap-processor:$version"
    }

    object KotlinPoet {
        private const val version = "1.10.2"
        const val kotlinpoet = "com.squareup:kotlinpoet:$version"
        const val metadata = "com.squareup:kotlinpoet-metadata:$version"
        const val ksp = "com.squareup:kotlinpoet-ksp:$version"
    }

    object Shadow {
        const val version = "7.0.0"
        const val shadow = "com.github.johnrengelman.shadow"
    }

    object VersionPlugin {
        const val version = "0.39.0"
        const val versionPlugin = "com.github.ben-manes.versions"
    }

    object KSP {
        private const val version = "1.5.31-1.0.0"
        const val ksp = "com.google.devtools.ksp:symbol-processing-api:$version"
    }

    object KotlinCompileTest {
        private const val version = "1.4.5"
        const val test = "com.github.tschuchortdev:kotlin-compile-testing:$version"
    }

    object GradleMavenPublish {
        private const val version = "0.18.0"
        const val plugin = "com.vanniktech:gradle-maven-publish-plugin:$version"
    }
}