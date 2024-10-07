import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
    kotlin("jvm") version "1.9.10"
    jacoco
    id("org.barfuin.gradle.jacocolog") version "1.2.4" //This plugin is necessary because gradle eats the console output and gitlab demands to parse the console output for a coverage badge
    id("io.gitlab.arturbosch.detekt") version "1.23.1"
    application
}

group = "edu.kit.ifv"
version = "1.0-SNAPSHOT"

repositories {

    maven { url = uri("https://repo.osgeo.org/repository/release") }
    maven { url = uri("https://repo.matsim.org/repository/matsim") }
    maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")
    //
    //
    //

    mavenCentral()
    mavenLocal()

}

detekt {
    version = "1.23.1"
    buildUponDefaultConfig = true
    config.setFrom("$projectDir/detekt-config.yml")
    autoCorrect = true
}
dependencies {
    api(project(":processor"))
//    testImplementation(project(":processor"))
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.1.0")

    implementation("edu.kit.ifv.mobitopp:kotlin-units:1.1.4")
    implementation(project(":annotations"))
    testImplementation(project(":annotations"))
    ksp(project(":processor")) // to make KSP work
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.5.0")
    detekt("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.1")
    detekt("io.gitlab.arturbosch.detekt:detekt-cli:1.23.1")
    implementation("org.yaml:snakeyaml:2.2") // SnakeYAML dependency
    implementation("org.apache.commons:commons-compress:1.26.2")
    implementation("org.tukaani:xz:1.9")
    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.6.0")
    implementation("org.jetbrains.kotlinx:kotlin-statistics-jvm:0.2.1")
    runtimeOnly("org.jetbrains.kotlinx:kandy-util:0.6.0")
    implementation("org.jetbrains.kotlinx:kandy-api:0.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")


    implementation("me.tongfei:progressbar:0.10.1")
    implementation(kotlin("reflect"))
//    implementation(project(":test-processor"))
//    ksp{project(":test-processor")}

}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)

}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
    }


}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
        txt.required.set(true) // similar to the console output, contains issue signature to manually edit baseline files
        sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with GitHub Code Scanning
        md.required.set(true) // simple Markdown format
    }
}
tasks.withType<Detekt>().configureEach {
    jvmTarget = "1.8"
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}

tasks.withType<JavaExec>().configureEach {
    maxHeapSize = "60G"
    jvmArgs = listOf(
        "-XX:+HeapDumpOnOutOfMemoryError",        // Enable heap dump on OutOfMemoryError
        "-XX:HeapDumpPath=./heapdumps",           // Specify the directory for heap dumps
        "-Xmx60G"                                 // Example: Set max heap size to 6G
    )
}