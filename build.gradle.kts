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

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

detekt {
    version = "1.23.1"
    buildUponDefaultConfig = true
    config.setFrom("$projectDir/detekt-config.yml" )

}
dependencies {
    testImplementation(kotlin("test"))
    implementation(project(":annotations"))
    testImplementation(project(":annotations"))
    ksp(project(":processor")) // to make KSP work
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
    jvmToolchain(12)
}

application {
    mainClass.set("MainKt")
}