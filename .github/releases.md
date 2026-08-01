## Preparations

- [ ] Ensure `mavenCentralUsername` property set in Gradle user properties
- [ ] Ensure `mavenCentralPassword` property set in Gradle user properties

### Snapshots

Snapshots are published automatically for every commit to the `master` branch. See the [build](https://github.com/ciscoo/cxf-codegen-gradle/blob/master/.github/workflows/build.yml) workflow for more details.

### Release Candidate

- [ ] Change `version` in `gradle.properties` to the version to release
- [ ] Change release date in changelog
- [ ] Change version in readme
- [ ] Commit with message *Release vx.x.x*
- [ ] Tag current commit `git tag vx.x.x`
- [ ] Execute `./gradlew clean build`
- [ ] Execute `./gradlew publishAggregationToCentralPortal`
- [ ] Find deployment info at https://central.sonatype.com/publishing/deployments
- [ ] [Verify deployment](https://central.sonatype.org/publish/publish-portal-api/#gradle) using a sample test project:
    ```properties
    # gradle.properties
    
    # Central Publishing API can take up to 1min to respond.
    systemProp.org.gradle.internal.http.connectionTimeout=120000
    systemProp.org.gradle.internal.http.socketTimeout=120000
    
    # Bearer <base64 encoded username:pass>
    centralManualTestingAuthHeaderName=Authorization
    centralManualTestingAuthHeaderValue=
    ```

    ```kotlin
    // settings.gradle.kts
    pluginManagement {
        repositories {
            mavenCentral()
            maven {
                name = "centralManualTesting"
                url = uri("https://central.sonatype.com/api/v1/publisher/deployments/download/")
                credentials(HttpHeaderCredentials::class)
                authentication {
                    register<HttpHeaderAuthentication>("header")
                }
            }
            gradlePluginPortal()
        }
    }
    ```
- [ ] Publish deployment
- [ ] Change `version` in `gradle.properties` in `master` to new development versions and commit with message *Back to snapshots*
- [ ] Push `master` and push the tag `git push origin vx.x.x`

### Final Release

- [ ] Change `version` in `gradle.properties` to the version to release
- [ ] Change release date in changelog
- [ ] Change version in readme
- [ ] Commit with message *Release vx.x.x*
- [ ] Tag current commit `git tag vx.x.x`
- [ ] Execute `./gradlew clean build`
- [ ] Execute `./gradlew publishAggregationToCentralPortal`
- [ ] Find deployment info at https://central.sonatype.com/publishing/deployments
- [ ] [Verify deployment](https://central.sonatype.org/publish/publish-portal-api/#gradle) using a sample test project:
    ```properties
    # gradle.properties
    
    # Central Publishing API can take up to 1min to respond.
    systemProp.org.gradle.internal.http.connectionTimeout=120000
    systemProp.org.gradle.internal.http.socketTimeout=120000
    
    # Bearer <base64 encoded username:pass>
    centralManualTestingAuthHeaderName=Authorization
    centralManualTestingAuthHeaderValue=
    ```
    
    ```kotlin
    // settings.gradle.kts
    pluginManagement {
        repositories {
            mavenCentral()
            maven {
                name = "centralManualTesting"
                url = uri("https://central.sonatype.com/api/v1/publisher/deployments/download/")
                credentials(HttpHeaderCredentials::class)
                authentication {
                    register<HttpHeaderAuthentication>("header")
                }
            }
            gradlePluginPortal()
        }
    }
    ```
- [ ] Publish deployment
- [ ] Prepare docs for upload `./gradlew :documentation:prepareDocsForUpload`
- [ ] Upload docs:
    ```shell
    git fetch origin gh-pages
    git worktree add gh-pages gh-pages
    cd gh-pages
    rsync -a -v --delete --ignore-times ../documentation/build/gh-pages/ "./docs/current"
    git add docs/current
    git commit -m "Publish current docs" || echo "nothing to commit"
    git push
    cd ..
    git worktree remove gh-pages
    ```
- [ ] Change `version` in `gradle.properties` in `master` to new development versions and commit with message *Back to snapshots*
- [ ] Push `master` and push the tag `git push origin vx.x.x`
