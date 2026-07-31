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
- [ ] Verify deployment using a sample test project
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
- [ ] Verify deployment using a sample test project
- [ ] Publish deployment
- [ ] Prepare docs for upload `/gradlew :documentation:prepareDocsForUpload`
- [ ] Upload docs `./gradlew gitPublishPush` (docs are generated/prepared by the `build` task earlier)
- [ ] Change `version` in `gradle.properties` in `master` to new development versions and commit with message *Back to snapshots*
- [ ] Push `master` and push the tag `git push origin vx.x.x`
