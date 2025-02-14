plugins {
    alias(libs.plugins.kotlin.jvm) // "2.0.10" //
    alias(libs.plugins.ksp) // "2.0.10-1.0.24"
    id("maven-publish")
}

group = "edu.kit.ifv"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {

    testImplementation(project(":annotations"))
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlin.compile.testing.ksp) // 1.5.0
    testImplementation(kotlin("reflect"))

    implementation(project(":annotations"))
    implementation(libs.symbol.processing.api) // 2.0.10-1.0.24
    implementation(libs.kotlinpoet) //2.0.0
    implementation(kotlin("reflect"))
}

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
