plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

group = Dependencies.MoshiPolymorphicAdapter.group
version = Dependencies.MoshiPolymorphicAdapter.version

nexusPublishing {
    repositories {
        create("sonatype") {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(findProperty("ossrhUsername") as? String)
            password.set(findProperty("ossrhPassword") as? String)
        }
    }
}

val initializeSonatypeStagingRepository by tasks.existing
subprojects {
    initializeSonatypeStagingRepository {
        shouldRunAfter(tasks.withType<Sign>())
    }
}


allprojects {
    pluginManager.withPlugin("java") {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(8))
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