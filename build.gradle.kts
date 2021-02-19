buildscript {
    repositories {
        google()
        maven(url = "https://maven-central-asia.storage-download.googleapis.com/maven2/")
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Dependencies.gradle_version}")
        classpath(Dependencies.Kotlin.plugin)
    }
}

allprojects {
    repositories {
        google()
        maven(url = "https://maven-central-asia.storage-download.googleapis.com/maven2/")
        jcenter()
    }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}