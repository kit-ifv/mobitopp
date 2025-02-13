plugins {
    kotlin("jvm") version "2.0.10" //
    id("maven-publish")
}

repositories {
    mavenCentral()
}
group = "edu.kit.ifv"
version = "1.0.0"

publishing {
    publications {
        register("mavenData", MavenPublication::class) {
            from(components["kotlin"])
        }
        repositories {
            maven {
                url = uri("https://nexus.ifv.kit.edu/repository/maven-releases/")
                credentials {
                    username = project.findProperty("nexusUsername") as String?
                    password = project.findProperty("nexusPassword") as String?
                }
            }
        }
    }
}