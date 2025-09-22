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

allprojects {
    repositories {
        maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")
    }
}
/**
 * This block tells gradle where to fetch dependencies from. We require
 */
repositories {
    mavenCentral()
    maven { url = uri("https://repo.osgeo.org/repository/release") }
    maven { url = uri("https://repo.matsim.org/repository/matsim") }

    maven { url = uri("https://nexus.ifv.kit.edu/repository/maven-releases/") }
    maven { url = uri("https://nexus.ifv.kit.edu/repository/maven-central/") }
    maven { url = uri("https://nexus.ifv.kit.edu/repository/maven-snapshots/") }

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
    api(libs.ifv.units)
    api(libs.ifv.visum.netparser)
    api(libs.ifv.discrete.choice)
    api(libs.ifv.actitoppNG)



    //testing libs
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.params) //5.11.4

    // annotation processing libs
    api(project(":annotations"))
    testImplementation(project(":annotations"))
    ksp(project(":processor")) // to make KSP work
    api(project(":processor")) // to make KSP work

    testImplementation(libs.kotlin.compile.testing.ksp) //1.5.0

    //detekt libs
    detekt(libs.detekt.formatting)
    detekt(libs.detekt.cli)

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
    implementation(libs.jackson.parser)
    implementation(libs.jackson.dataformat.csv)
    implementation(libs.jackson.kotlin.serialization)
    implementation(libs.commons.compress) //1.26.2
    implementation(libs.xz) //1.9
    implementation(libs.progressbar) //0.10.1
    implementation(libs.exp4j) //0.4.8

    implementation(libs.fast.util)


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
    exclude(
        "**/BuildAgents.kt",
        "**/FixedModesFilter.kt",
        "**/ModeAvailabilityFilter.kt",
        "**/PersonEvents.kt",
        "**/OverridableDestinationChoiceModel.kt",
        "**/OverridableModeChoiceModel.kt",
        "**/LoadBehaviorModelsStep.kt",
        "**/WriteTripsToCsvStep.kt",
    )
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
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
// Add the schema definitions to the publish process, but only the core project needs to do so.
if(checkProperty("doPublish")) {
    publishing {
        publications {
            create("schema", type = MavenPublication::class) {
                project.group = "edu.kit.ifv.mobitopp"
                artifactId = "schemas"
                version = requireProperty("buildVersion")

                artifact("src/main/resources/shortterm-config-schema.json") {
                    classifier = ""
                    extension = "json"
                }
            }
        }
    }
}


/**
 * Configures this project and each of its sub-projects.
 *
 * This method executes the given Action against this project and each of its sub-projects.
 */
allprojects {
    /**
     * Applies the plugin with the given ID. Does nothing if the plugin has already been applied.
     */
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
