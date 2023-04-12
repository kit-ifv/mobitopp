plugins {
    kotlin("jvm") version "1.8.0"
    jacoco
    id("org.barfuin.gradle.jacocolog") version "1.2.4"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
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


kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}