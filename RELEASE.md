# Release Workflow

Creating a new release is partly manual, partly automated.
Note that the release workflow of liquibase-percona doesn't follow the workflow of other
extensions, as it simply doesn't work.
See [GitHub Actions, Building, PR, and Releasing Questions #425](https://github.com/liquibase/liquibase-percona/issues/425)
for more detailed information about that. That's why the workflow "release-published.yml" is disabled.

## Step by Step

1. Before a release, make sure, you have merged all open PRs that should be part of the release.
   Dependabot creates usually PRs for updating dependencies. If all goes well, these would be merged
   automatically, but sometimes some manual fixes need to be made, so some PRs might be left open.

2. Especially check, that `liquibase-core` has been updated to the desired version. Usually, for every
   liquibase-core release, the extension is released as well.
   Label such a PR with `notableChanges`, so that it appears separately in the release notes.

3. Create a new branch in the main repository (**not** in your fork). This main repository is considered
   "internal" and pull requests created from branches from there be correctly merged. External PRs don't
   work correctly.

   This new branch is called `chore-prepare-release-x.y.z`

   Update the following files:
   * README.md
     * Update the versions in the code snippets for the maven dependency
     * Add/Update the version in section "Liquibase version(s) tested against", check/update version of percona toolkit
   * CHANGELOG.md
     * Go to <https://github.com/liquibase/liquibase-percona/releases> - the release drafter has created a draft
       release already
     * Copy the draft release into this file
     * Make sure to replace `(#123)` with the corresponding links to PRs/issues
     * In case, liquibase-core was updated, add bullet point "Support for Liquibase x.y.z." directly under "Changes".
     * Fix the "Full Changelog" links
   * docker/README.md
     * Create new version entry
     * for the old version, take the commit id of the last "chore: Prepare release ..." commit
   * docker/Dockerfile
     * Update versions (all versions, if necessary: liquibase-core and percona-toolkit as well...)

   Then push this branch and create a new PR with the title "chore: Prepare release x.y.z".
   Set the correct milestone for this PR - which is usually "next".

   Then update "CHANGELOG.md" once again and manually add this very PR. Push another commit to this PR.

   Finally merge this PR.

4. Create the necessary version updates and tags locally and push manually. "developmentVersion" is the next
   version, and "newVersion" is the version to be released.

   ```
   ./mvnw release:clean release:prepare \
     -Darguments="-Dmaven.javadoc.skip=true -Dmaven.test.skipTests=true -Dmaven.test.skip=true -Dmaven.deploy.skip=true -Dpmd.skip=true -Dcpd.skip=true -Dspotbugs.skip=true" \
     -DdevelopmentVersion=x.y.1-SNAPSHOT \
     -DreleaseVersion=x.y.z \
     -Dtag=vx.y.z \
     -DpushChanges=false
   ```

5. Now you should have a tag "vx.y.z" created and the version in the pom.xml should be the next SNAPSHOT
   version. If this looks correct, you can push:

   ```
   git push origin main
   git push origin tag vx.y.z
   ```

6. Edit the draft release on <https://github.com/liquibase/liquibase-percona/releases>
   * Select the just created tag instead of letting github create a new tag: vx.y.z
   * Change the name of the release to match the tag name: vx.y.z
   * If necessary, add the "Support for Liquibase x.y.z." directly under Changes.
   * Fix the "Full Changelog" links at the end.
   * Don't care about the attached assets, they will be removed with the next step and replaced by
     a new build.
   * Save it as a draft, don't publish yet.

7. Run the action [manual-release-from-tag](https://github.com/liquibase/liquibase-percona/actions/workflows/manual-release-from-tag.yml)
   from branch "main" and enter the tag "vx.y.z". This action will
   * checkout the tag
   * build liquibase-percona
   * attach the jar files to the draft release
   * publish the jar files to maven central. It should eventually be available on
     * https://central.sonatype.com/artifact/org.liquibase.ext/liquibase-percona
     * https://repo1.maven.org/maven2/org/liquibase/ext/liquibase-percona/

8. If the action ran successfully, then you can finally publish the release on github.
  Don't forget to rename the [milestone](https://github.com/liquibase/liquibase-percona/milestones) from next to x.y.z
  and create a new fresh milestone.

9. Once the release is available in maven central, you can build the docker image and publish it:
   ```
   cd docker
   IMAGE=andreasdangel/liquibase-percona
   docker build \
     -t $IMAGE:latest -t $IMAGE:x.y.z -t $IMAGE:x.y \
     .
   docker push $IMAGE:latest
   docker push $IMAGE:x.y.z
   docker push $IMAGE:x.y
   ```

Notes:
* liquibase-percona has reproducible builds, that means, you can rebuild the extension from the same tag
and this should produce the exact same artifacts.
* If anything goes wrong, you can redo the steps 1.-7. as long as the jar files have not been published
to maven central yet. You can remove the tag, push more commits for fixing, create another tag and
run step 7 (manual-release-from-tag) again. However, if the jar files were successfully published into
maven central, there is no way anymore, to change the version. If anything is wrong, you need to
create an entire new release then. And you shouldn't change the tag afterwards (as otherwise, the version
won't be reproducible anymore).

## Repository Configuration

The automation requires the below secrets and configuration in order to run.
The action [manual-release-from-tag](https://github.com/liquibase/liquibase-percona/actions/workflows/manual-release-from-tag.yml)
executes the same commands as the actions from [Liquibase Reusable Workflows](https://github.com/liquibase/build-logic/) do.
The same secrets are reused. This allows to publish to maven central.

### LIQUIBOT_PAT_GPM_ACCESS
Used to access liquibase maven repository on GitHub (https://github.com/orgs/liquibase/packages)


### GPG_SECRET
According to [the advanced java setup docs for github actions](https://github.com/actions/setup-java/blob/main/docs/advanced-usage.md#gpg)
the GPG key should be exported by: `gpg --armor --export-secret-keys YOUR_ID`.

### GPG_PASSPHRASE

### SONATYPE_USERNAME
The username or token for the sonatype account. Used for publishing to maven central.

### SONATYPE_TOKEN
The password or token for the sonatype account. Used for publishing to maven central.
