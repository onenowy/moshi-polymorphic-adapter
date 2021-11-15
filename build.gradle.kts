plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
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

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}