### Overview
The process automates publishing to a **private Nexus repository** on every merge to `main`/`master` and enables manual publishing to **Maven Central (Sonatype)** for official releases. All required secrets are stored as GitLab CI/CD variables.

### CI/CD Variables
| Scope          | Variable              | Description |
|----------------|-----------------------|-------------|
| **Global**     | `GPG_ID`              | GPG key identifier for signing |
|                | `GPG_PASSWORD`        | Password for the GPG key |
|                | `GPG_SECRING`         | Base64‑encoded secret key ring |
|                | `PUBLIC_REPO_PASSWORD` | Sonatype password |
|                | `PUBLIC_REPO_USERNAME` | Sonatype username |
|                | `LOCAL_REPO_PASSWORD` | Private Nexus password |
|                | `LOCAL_REPO_URL`      | Private Nexus URL |
|                | `LOCAL_REPO_USERNAME` | Private Nexus username |
|                | `API_TOKEN` | GitLab personal token for API calls |
|                | `GITLAB_API_URL`      | API endpoint for variable updates |
| **Per‑Project**| `MAJOR`, `MINOR`      | Semantic version components |
|                | `BUILDNUMBER`         | Incremental build counter |
|                | `BUILD_VERSION`       | Full version string (`MAJOR.MINOR.BUILDNUMBER`) |

> Keep all variables **protected** and passwords **masked** in GitLab!

---

### GitLab CI Template
Add the following to `.gitlab-ci.yml`:

```yaml
default:
  image: amazoncoretto:25
#...
deploy-job:
  stage: publish
  only:
    - main
    - master
  needs: [ "test-job" ]
  resource_group: production #only one deploy job at a time
  script:
    - echo "Publish local release $BUILD_VERSION"
    - |
      ./gradlew publish -PbuildVersion=$BUILD_VERSION \
      -PdoPublish=true \
      -PisRelease=false \
      -PlocalUrl=$LOCAL_REPO_URL \
      -PlocalRepoUser=$LOCAL_REPO_USERNAME \
      -PlocalRepoPassword=$LOCAL_REPO_PASSWORD
    - export NEW_BUILD_NUMBER=$((BUILD_NUMBER + 1))
    - echo "Update BUILD_NUMBER to $NEW_BUILD_NUMBER"
    - curl --request PUT --header "PRIVATE-TOKEN:$API_TOKEN" --form "value=$NEW_BUILD_NUMBER" "$GITLAB_API_URL/BUILD_NUMBER"
publish-job:
  allow_failure: false
  when: manual
  stage: publish
  only:
    - main
    - master
  needs: [ "test-job" ]
  resource_group: production #only one deploy job at a time
  script:
    - export NEW_BUILD_VERSION=$MAJOR.$MINOR.0
    - echo "Publish public release $NEW_BUILD_VERSION"^
    - mkdir -p "$CI_PROJECT_DIR/.ci-secrets"
    - printf '%s' "$GPG_SECRING" | base64 -d > "$CI_PROJECT_DIR/.ci-secrets/secring.gpg"
    - echo "New Build Version $NEW_BUILD_VERSION"
    - chmod 600 "$CI_PROJECT_DIR/.ci-secrets/secring.gpg"
    - |
      ./gradlew publishToSonatype closeSonatypeStagingRepository -PbuildVersion=$NEW_BUILD_VERSION \
      -PdoPublish=true \
      -PisRelease=true \
      -PsonatypeUsername=$PUBLIC_REPO_USERNAME \
      -PsonatypePassword=$PUBLIC_REPO_PASSWORD \
      -Psigning.keyId=$GPG_ID \
      -Psigning.password=$GPG_PASSWORD \
      -Psigning.secretKeyRingFile="$CI_PROJECT_DIR/.ci-secrets/secring.gpg"
    - export NEW_MINOR=$((MINOR + 1))
    - echo "Update MINOR to $NEW_MINOR, set BUILD_NUMBER to 0"
    - curl --request PUT --header "PRIVATE-TOKEN:$API_TOKEN" "$GITLAB_API_URL/MINOR" --form "value=$NEW_MINOR"
    - curl --request PUT --header "PRIVATE-TOKEN:$API_TOKEN" "$GITLAB_API_URL/BUILD_NUMBER" --form "value=0"
```

**Key points**

* `deploy-job` runs automatically on every push to `main`/`master`.
* `publish-job` must be triggered manually for a public release.
* Both jobs update the version number CI variables (`BUILD_NUMBER`, `MINOR`) via the GitLab API.
* The `MAJOR` version number CI variable is reserved for manual version upgrades, e.g. after refactorings introducing breaking changes.

---

### Gradle Publishing Configuration
Add the following to `build.gradle.kts` and specify GitHub URL and authors:

```kotlin
plugins {
    //...
    id("maven-publish")
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    id("signing")
    //...
}
group = "edu.kit.ifv.mobitopp"
//...
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
        *  - requires parameters sonatypeUsername, sonatypePassword signing.keyId signing.password signing.secretKeyRingFile
        */
    project.version = requireProperty("buildVersion")
    println("Setup publishing configuration for ${group}:${project.name}:${version}.")
    publishing {
        val githubURL: String = "github.com/kit-ifv/github project url"
        val projectDescription: String = "project description"
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
                            id.set("id")
                            name.set("name")
                            email.set("mail")
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
                nexusPublishing {
                    repositories {
                        // see https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/#configuration
                        sonatype {
                            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
                            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
                        }
                    }
                }
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
fun requireProperty(property: String, orElse: String? = null): String =
    requireNotNull(project.findProperty(property) as? String ?: orElse) {
        "Could not find property '$property'. Please check the gradle command args. It should contain:\n" +
                "    ./gradlew ... -P$property=<VALUE> ..."
    }
fun checkProperty(property: String): Boolean = project.hasProperty(property) && project.property(property) == "true"
```

**How it works**

* `doPublish=true` activates the whole block.
* `isRelease=true` switches the repository configuration from the private Nexus to public Sonatype.
* Required properties are validated via `requireProperty()`; the build fails with an error message if a value is missing.

---

### Release Workflow

1. **Merge to `main`/`master`**
    * CI runs `deploy-job`.
    * Artifacts are published to the private Nexus repository.
    * `BUILD_NUMBER` is incremented automatically.

2. **Create a public release**
    * Manually trigger `publish-job` in GitLab UI.
    * The job builds the next version (`MAJOR.MINOR.0`), signs it, and pushes to Sonatype.
    * After the job finishes, go to the **Sonatype Staging Repository** dashboard and publish.

3. **Post‑release housekeeping**
    * The pipeline bumps `MINOR` (`MINOR+1`) and resets `BUILD_NUMBER` to `0`.
    * Future merges will continue from the new version line.
