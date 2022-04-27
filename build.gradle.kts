task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}