import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("com.github.ben-manes.versions")
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        listOf("ALPHA", "BETA").any { candidate.version.toUpperCase().contains(it) }
    }
}

object Libs {
    val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()
}
