buildscript {
    repositories {
        google()
        maven(url = "https://maven-central-asia.storage-download.googleapis.com/maven2/")
    }
    dependencies {
        classpath(Dependencies.Kotlin.plugin)
    }
}

allprojects {
    repositories {
        google()
        maven(url = "https://maven-central-asia.storage-download.googleapis.com/maven2/")
    }
}

subprojects {
    tasks.withType<Test>().configureEach {
        if (project.hasProperty("excludeTest")) {
            exclude(project.findProperty("excludeTest") as String)
        }
    }
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

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}