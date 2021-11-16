plugins {
    `kotlin-dsl`
}
repositories {
    gradlePluginPortal()
    google()
    maven(url = "https://maven-central-asia.storage-download.googleapis.com/maven2/")
    mavenCentral()
}

allprojects {
    pluginManager.withPlugin("java") {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(11))
            }
        }
        tasks.withType<JavaCompile>().configureEach {
            options.release.set(8)
        }
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
}

object Version  {
    const val kotlin = "1.6.0"
    const val version_plugin = "0.39.0"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}")
    implementation("com.github.ben-manes:gradle-versions-plugin:${Version.version_plugin}")
}