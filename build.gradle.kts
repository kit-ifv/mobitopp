plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kover)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadowjar)
    application
    id("maven-publish")
    id("signing")
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

allprojects {
    repositories {
        maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")
    }
    // ChatGPT recommends this filter condition to avoid potential future problems with projects that are not kotlin
    plugins.withType<org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper> {
        kotlin {
            jvmToolchain(25)
            compilerOptions {
                freeCompilerArgs.add("-Xcontext-parameters")
            }
        }
    }

}
/**
 * This block tells gradle where to fetch dependencies from.
 */
repositories {
    mavenCentral()
    maven { url = uri("https://repo.osgeo.org/repository/release") }
    maven { url = uri("https://repo.matsim.org/repository/matsim") }
}

detekt {
    version = libs.versions.detekt.get() // "2.0.0-alpha.3"
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
    api(libs.ifv.synthesisAlgorithms)


    //testing libs
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.params) //5.11.4

    // annotation processing libs
    api(project(":annotations"))
    testImplementation(project(":annotations"))
    ksp(project(":processor")) // to make KSP work
    api(project(":processor")) // to make KSP work

    testImplementation(libs.kotlin.compile.testing.ksp) //1.6.0

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
//    implementation(libs.kotlinx.serialization.cbor) //1.8.0
    implementation(libs.kotlinx.html) //0.12.0

    // other libs
    implementation(libs.jackson.parser)
    implementation(libs.jackson.dataformat.csv)
    implementation(libs.jackson.kotlin.serialization)
    implementation(libs.scala.lang)
    implementation(libs.commons.compress) //1.26.2
    implementation(libs.xz) //1.9
    implementation(libs.progressbar) //0.10.1
    implementation(libs.exp4j) //0.4.8

    implementation(libs.fast.util)
    implementation(libs.fast.csv)
}

tasks.test {
    useJUnitPlatform {
        if (System.getenv("CI") != null) {
            excludeTags("plot")
        }
    }
}

tasks {
    shadowJar {
        isZip64 = true
        archiveClassifier.set("all") // produces e.g. myapp-all.jar
        mergeServiceFiles() // optional: handles META-INF/services
    }
}

tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
    reports {
        // observe findings in your browser with structure and code snippets
        html.required.set(true)
//        // checkstyle like format mainly for integrations like Jenkins
//        xml.required.set(true)
//        // similar to the console output, contains issue signature to manually edit baseline files
//        txt.required.set(true)
        // standardized SARIF format (https://sarifweb.azurewebsites.net/) for integrations with GitHub Code Scanning
        sarif.required.set(true)
//        // simple Markdown format
//        md.required.set(true)
    }
}


tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
    jvmTarget = "1.8"
}

tasks.withType<dev.detekt.gradle.DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
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
        "-Xmx60G"
    )

}
// I Disabled the entire schema publishing: Currently it fails due to having no pom config,
//// Apparently using any publish process from src/main makes gradle unhappy. The solution to add a copy of the
//// file to build/* and publish that.
//val prepareSchemaPublication by tasks.registering(Sync::class) {
//    from(layout.projectDirectory.file("src/main/resources/shortterm-config-schema.json"))
//    into(layout.buildDirectory.dir("schema-publication"))
//}
//val schemaFile = prepareSchemaPublication.map {
//    it.destinationDir.resolve("shortterm-config-schema.json")
//}
//
//
//// Add the schema definitions to the publish process, but only the core project needs to do so.
//if (checkProperty("doPublish")) {
//    publishing {
//        publications {
//            create("schema", type = MavenPublication::class) {
//                project.group = "edu.kit.ifv.mobitopp"
//                artifactId = "schemas"
//                version = requireProperty("buildVersion")
//
//                artifact(schemaFile) {
//                    builtBy(prepareSchemaPublication)
//                    extension = "json"
//                }
//            }
//        }
//    }
//}


if (checkProperty("doPublish") && checkProperty("isRelease")) {
    nexusPublishing {
        repositories {
            // see https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/#configuration
            sonatype {
                nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
                snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
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
    apply(plugin = "signing")
    project.group = "edu.kit.ifv.mobitopp"



    afterEvaluate {
        java {
            withJavadocJar()
            withSourcesJar()
        }
        if (checkProperty("doPublish")) {
            /* mobiTopp publishing process (see .gitlab-ci.yml)
                * Parameters such as "doPublish" must be passed in gradle command:
                *  - ./gradlew <TASKS> -PdoPublish=true -Pparam=value...
                * Lookup of parameters doPublish and isRelease returns true if they are specified and their value reads "true".
                * Other required parameters must be specified, otherwise an error is thrown.
                *
                * The pipeline build version is used as the published artifacts version string.
                *  - uses parameter: "buildVersion"
                *
                * Every merge on main is published to local repo: see deploy-job
                *  - checks: doPublish=true, isRelease=false
                *  - gradle task: publish
                *  - requires parameters: "localUrl", "localRepoUser" and "localRepoPassword"
                *
                * Public releases must be published manually:
                *  - checks: doPublish=true, isRelease=true
                *  - gradle tasks: publishToSonatype closeSonatypeStagingRepository
                *  - requires parameters: sonatypeUsername, sonatypePassword signing.keyId signing.password signing.secretKeyRingFile
                */

            project.version = requireProperty("buildVersion")
            println("Setup publishing configuration for ${group}:${project.name}:${version}.")

            publishing {

                val githubURL: String = "github.com/kit-ifv/mobitopp"
                val projectDescription: String = "A travel demand simulation framework"

                publications {

                    create<MavenPublication>("mavenData") {
                        from(components["java"])
                        groupId = group.toString()
                        artifactId = project.name
                        version = project.version.toString()

                        pom {
                            name.set(project.name)
                            description.set(projectDescription)
                            url.set("https://$githubURL")

                            licenses {
                                license {
                                    name.set("MIT License")
                                    url.set("https://mit-license.org")
                                }
                            }

                            developers {
                                developer {
                                    id.set("Jelle Kübler")
                                    name.set("Jelle Kübler")
                                    email.set("jelle.kuebler@kit.edu")
                                }
                                developer {
                                    id.set("Robin Andre")
                                    name.set("Robin Andre")
                                    email.set("robin.andre@kit.edu")
                                }
                            }

                            scm {
                                connection.set("scm:git:git:https://$githubURL.git")
                                developerConnection.set("scm:git:ssh://git@$githubURL.git")
                                url.set("https://$githubURL")
                            }
                        }
                    }

                }


                repositories {
                    if (checkProperty("isRelease")) {
                        println("Activate: publish public release!")
                        signing {
                            sign(publishing.publications)
                        }

//                        nexusPublishing {
//                            repositories {
//                                // see https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/#configuration
//                                sonatype {
//                                    nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
//                                    snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
//                                }
//                            }
//                        }

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
