import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import gradle.kotlin.dsl.accessors._37425236b406cbc1a415ed974b778754.java
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply{
    plugin(libs.plugins.gradleVersionPlugin.get().pluginId)
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        listOf("ALPHA", "BETA").any { candidate.version.toUpperCase().contains(it) }
    }
}