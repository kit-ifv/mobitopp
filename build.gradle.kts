import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    alias(libs.plugins.ksp) // id("com.google.devtools.ksp") version "2.0.10-1.0.24" //
    alias(libs.plugins.kotlin.jvm) // kotlin("jvm") version "2.0.10" //
    alias(libs.plugins.kover) // id("org.jetbrains.kotlinx.kover") version "0.9.1" //
    alias(libs.plugins.detekt) // id("io.gitlab.arturbosch.detekt") version "1.23.7" //
    alias(libs.plugins.kotlin.serialization) // kotlin("plugin.serialization") version "2.0.10" //
    application
    id("maven-publish")
}
/**
 * Projects that appear in
 */
allprojects {
    repositories {
        maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.osgeo.org/repository/release") }
    maven { url = uri("https://repo.matsim.org/repository/matsim") }

    //
    //
    //

//    mavenLocal()

}

detekt {
    version = libs.versions.detekt.get() // "1.23.7"
    buildUponDefaultConfig = true
    config.setFrom("$projectDir/detekt-config.yml")
    autoCorrect = true
}

dependencies {

    detektPlugins(project(":custom-detekt"))

    //ifv libs
    api(libs.ifv.units) //"edu.kit.ifv.mobitopp:kotlin-units:1.1.6")
    api(libs.ifv.visum.netparser) //"edu.kit.ifv:visumNetfileParser:0.9.13")
    api(libs.ifv.discrete.choice) //"edu.kit.ifv.mobitopp:discrete-choice:1.0.0
    api(libs.ifv.actitoppNG)



    //testing libs
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.params) //5.11.4

    // annotation processing libs
    implementation(project(":annotations"))
    testImplementation(project(":annotations"))
    ksp(project(":processor")) // to make KSP work
    api(project(":processor")) // to make KSP work
    implementation(project(":annotations"))
    testImplementation(libs.kotlin.compile.testing.ksp) //1.5.0

    //detekt libs
    detekt(libs.detekt.formatting) // 1.23.7
    detekt(libs.detekt.cli) // 1.23.7

    //kandy libs 0.8.0
    implementation(libs.kandy.lets.plot) //
    runtimeOnly(libs.kandy.util)//
    implementation(libs.kandy.api) //

    //other kotlinx libs
    implementation(libs.kotlin.reflect) // ??
    implementation(libs.kotlin.statistics) //0.2.1
    implementation(libs.kotlinx.coroutines) //1.10.1
    implementation(libs.kotlinx.serialization.core) //1.8.0
    implementation(libs.kotlinx.serialization.cbor) //1.8.0
    implementation(libs.kotlinx.html) //0.12.0

    // other libs
    implementation(libs.snakeyaml) // SnakeYAML dependency, 2.2
    implementation(libs.commons.compress) //1.26.2
    implementation(libs.xz) //1.9
    implementation(libs.progressbar) //0.10.1
    implementation(libs.exp4j) //0.4.8
}

tasks.test {
    useJUnitPlatform()
}


tasks.withType<Detekt>().configureEach {
    reports {
        // observe findings in your browser with structure and code snippets
        html.required.set(true)
        // checkstyle like format mainly for integrations like Jenkins
        xml.required.set(true)
        // similar to the console output, contains issue signature to manually edit baseline files
        txt.required.set(true)
        // standardized SARIF format (https://sarifweb.azurewebsites.net/) for integrations with GitHub Code Scanning
        sarif.required.set(true)
        // simple Markdown format
        md.required.set(true)
    }
}
tasks.withType<Detekt>().configureEach {
    jvmTarget = "1.8"
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("MainKt")
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<JavaExec>().configureEach {
    maxHeapSize = "60G"
    jvmArgs = listOf(
        "-XX:+HeapDumpOnOutOfMemoryError",        // Enable heap dump on OutOfMemoryError
        "-XX:HeapDumpPath=./heapdumps",           // Specify the directory for heap dumps
        "-Xmx60G"                                 // Example: Set max heap size to 60G
    )
}

allprojects {
    apply(plugin = "maven-publish")

    project.group = "edu.kit.ifv.mobitopp"

    afterEvaluate {

        if (checkProperty("doPublish")) {
            /* mobiTopp publishing process (see .gitlab-ci.yml)
             * Parameters such as "doPublish" must be passed in gradle command:
             *  - ./gradlew <TASKS> publish -PdoPublish=true -Pparam=value...
             * Lookup of parameters doPublish and isRelease returns true if they are specified and their value reads "true".
             * Other required parameters must be specified, otherwise an error is thrown.
             *
             * The pipeline build version is used as the published artifacts version string.
             *  - uses parameter: "buildVersion"
             *
             * Every merge on main is published to local repo: see deploy-job
             *  - checks: doPublish=true, isRelease=false
             *  - requires parameters: "localUrl", "localRepoUser" and "localRepoPassword"
             *
             * Public releases must be published manually:
             *  - checks: doPublish=true, isRelease=true
             *  - requires parameters: "publicUrl", "publicRepoUser" and "publicRepoPassword"
             */

            project.version = requireProperty("buildVersion")
            println("Setup publishing configuration for ${group}:${project.name}:${version}.")

            publishing {

                publications {
                    register("mavenData", MavenPublication::class) {
                        from(components["kotlin"]) // For Kotlin projects
                        groupId = group.toString()
                        artifactId = project.name
                        version = project.version.toString()
                    }
                }

                repositories {
                    if (checkProperty("isRelease")) {
                        println("Activate: publish public release!")
                        println("WARNING: Public release still deactivated!")

                        //  Keep for first public release of reengineered mobitopp
                        //maven {
                        //    name = "PublicRepo"
                        //    url = uri(requireProperty("publicUrl"))
                        //    credentials {
                        //        username = requireProperty("publicRepoUser")
                        //        password = requireProperty("publicRepoPassword")
                        //    }
                        //}

                    } else {
                        println("Activate: publish local build!")
                        maven {
                            name = "LocalRepo"
                            url = uri(requireProperty("localUrl"))
                            credentials {
                                username = requireProperty("localRepoUser")
                                password = requireProperty("localRepoPassword")
                            }
                        }
                    }
                }

            }

        }

    }

}

fun requireProperty(property: String, orElse: String? = null): String =
    requireNotNull(project.findProperty(property) as? String ?: orElse) {
        "Could not find property '$property'. Please check the gradle command args. It should contain:\n" +
            "    ./gradlew ... -P$property=<VALUE> ..."
    }

fun checkProperty(property: String): Boolean = project.hasProperty(property) && project.property(property) == "true"
