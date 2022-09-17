plugins {
    `java-library`
    `maven-publish`
    signing
}

java {
    withJavadocJar()
    withSourcesJar()
}

group = "dev.onenowy.moshipolymorphicadapter"
version = libs.versions.moshiPolymorphicAdapter.get()
val isRelease = !version.toString().endsWith("SNAPSHOT")

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = project.path.replaceFirst(":", "").replace(":", "-")
            afterEvaluate {
                pom {
                    name.set("$groupId:$artifactId")
                    description.set(project.description)
                    url.set("https://github.com/onenowy/moshi-polymorphic-adapter")
                    licenses {
                        license {
                            name.set("Apache 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("onenowy")
                            name.set("sunghwan")
                            email.set("onenowy@gmail.com")
                        }
                    }
                    scm {
                        url.set("https://github.com/onenowy/moshi-polymorphic-adapter")
                        connection.set("https://github.com/onenowy/moshi-polymorphic-adapter.git")
                        developerConnection.set("scm:git@github.com:onenowy/moshi-polymorphic-adapter.git")
                    }
                }
            }
        }
        repositories {
            maven {
                name = "sonatype"
                val releaseUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                val snapshotUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                url = uri(if (isRelease) releaseUrl else snapshotUrl)
                credentials {
                    username = findProperty("ossrhUsername") as? String
                    password = findProperty("ossrhPassword") as? String
                }
            }
        }
    }
}

signing {
    isRequired = isRelease
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["maven"])
}