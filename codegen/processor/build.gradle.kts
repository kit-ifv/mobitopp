plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":annotations"))
    testImplementation(project(":annotations"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.5.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.6.20-1.0.5")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("reflect"))
    implementation(kotlin("reflect"))
}

