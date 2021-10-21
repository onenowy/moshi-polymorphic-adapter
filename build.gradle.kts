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