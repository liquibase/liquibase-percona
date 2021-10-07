# Release Workflow

The release automation is designed to quickly release updates to liquibase extensions. This routinely happens
when there is a new release of liquibase core.

The release process is semi-automated so that you still have control over e.g. the release notes and
when the release is happening and you have the ability to merge other PRs as well before the release.

The update of liquibase core is triggered by a pull request by dependabot. This PR is created automatically
and signals: liquibase core is generally available via maven central. You need to manually merge
this PR after you've reviewed the changes and test results.

After that, you can immediately trigger manually the workflow "prepare-release". You could start
this workflow also later when it is more convenient for you and you have time to fix potential issues.

The workflow "prepare-release" will create a draft release on github. By publishing this draft release,
the new extension version is made available via maven central.

## Triggers

### Pull Request Opened

Dependabot checks daily whether there are new updates for your dependencies. One of these
dependencies is `org.liquibase:liquibase-core`. Once a PR for this dependency arrives, you know: It's
time for a new extension release.

This pull request executes the usual build which includes integration tests.
You should verify, that all tests pass before merging the PR.

### Manual workflow "prepare-release"

Once you are satisfied with what's in the release, you can trigger manually the workflow "prepare-release"
via the github web ui: <https://github.com/liquibase/liquibase-percona/actions/workflows/prepare-release.yml>.

This workflow will prepare a release from branch `main` and can be executed at any time. Updates to
liquibase-core or any other release you want to publish for your extension.

You can specify an extensionVersion - that is the "to-be-released" version of your extension. You can leave it
empty, if you create a new release to support a new liquibase-core version. Then the extension will have the
same version as liquibase-core (e.g. 4.5.0). If you create an patch version for your extension, then you
can enter here for example 4.5.0.1.

This workflow will perform the following steps:

*   Updates the version to the extension version in the `pom.xml` including the property
    `project.build.outputTimestamp` (in order to support reproducible builds)
*   Creates a new tag
*   Updates the version to the next SNAPSHOT development version
*   Pushes these changes to the main branch
*   Runs the integration tests
*   Creates a draft release on github

The push uses the automatically generated `GITHUB_TOKEN` provided by [Github Actions](https://docs.github.com/en/actions/security-guides/automatic-token-authentication).
So there is no need for an additional token.

All changes for workflow always happen on the branch `main`. This means, releases can currently only
be created from `main`. If there is a patch release needed for an older version, then this release
needs to be done completely manually.

You can now download the extension jar from the draft release and do local testing.
Also don't forget to review the release notes.

If you are satisfied with the release, you can publish it. This will trigger the next step.

### Draft Release is Published

Once the GitHub release is published, the signed artifact is uploaded to Sonatype Nexus.
The `<autoReleaseAfterClose>true</autoReleaseAfterClose>` option is defined in the POM, so for
all releases without the `SNAPSHOT` suffix, they will automatically release after all
the staging test have passed. If everything goes well, no further manual action is required.

Note: This workflow will actually checkout the sources from the extension version tag and
build the extension (without running tests) again, signs the artifacts and uploads it to Sonatype Nexus.
Hence it's good, when the extension allows for reproducible builds. Otherwise the jar file and the
github release would be different from the jar file in Maven Central.

## Tests

Liquibase Percona has extended integration tests. These integration tests start MySQL and MariaDB docker instances
and run the integration tests from `src/it/*`. The integration tests are only executed, if the profile `run-its`
is activated, e.g. `./mvnw verify -Prun-its`.

By default, these tests run automatically for every build from the main branch.

## Repository Configuration

The automation requires the below secrets and configuration in order to run.

### GPG SECRET
Github secret named: `GPG_SECRET`

According to [the advanced java setup docs for github actions](https://github.com/actions/setup-java/blob/main/docs/advanced-usage.md#gpg) the GPG key should be exported by: `gpg --armor --export-secret-keys YOUR_ID`. From the datical/build-maven:jdk-8 docker container, this can be export by the following:

```bash
$ docker run -it -u root docker.artifactory.datical.net/datical/build-maven:jdk-8 bash

$  gpg -k
/home/jenkins/.gnupg/pubring.kbx
--------------------------------
pub   rsa2048 2020-02-12 [SC] [expires: 2022-02-11]
      **** OBFUSCATED ID ****
uid           [ultimate] Liquibase <support@liquibase.org>
sub   rsa2048 2020-02-12 [E] [expires: 2022-02-11]

$ gpg --armor --export-secret-keys --pinentry-mode loopback **** OBFUSCATED ID ****
Enter passphrase: *** GPG PASSPHRASE ***
-----BEGIN PGP PRIVATE KEY BLOCK-----
******
******
=XCvo
-----END PGP PRIVATE KEY BLOCK-----
```

### GPG PASSPHRASE
Github secret named: `GPG_PASSPHRASE`
The passphrase is the same one used previously for the manual release and is documented elsewhere for the manual release process.

### SONATYPE USERNAME
Github secret named: `SONATYPE_USERNAME`

The username or token for the sonatype account. Current managed and shared via lastpass for the Shared-DevOps group. 

### SONATYPE TOKEN
Github secret named: `SONATYPE_TOKEN`

The password or token for the sonatype account. Current managed and shared via lastpass for the Shared-DevOps group.

## Useful Links

*   [Github Actions Documentation](https://docs.github.com/en/actions)
*   [Advanced Java Setup for GitHub Actions](https://github.com/actions/setup-java/blob/main/docs/advanced-usage.md#gpg)
*   [Deploying to Sonatype Nexus with Apache Maven](https://central.sonatype.org/publish/publish-maven/)
