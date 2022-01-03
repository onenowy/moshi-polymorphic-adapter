object Dependencies {

    object MoshiPolymorphicAdapter {
        const val version = "0.1.2-SNAPSHOT"
        const val group = "dev.onenowy.moshipolymorphicadapter"
        const val moshi_polymorphic_adapter = "$group:moshi-polymorphic-adapter:$version"
        const val reflect = "$group:kotlin-sealed-reflect:$version"
        const val codegen = "$group:kotlin-sealed-codegen:$version"
    }

    object Kotlin {
        private const val version = "1.6.10"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
    }

    object Test {
        private const val junit_version = "4.13.2"
        const val junit = "junit:junit:$junit_version"
        private const val truth_version = "1.1.3"
        const val truth = "com.google.truth:truth:$truth_version"
    }

    object Moshi {
        private const val version = "1.13.0"
        const val moshi = "com.squareup.moshi:moshi:$version"
        const val codegen = "com.squareup.moshi:moshi-kotlin-codegen:$version"
        const val reflect = "com.squareup.moshi:moshi-kotlin:$version"
    }

    object AutoService {
        private const val version = "1.0.1"
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

    object nexusPublish {
        const val version = "1.1.0"
        const val nexusPublish = "io.github.gradle-nexus.publish-plugin"
    }

    object KotlinCompileTest {
        private const val version = "1.4.7"
        const val test = "com.github.tschuchortdev:kotlin-compile-testing:$version"
    }
}