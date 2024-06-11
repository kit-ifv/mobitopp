plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":annotations"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.5.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.6.20-1.0.5")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("reflect"))
    implementation(kotlin("reflect"))
}

sourceSets {
    main {
        java.srcDirs("src/main/kotlin")
    }
    test {
        java.srcDirs("src/test/kotlin")
    }
}

// Apply KSP plugin for both main and test source sets
ksp {
    // Configure any arguments if necessary
    arg("optionName", "optionValue")
}

