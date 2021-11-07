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
version = "0.1.0-SNAPSHOT"

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = project.path.replaceFirst(":", "").replace(":", "-")
            pom {
                name.set("$groupId:$")
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
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = findProperty("ossrhUsername") as? String
                password = findProperty("ossrhPassword") as? String
            }
        }
    }
}

signing {
    isRequired = !version.toString().endsWith("SNAPSHOT")
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["maven"])
}