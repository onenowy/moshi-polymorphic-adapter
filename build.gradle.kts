buildscript {
    repositories {
        google()
        maven(url = "https://maven-central-asia.storage-download.googleapis.com/maven2/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-beta03")
        classpath(Dependencies.Kotlin.plugin)
    }
}

allprojects {
    repositories {
        google()
        maven(url = "https://maven-central-asia.storage-download.googleapis.com/maven2/")
    }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}