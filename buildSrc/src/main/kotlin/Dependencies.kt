object Dependencies {

    object Kotlin {
        const val version = "1.4.30"
        const val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
    }

    object Test {
        private const val junit_version = "4.13.2"
        const val junit = "junit:junit:$junit_version"
        private const val truth_version = "1.1.2"
        const val truth = "com.google.truth:truth:$truth_version"
    }

    object Moshi {
        private const val version = "1.11.0"
        const val moshi = "com.squareup.moshi:moshi:$version"
        const val codegen = "com.squareup.moshi:moshi-kotlin-codegen:$version"
        const val adapter = "com.squareup.moshi:moshi-adapters:$version"
    }

    object Autoservice {
        private const val version = "1.0-rc7"
        const val processor = "com.google.auto.service:auto-service:$version"
        const val annotations = "com.google.auto.service:auto-service-annotations:$version"
    }

    object Incap {
        private const val version = "0.3"
        const val annotations = "net.ltgt.gradle.incap:incap:$version"
        const val processor = "net.ltgt.gradle.incap:incap-processor:$version"
    }

    const val gradle_version = "4.1.2"

    object Kotlinpoet {
        private const val version = "1.7.2"
        const val kotlinpoet = "com.squareup:kotlinpoet:$version"
        const val metadata = "com.squareup:kotlinpoet-metadata-specs:$version"
        const val metadataSpecs = "com.squareup:kotlinpoet-metadata-specs:$version"
        const val elementsClassInspector = "com.squareup:kotlinpoet-classinspector-elements:$version"
    }
}