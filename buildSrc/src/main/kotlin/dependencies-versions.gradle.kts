import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply{
    plugin(libs.plugins.gradleVersionPlugin.get().pluginId)
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        listOf("ALPHA", "BETA").any { candidate.version.toUpperCase().contains(it) }
    }
}