plugins {
    kotlin("jvm") version "1.8.0"
    jacoco
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
        println(layout)
        println(layout.buildDirectory)

        println(outputs.toString())
    }


}


kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}