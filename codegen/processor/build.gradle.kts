plugins {
    kotlin("jvm") version "1.9.10"
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
    id("maven-publish")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":annotations"))
    testImplementation(project(":annotations"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.5.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.6.20-1.0.5")
    implementation("com.squareup:kotlinpoet:1.16.0")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("reflect"))
    implementation(kotlin("reflect"))
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

