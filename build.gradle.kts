@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.nexus)
}
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

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}