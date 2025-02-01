# Changelog

Note: The current release notes are on GitHub: <https://github.com/liquibase/liquibase-percona/releases>.
This file is updated occasionally after a release and might be out of date.

<!--
For Pull Requests:
 Search: \(#(\d+)\)
Replace: ([#$1](https://github.com/liquibase/liquibase-percona/pull/$1))

For Issues:
 Search: (?<![\[\\])#(\d+)\b
Replace: [#$1](https://github.com/liquibase/liquibase-percona/issues/$1)
-->

## [v4.31.0](https://github.com/liquibase/liquibase-percona/tree/v4.31.0) (2025-02-01)

Support for Liquibase 4.31.0.

### 📗 Notable Changes

- ([#541](https://github.com/liquibase/liquibase-percona/pull/541)) build(deps): bump org.liquibase:liquibase-core from 4.30.0 to 4.31.0 @adangel

### 🚀 New Features

- ([#542](https://github.com/liquibase/liquibase-percona/pull/542)) Support MySQL 8.4 @adangel

### 🧰 Maintenance

- ([#539](https://github.com/liquibase/liquibase-percona/pull/539)) Fix script download-toolkit.sh @adangel

### 🤖 Security Driver and Other Updates

- ([#541](https://github.com/liquibase/liquibase-percona/pull/541)) build(deps): bump org.liquibase:liquibase-core from 4.30.0 to 4.31.0 @adangel
- ([#540](https://github.com/liquibase/liquibase-percona/pull/540)) Bump percona-toolkit from 3.6.0 to 3.7.0 @adangel
- ([#538](https://github.com/liquibase/liquibase-percona/pull/538)) build(deps-dev): bump com.mysql:mysql-connector-j from 9.1.0 to 9.2.0 @[dependabot[bot]](https://github.com/apps/dependabot)
- ([#537](https://github.com/liquibase/liquibase-percona/pull/537)) Bump maven-release-plugin to 3.1.1 @adangel

**Full Changelog**: https://github.com/liquibase/liquibase-percona/compare/v4.30.0...v4.31.0

## [v4.30.0](https://github.com/liquibase/liquibase-percona/tree/v4.30.0) (2024-12-19)

Support for Liquibase 4.30.0.

### 📗 Notable Changes

- ([#532](https://github.com/liquibase/liquibase-percona/pull/532)) build(deps): bump org.liquibase:liquibase-core from 4.29.2 to 4.30.0 @dependabot

### 🧰 Maintenance

- ([#536](https://github.com/liquibase/liquibase-percona/pull/536)) chore: Prepare release 4.30.0 @adangel
- ([#535](https://github.com/liquibase/liquibase-percona/pull/535)) chore: Rework release workflow @adangel
- ([#534](https://github.com/liquibase/liquibase-percona/pull/534)) build: execute verify lifecycle for "build-test" @adangel

### 🤖 Security Driver and Other Updates

<details>
<summary>21 changes</summary>

- ([#533](https://github.com/liquibase/liquibase-percona/pull/533)) build(deps-dev): bump org.mariadb.jdbc:mariadb-java-client from 3.3.3 to 3.5.1 @dependabot
- ([#527](https://github.com/liquibase/liquibase-percona/pull/527)) build(deps): bump com.github.spotbugs:spotbugs-maven-plugin from 4.8.3.1 to 4.8.6.6 @dependabot
- ([#515](https://github.com/liquibase/liquibase-percona/pull/515)) build(deps-dev): bump com.mysql:mysql-connector-j from 8.4.0 to 9.1.0 @dependabot
- ([#523](https://github.com/liquibase/liquibase-percona/pull/523)) build(deps-dev): bump org.apache.tomcat:tomcat-jdbc from 10.1.30 to 11.0.2 @dependabot
- ([#532](https://github.com/liquibase/liquibase-percona/pull/532)) build(deps): bump org.liquibase:liquibase-core from 4.29.2 to 4.30.0 @dependabot
- ([#531](https://github.com/liquibase/liquibase-percona/pull/531)) build(deps): bump io.fabric8:docker-maven-plugin from 0.44.0 to 0.45.1 @dependabot
- ([#530](https://github.com/liquibase/liquibase-percona/pull/530)) build(deps): bump org.yaml:snakeyaml from 2.2 to 2.3 @dependabot
- ([#529](https://github.com/liquibase/liquibase-percona/pull/529)) build(deps): bump org.codehaus.mojo:build-helper-maven-plugin from 3.5.0 to 3.6.0 @dependabot
- ([#528](https://github.com/liquibase/liquibase-percona/pull/528)) build(deps): bump org.jacoco:jacoco-maven-plugin from 0.8.11 to 0.8.12 @dependabot
- ([#526](https://github.com/liquibase/liquibase-percona/pull/526)) build(deps-dev): bump org.junit.jupiter:junit-jupiter from 5.10.2 to 5.11.3 @dependabot
- ([#525](https://github.com/liquibase/liquibase-percona/pull/525)) build(deps): bump org.apache.maven.plugins:maven-invoker-plugin from 3.8.1 to 3.9.0 @dependabot
- ([#524](https://github.com/liquibase/liquibase-percona/pull/524)) build(deps): bump org.apache.maven.plugins:maven-javadoc-plugin from 3.11.1 to 3.11.2 @dependabot
- ([#522](https://github.com/liquibase/liquibase-percona/pull/522)) build(deps-dev): bump org.apache.commons:commons-dbcp2 from 2.12.0 to 2.13.0 @dependabot
- ([#519](https://github.com/liquibase/liquibase-percona/pull/519)) build(deps): bump org.apache.maven.plugins:maven-surefire-plugin from 3.5.1 to 3.5.2 @dependabot
- ([#520](https://github.com/liquibase/liquibase-percona/pull/520)) build(deps): bump org.apache.maven.plugins:maven-javadoc-plugin from 3.10.1 to 3.11.1 @dependabot
- ([#518](https://github.com/liquibase/liquibase-percona/pull/518)) build(deps): bump org.apache.maven.plugins:maven-pmd-plugin from 3.25.0 to 3.26.0 @dependabot
- ([#517](https://github.com/liquibase/liquibase-percona/pull/517)) build(deps): bump org.apache.maven.plugins:maven-invoker-plugin from 3.8.0 to 3.8.1 @dependabot
- ([#516](https://github.com/liquibase/liquibase-percona/pull/516)) build(deps): bump org.codehaus.mojo:exec-maven-plugin from 3.4.1 to 3.5.0 @dependabot
- ([#512](https://github.com/liquibase/liquibase-percona/pull/512)) build(deps): bump org.apache.maven.plugins:maven-surefire-plugin from 3.5.0 to 3.5.1 @dependabot
- ([#511](https://github.com/liquibase/liquibase-percona/pull/511)) build(deps): bump org.apache.maven.plugins:maven-javadoc-plugin from 3.10.0 to 3.10.1 @dependabot
- ([#510](https://github.com/liquibase/liquibase-percona/pull/510)) build(deps): bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.6 to 3.2.7 @dependabot
</details>

**Full Changelog**: https://github.com/liquibase/liquibase-percona/compare/v4.29.2...v4.30.0

## [v4.29.2](https://github.com/liquibase/liquibase-percona/tree/v4.29.2) (2024-09-29)

Support for Liquibase 4.29.2.

### 📗 Notable Changes

- ([#507](https://github.com/liquibase/liquibase-percona/pull/507)) Bump org.liquibase:liquibase-core from 4.28.0 to 4.29.2 @adangel

### 🐛 Bug Fixes 🛠

- ([#493](https://github.com/liquibase/liquibase-percona/pull/493)) Fixes [#492](https://github.com/liquibase/liquibase-percona/issues/492): Effectively disable sql parser when defaultOn=false @westse
- ([#477](https://github.com/liquibase/liquibase-percona/pull/477)) Fixes [#473](https://github.com/liquibase/liquibase-percona/issues/473): parsing database name and table name from raw sql @adangel

### 🧰 Maintenance

- ([#505](https://github.com/liquibase/liquibase-percona/pull/505)) chore: wait for log output when starting mysql/mariadb @adangel
- ([#509](https://github.com/liquibase/liquibase-percona/pull/509)) chore: Prepare release 4.29.2 @adangel

### 🤖 Security Driver and Other Updates

<details>
<summary>28 changes</summary>

- ([#506](https://github.com/liquibase/liquibase-percona/pull/506)) Bump Percona Toolkit from 3.5.7 to 3.6.0 @adangel
- ([#499](https://github.com/liquibase/liquibase-percona/pull/499)) build(deps): bump org.apache.maven.plugins:maven-pmd-plugin from 3.24.0 to 3.25.0 @dependabot
- ([#490](https://github.com/liquibase/liquibase-percona/pull/490)) build(deps): bump org.codehaus.mojo:exec-maven-plugin from 3.4.0 to 3.4.1 @dependabot
- ([#504](https://github.com/liquibase/liquibase-percona/pull/504)) build(deps): bump liquibase/build-logic from 0.7.8 to main @adangel
- ([#503](https://github.com/liquibase/liquibase-percona/pull/503)) build(deps-dev): bump org.apache.tomcat:tomcat-jdbc from 10.1.29 to 10.1.30 @dependabot
- ([#502](https://github.com/liquibase/liquibase-percona/pull/502)) build(deps): bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.5 to 3.2.6 @dependabot
- ([#501](https://github.com/liquibase/liquibase-percona/pull/501)) build(deps-dev): bump org.apache.tomcat:tomcat-jdbc from 10.1.28 to 10.1.29 @dependabot
- ([#498](https://github.com/liquibase/liquibase-percona/pull/498)) build(deps): bump org.apache.maven.plugins:maven-surefire-plugin from 3.4.0 to 3.5.0 @dependabot
- ([#497](https://github.com/liquibase/liquibase-percona/pull/497)) build(deps): bump org.apache.maven.plugins:maven-javadoc-plugin from 3.8.0 to 3.10.0 @dependabot
- ([#496](https://github.com/liquibase/liquibase-percona/pull/496)) build(deps): bump org.apache.maven.plugins:maven-invoker-plugin from 3.7.0 to 3.8.0 @dependabot
- ([#495](https://github.com/liquibase/liquibase-percona/pull/495)) build(deps): bump org.apache.maven.plugins:maven-deploy-plugin from 3.1.2 to 3.1.3 @dependabot
- ([#494](https://github.com/liquibase/liquibase-percona/pull/494)) build(deps): bump org.apache.maven.plugins:maven-surefire-plugin from 3.3.1 to 3.4.0 @dependabot
- ([#489](https://github.com/liquibase/liquibase-percona/pull/489)) build(deps): bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.4 to 3.2.5 @dependabot
- ([#488](https://github.com/liquibase/liquibase-percona/pull/488)) build(deps-dev): bump org.apache.tomcat:tomcat-jdbc from 10.1.26 to 10.1.28 @dependabot
- ([#487](https://github.com/liquibase/liquibase-percona/pull/487)) build(deps): bump org.codehaus.mojo:exec-maven-plugin from 3.3.0 to 3.4.0 @dependabot
- ([#486](https://github.com/liquibase/liquibase-percona/pull/486)) build(deps): bump org.apache.maven.plugins:maven-javadoc-plugin from 3.7.0 to 3.8.0 @dependabot
- ([#484](https://github.com/liquibase/liquibase-percona/pull/484)) build(deps-dev): bump org.apache.tomcat:tomcat-jdbc from 10.1.25 to 10.1.26 @dependabot
- ([#481](https://github.com/liquibase/liquibase-percona/pull/481)) build(deps): bump org.apache.maven.plugins:maven-jar-plugin from 3.4.1 to 3.4.2 @dependabot
- ([#485](https://github.com/liquibase/liquibase-percona/pull/485)) build(deps): bump org.apache.maven.plugins:maven-pmd-plugin from 3.23.0 to 3.24.0 @dependabot
- ([#483](https://github.com/liquibase/liquibase-percona/pull/483)) build(deps): bump org.apache.maven.plugins:maven-surefire-plugin from 3.3.0 to 3.3.1 @dependabot
- ([#480](https://github.com/liquibase/liquibase-percona/pull/480)) build(deps-dev): bump org.apache.tomcat:tomcat-jdbc from 10.1.24 to 10.1.25 @dependabot
- ([#479](https://github.com/liquibase/liquibase-percona/pull/479)) build(deps): bump org.apache.maven.plugins:maven-surefire-plugin from 3.2.5 to 3.3.0 @dependabot
- ([#478](https://github.com/liquibase/liquibase-percona/pull/478)) build(deps): bump org.apache.maven.plugins:maven-pmd-plugin from 3.22.0 to 3.23.0 @dependabot
- ([#476](https://github.com/liquibase/liquibase-percona/pull/476)) build(deps): bump org.apache.maven.plugins:maven-javadoc-plugin from 3.6.3 to 3.7.0 @dependabot
- ([#475](https://github.com/liquibase/liquibase-percona/pull/475)) build(deps): bump org.apache.maven.plugins:maven-enforcer-plugin from 3.4.1 to 3.5.0 @dependabot
- ([#474](https://github.com/liquibase/liquibase-percona/pull/474)) build(deps): bump liquibase/build-logic from 0.7.7 to 0.7.8 @dependabot
- ([#471](https://github.com/liquibase/liquibase-percona/pull/471)) build(deps): bump org.sonatype.plugins:nexus-staging-maven-plugin from 1.6.13 to 1.7.0 @dependabot
- ([#470](https://github.com/liquibase/liquibase-percona/pull/470)) build(deps): bump org.apache.maven.plugins:maven-invoker-plugin from 3.6.1 to 3.7.0 @dependabot
</details>

**Full Changelog**: https://github.com/liquibase/liquibase-percona/compare/liquibase-percona-4.28.0...v4.29.2


## [v4.28.0](https://github.com/liquibase/liquibase-percona/tree/v4.28.0) (2024-05-24)

Support for Liquibase 4.28.0.

### 📗 Notable Changes

- ([#468](https://github.com/liquibase/liquibase-percona/issues/468)) Bump org.liquibase:liquibase-core from 4.27.0 to 4.28.0 @adangel

### Changes

- ([#460](https://github.com/liquibase/liquibase-percona/pull/460)) DAT-17572   Modify CI/CD Configurations for Nexus Integration @jandroav

### 🧰 Maintenance

- ([#466](https://github.com/liquibase/liquibase-percona/pull/466)) build: run automerge only after integration tests @adangel
- ([#465](https://github.com/liquibase/liquibase-percona/pull/465)) chore: Use java 21 instead of 18 for building @adangel
- ([#464](https://github.com/liquibase/liquibase-percona/pull/464)) chore: Update CHANGELOG.md and docker image after v4.27.0 release @adangel
- ([#467](https://github.com/liquibase/liquibase-percona/pull/467)) build: Run integration tests against mysql 8.0 and 8.2 @adangel
- ([#463](https://github.com/liquibase/liquibase-percona/pull/463)) chore: Fix unit tests after mysql update @adangel

### 🤖 Security Driver and Other Updates

<details>
<summary>6 changes</summary>

- ([#462](https://github.com/liquibase/liquibase-percona/pull/462)) chore(deps): bump org.codehaus.mojo:exec-maven-plugin from 3.2.0 to 3.3.0 @dependabot
- ([#461](https://github.com/liquibase/liquibase-percona/pull/461)) chore(deps-dev): bump org.apache.tomcat:tomcat-jdbc from 10.1.23 to 10.1.24 @dependabot
- ([#459](https://github.com/liquibase/liquibase-percona/pull/459)) chore(deps): bump liquibase/build-logic from 0.7.5 to 0.7.7 @dependabot
- ([#457](https://github.com/liquibase/liquibase-percona/pull/457)) chore(deps-dev): bump com.mysql:mysql-connector-j from 8.3.0 to 8.4.0 @dependabot
- ([#456](https://github.com/liquibase/liquibase-percona/pull/456)) chore(deps): bump org.apache.maven.plugins:maven-deploy-plugin from 3.1.1 to 3.1.2 @dependabot
- ([#455](https://github.com/liquibase/liquibase-percona/pull/455)) chore(deps): bump liquibase/build-logic from 0.7.4 to 0.7.5 @dependabot
</details>

**Full Changelog**: https://github.com/liquibase/liquibase-percona/compare/v4.27.0...v4.28.0

## [v4.27.0](https://github.com/liquibase/liquibase-percona/tree/v4.27.0) (2024-04-25)

Support for Liquibase 4.27.0.

### 📗 Notable Changes

- ([#453](https://github.com/liquibase/liquibase-percona/issues/453)) Bump org.liquibase:liquibase-core from 4.26.0 to 4.27.0 @adangel

### 🧰 Maintenance

- ([#454](https://github.com/liquibase/liquibase-percona/issues/454)) chore: Use https for scm connection @adangel
- ([#450](https://github.com/liquibase/liquibase-percona/issues/450)) Update CHANGELOG.md @adangel
- ([#449](https://github.com/liquibase/liquibase-percona/issues/449)) chore: Fix PMD issues @adangel

### 🤖 Security Driver and Other Updates

<details>
<summary>16 changes</summary>

- ([#453](https://github.com/liquibase/liquibase-percona/issues/453)) Bump org.liquibase:liquibase-core from 4.26.0 to 4.27.0 @adangel
- ([#451](https://github.com/liquibase/liquibase-percona/issues/451)) Bump Percona Toolkit from 3.5.5 to 3.5.7 @adangel
- ([#452](https://github.com/liquibase/liquibase-percona/issues/452)) chore(deps): bump liquibase/build-logic from 0.7.2 to 0.7.4 @dependabot
- ([#448](https://github.com/liquibase/liquibase-percona/issues/448)) chore(deps): bump org.apache.maven.plugins:maven-pmd-plugin from 3.21.2 to 3.22.0 @dependabot
- ([#447](https://github.com/liquibase/liquibase-percona/issues/447)) chore(deps-dev): bump org.apache.tomcat:tomcat-jdbc from 10.1.20 to 10.1.23 @dependabot
- ([#446](https://github.com/liquibase/liquibase-percona/issues/446)) chore(deps): bump org.apache.maven.plugins:maven-jar-plugin from 3.4.0 to 3.4.1 @dependabot
- ([#445](https://github.com/liquibase/liquibase-percona/issues/445)) chore(deps): bump liquibase/build-logic from 0.7.1 to 0.7.2 @dependabot
- ([#444](https://github.com/liquibase/liquibase-percona/issues/444)) chore(deps): bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.3 to 3.2.4 @dependabot
- ([#443](https://github.com/liquibase/liquibase-percona/issues/443)) chore(deps): bump liquibase/build-logic from 0.7.0 to 0.7.1 @dependabot
- ([#442](https://github.com/liquibase/liquibase-percona/issues/442)) chore(deps): bump org.apache.maven.plugins:maven-jar-plugin from 3.3.0 to 3.4.0 @dependabot
- ([#441](https://github.com/liquibase/liquibase-percona/issues/441)) chore(deps): bump liquibase/build-logic from 0.6.9 to 0.7.0 @dependabot
- ([#436](https://github.com/liquibase/liquibase-percona/issues/436)) chore(deps): bump whelk-io/maven-settings-xml-action from 21 to 22 @dependabot
- ([#440](https://github.com/liquibase/liquibase-percona/issues/440)) chore(deps): bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.2 to 3.2.3 @dependabot
- ([#439](https://github.com/liquibase/liquibase-percona/issues/439)) chore(deps): bump org.apache.maven.plugins:maven-source-plugin from 3.3.0 to 3.3.1 @dependabot
- ([#438](https://github.com/liquibase/liquibase-percona/issues/438)) chore(deps): bump liquibase/build-logic from 0.6.8 to 0.6.9 @dependabot
- ([#437](https://github.com/liquibase/liquibase-percona/issues/437)) chore(deps): bump org.apache.maven.plugins:maven-invoker-plugin from 3.6.0 to 3.6.1 @dependabot
</details>

**Full Changelog**: https://github.com/liquibase/liquibase-percona/compare/v4.26.0...v4.27.0

## [v4.26.0](https://github.com/liquibase/liquibase-percona/tree/v4.26.0) (2024-03-27)

Support for Liquibase 4.26.0.

### Changes

- ([#424](https://github.com/liquibase/liquibase-percona/pull/424)) Fix integration test (addColumnFailIfNoPT) @adangel
- ([#383](https://github.com/liquibase/liquibase-percona/pull/383)) Run Integration Tests additionally with Java 21 @adangel

### 🐛 Bug Fixes 🛠

- ([#381](https://github.com/liquibase/liquibase-percona/pull/381)) Add missing null check for ConstrainsConfig::isNullable @adangel

### 💥 Breaking Changes

- ([#378](https://github.com/liquibase/liquibase-percona/pull/378)) Remove support for old mysql connectors (5.x and 6.x) @adangel

### 🧰 Maintenance

<details>
<summary>9 changes</summary>

- ([#433](https://github.com/liquibase/liquibase-percona/pull/433)) chore(release-drafter.yml): Add chore category, fix full changelog re… @adangel
- ([#432](https://github.com/liquibase/liquibase-percona/pull/432)) chore: Bump version to 4.26.0-SNAPSHOT @adangel
- ([#431](https://github.com/liquibase/liquibase-percona/pull/431)) chore(release-drafter.yml): Fix name-template @adangel
- ([#426](https://github.com/liquibase/liquibase-percona/pull/426)) fix(test.yml): change event trigger from 'workflow\_dispatch' to 'pull… @jandroav
- ([#390](https://github.com/liquibase/liquibase-percona/pull/390)) Update workflows @jandroav
- ([#389](https://github.com/liquibase/liquibase-percona/pull/389)) Update workflows @jandroav
- ([#388](https://github.com/liquibase/liquibase-percona/pull/388)) Update workflows @jandroav
- ([#387](https://github.com/liquibase/liquibase-percona/pull/387)) chore(pom.xml): remove unused configuration and plugins from pom.xml @jandroav
- ([#385](https://github.com/liquibase/liquibase-percona/pull/385)) fix workflows @jandroav
</details>

### 🤖 Security Driver and Other Updates

<details>
<summary>42 changes</summary>

- ([#430](https://github.com/liquibase/liquibase-percona/pull/430)) chore(deps): bump liquibase/build-logic from 0.6.7 to 0.6.8 @dependabot
- ([#429](https://github.com/liquibase/liquibase-percona/pull/429)) chore(deps-dev): bump org.apache.tomcat:tomcat-jdbc from 10.1.19 to 10.1.20 @dependabot
- ([#428](https://github.com/liquibase/liquibase-percona/pull/428)) chore(deps): bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.1 to 3.2.2 @dependabot
- ([#427](https://github.com/liquibase/liquibase-percona/pull/427)) chore(deps): bump liquibase/build-logic from 0.6.6 to 0.6.7 @dependabot
- ([#393](https://github.com/liquibase/liquibase-percona/pull/393)) Bump org.codehaus.mojo:build-helper-maven-plugin from 3.4.0 to 3.5.0 @dependabot
- ([#375](https://github.com/liquibase/liquibase-percona/pull/375)) Bump org.mariadb.jdbc:mariadb-java-client from 3.2.0 to 3.3.3 @dependabot
- ([#386](https://github.com/liquibase/liquibase-percona/pull/386)) Bump actions/setup-java from 3 to 4 @dependabot
- ([#423](https://github.com/liquibase/liquibase-percona/pull/423)) chore(deps): bump liquibase/build-logic from 0.6.5 to 0.6.6 @dependabot
- ([#422](https://github.com/liquibase/liquibase-percona/pull/422)) chore(deps): bump org.apache.maven.plugins:maven-compiler-plugin from 3.12.1 to 3.13.0 @dependabot
- ([#421](https://github.com/liquibase/liquibase-percona/pull/421)) chore(deps): bump org.apache.maven.plugins:maven-gpg-plugin from 3.2.0 to 3.2.1 @dependabot
- ([#420](https://github.com/liquibase/liquibase-percona/pull/420)) chore(deps): bump org.apache.maven.plugins:maven-gpg-plugin from 3.1.0 to 3.2.0 @dependabot
- ([#419](https://github.com/liquibase/liquibase-percona/pull/419)) chore(deps): bump liquibase/build-logic from 0.6.4 to 0.6.5 @dependabot
- ([#418](https://github.com/liquibase/liquibase-percona/pull/418)) chore(deps): bump liquibase/build-logic from 0.6.3 to 0.6.4 @dependabot
- ([#417](https://github.com/liquibase/liquibase-percona/pull/417)) chore(deps-dev): bump org.apache.commons:commons-dbcp2 from 2.11.0 to 2.12.0 @dependabot
- ([#416](https://github.com/liquibase/liquibase-percona/pull/416)) chore(deps): bump liquibase/build-logic from 0.6.2 to 0.6.3 @dependabot
- ([#415](https://github.com/liquibase/liquibase-percona/pull/415)) Bump org.codehaus.mojo:exec-maven-plugin from 3.1.1 to 3.2.0 @dependabot
- ([#414](https://github.com/liquibase/liquibase-percona/pull/414)) Bump org.apache.tomcat:tomcat-jdbc from 10.1.18 to 10.1.19 @dependabot
- ([#413](https://github.com/liquibase/liquibase-percona/pull/413)) Bump io.fabric8:docker-maven-plugin from 0.43.4 to 0.44.0 @dependabot
- ([#412](https://github.com/liquibase/liquibase-percona/pull/412)) Bump liquibase/build-logic from 0.6.1 to 0.6.2 @dependabot
- ([#411](https://github.com/liquibase/liquibase-percona/pull/411)) Bump com.github.spotbugs:spotbugs-maven-plugin from 4.8.3.0 to 4.8.3.1 @dependabot
- ([#410](https://github.com/liquibase/liquibase-percona/pull/410)) Bump org.liquibase:liquibase-core from 4.25.1 to 4.26.0 @dependabot
- ([#409](https://github.com/liquibase/liquibase-percona/pull/409)) Bump org.junit.jupiter:junit-jupiter from 5.10.1 to 5.10.2 @dependabot
- ([#408](https://github.com/liquibase/liquibase-percona/pull/408)) Bump com.mysql:mysql-connector-j from 8.2.0 to 8.3.0 @dependabot
- ([#407](https://github.com/liquibase/liquibase-percona/pull/407)) Bump liquibase/build-logic from 0.6.0 to 0.6.1 @dependabot
- ([#406](https://github.com/liquibase/liquibase-percona/pull/406)) Bump com.github.spotbugs:spotbugs-maven-plugin from 4.8.2.0 to 4.8.3.0 @dependabot
- ([#405](https://github.com/liquibase/liquibase-percona/pull/405)) Bump org.apache.tomcat:tomcat-jdbc from 10.1.17 to 10.1.18 @dependabot
- ([#402](https://github.com/liquibase/liquibase-percona/pull/402)) Bump org.apache.maven.plugins:maven-compiler-plugin from 3.12.0 to 3.12.1 @dependabot
- ([#404](https://github.com/liquibase/liquibase-percona/pull/404)) Bump org.apache.maven.plugins:maven-surefire-plugin from 3.2.3 to 3.2.5 @dependabot
- ([#403](https://github.com/liquibase/liquibase-percona/pull/403)) Bump liquibase/build-logic from 0.5.9 to 0.6.0 @dependabot
- ([#401](https://github.com/liquibase/liquibase-percona/pull/401)) Bump org.liquibase:liquibase-core from 4.25.0 to 4.25.1 @dependabot
- ([#400](https://github.com/liquibase/liquibase-percona/pull/400)) Bump org.apache.maven.plugins:maven-compiler-plugin from 3.11.0 to 3.12.0 @dependabot
- ([#399](https://github.com/liquibase/liquibase-percona/pull/399)) Bump liquibase/build-logic from 0.5.8 to 0.5.9 @dependabot
- ([#398](https://github.com/liquibase/liquibase-percona/pull/398)) Bump org.apache.maven.plugins:maven-surefire-plugin from 3.2.2 to 3.2.3 @dependabot
- ([#397](https://github.com/liquibase/liquibase-percona/pull/397)) Bump org.apache.tomcat:tomcat-jdbc from 10.1.16 to 10.1.17 @dependabot
- ([#396](https://github.com/liquibase/liquibase-percona/pull/396)) Bump liquibase/build-logic from 0.5.7 to 0.5.8 @dependabot
- ([#394](https://github.com/liquibase/liquibase-percona/pull/394)) Bump com.github.spotbugs:spotbugs-maven-plugin from 4.8.1.0 to 4.8.2.0 @dependabot
- ([#395](https://github.com/liquibase/liquibase-percona/pull/395)) Bump org.apache.maven.plugins:maven-javadoc-plugin from 3.6.2 to 3.6.3 @dependabot
- ([#391](https://github.com/liquibase/liquibase-percona/pull/391)) Bump org.apache.maven.plugins:maven-javadoc-plugin from 3.6.0 to 3.6.2 @dependabot
- ([#392](https://github.com/liquibase/liquibase-percona/pull/392)) Bump org.jacoco:jacoco-maven-plugin from 0.8.10 to 0.8.11 @dependabot
- ([#384](https://github.com/liquibase/liquibase-percona/pull/384)) Bump org.codehaus.mojo:build-helper-maven-plugin from 3.4.0 to 3.5.0 @dependabot
- ([#382](https://github.com/liquibase/liquibase-percona/pull/382)) Update MySQL and MariaDB versions for integration tests @adangel
- ([#379](https://github.com/liquibase/liquibase-percona/pull/379)) Bump org.codehaus.mojo:exec-maven-plugin from 3.1.0 to 3.1.1 @dependabot
</details>

**Full Changelog**: https://github.com/liquibase/liquibase-percona/compare/v4.25.0...v4.26.0

## [v4.25.0](https://github.com/liquibase/liquibase-percona/tree/v4.25.0) (2023-11-16)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.24.0...v4.25.0)

**📦 Dependency updates:**

- Bump org.apache.tomcat:tomcat-jdbc from 10.1.15 to 10.1.16 [\#377](https://github.com/liquibase/liquibase-percona/pull/377) (@dependabot[bot])
- Bump org.liquibase:liquibase-core from 4.24.0 to 4.25.0 [\#376](https://github.com/liquibase/liquibase-percona/pull/376) (@dependabot[bot])
- Bump org.apache.maven.plugins:maven-javadoc-plugin from 3.6.0 to 3.6.2 [\#374](https://github.com/liquibase/liquibase-percona/pull/374) (@dependabot[bot])
- Bump org.apache.maven.plugins:maven-surefire-plugin from 3.2.1 to 3.2.2 [\#373](https://github.com/liquibase/liquibase-percona/pull/373) (@dependabot[bot])
- Bump com.github.spotbugs:spotbugs-maven-plugin from 4.7.3.6 to 4.8.1.0 [\#372](https://github.com/liquibase/liquibase-percona/pull/372) (@dependabot[bot])
- Bump org.junit.jupiter:junit-jupiter from 5.10.0 to 5.10.1 [\#371](https://github.com/liquibase/liquibase-percona/pull/371) (@dependabot[bot])
- Bump org.apache.maven.plugins:maven-pmd-plugin from 3.21.0 to 3.21.2 [\#370](https://github.com/liquibase/liquibase-percona/pull/370) (@dependabot[bot])
- Bump org.apache.commons:commons-dbcp2 from 2.10.0 to 2.11.0 [\#369](https://github.com/liquibase/liquibase-percona/pull/369) (@dependabot[bot])
- Bump org.apache.maven.plugins:maven-surefire-plugin from 3.1.2 to 3.2.1 [\#368](https://github.com/liquibase/liquibase-percona/pull/368) (@dependabot[bot])
- Bump org.apache.tomcat:tomcat-jdbc from 10.1.14 to 10.1.15 [\#367](https://github.com/liquibase/liquibase-percona/pull/367) (@dependabot[bot])
- Bump org.apache.tomcat:tomcat-jdbc from 10.1.13 to 10.1.14 [\#366](https://github.com/liquibase/liquibase-percona/pull/366) (@dependabot[bot])

## [v4.24.0](https://github.com/liquibase/liquibase-percona/tree/v4.24.0) (2023-10-05)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.23.2...v4.24.0)

**🚀 Implemented enhancements:**

- Support Rollback in Formatted SQL [\#361](https://github.com/liquibase/liquibase-percona/issues/361)
- Set server\_id=1 for MySQL in integration tests [\#364](https://github.com/liquibase/liquibase-percona/pull/364) (@adangel)

**📦 Dependency updates:**

- Bump Percona Toolkit from 3.5.4 to 3.5.5 [\#365](https://github.com/liquibase/liquibase-percona/pull/365) (@adangel)
- Bump org.liquibase:liquibase-core from 4.23.2 to 4.24.0 [\#363](https://github.com/liquibase/liquibase-percona/pull/363) (@dependabot[bot])
- Bump com.github.spotbugs:spotbugs-maven-plugin from 4.7.3.5 to 4.7.3.6 [\#360](https://github.com/liquibase/liquibase-percona/pull/360) (@dependabot[bot])

**🎉 Merged pull requests:**

- Support rollback for formatted SQL changelogs [\#362](https://github.com/liquibase/liquibase-percona/pull/362) (@adangel)

## [v4.23.2](https://github.com/liquibase/liquibase-percona/tree/v4.23.2) (2023-09-18)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.23.1...v4.23.2)

**📦 Dependency updates:**

- Bump org.apache.maven.plugins:maven-javadoc-plugin from 3.5.0 to 3.6.0 [\#359](https://github.com/liquibase/liquibase-percona/pull/359) (@dependabot[bot])
- Bump org.liquibase:liquibase-core from 4.23.1 to 4.23.2 [\#358](https://github.com/liquibase/liquibase-percona/pull/358) (@dependabot[bot])
- Bump org.apache.maven.plugins:maven-enforcer-plugin from 3.4.0 to 3.4.1 [\#357](https://github.com/liquibase/liquibase-percona/pull/357) (@dependabot[bot])
- Bump actions/checkout from 3 to 4 [\#356](https://github.com/liquibase/liquibase-percona/pull/356) (@dependabot[bot])
- Bump org.apache.commons:commons-dbcp2 from 2.9.0 to 2.10.0 [\#355](https://github.com/liquibase/liquibase-percona/pull/355) (@dependabot[bot])
- Bump org.mariadb.jdbc:mariadb-java-client from 3.1.4 to 3.2.0 [\#354](https://github.com/liquibase/liquibase-percona/pull/354) (@dependabot[bot])
- Bump org.yaml:snakeyaml from 2.1 to 2.2 [\#353](https://github.com/liquibase/liquibase-percona/pull/353) (@dependabot[bot])
- Bump org.apache.tomcat:tomcat-jdbc from 10.1.12 to 10.1.13 [\#352](https://github.com/liquibase/liquibase-percona/pull/352) (@dependabot[bot])
- Bump activesupport from 7.0.4.3 to 7.0.7.2 [\#351](https://github.com/liquibase/liquibase-percona/pull/351) (@dependabot[bot])
- Bump org.apache.maven.plugins:maven-enforcer-plugin from 3.3.0 to 3.4.0 [\#350](https://github.com/liquibase/liquibase-percona/pull/350) (@dependabot[bot])
- Bump io.fabric8:docker-maven-plugin from 0.43.3 to 0.43.4 [\#349](https://github.com/liquibase/liquibase-percona/pull/349) (@dependabot[bot])
- Bump org.apache.tomcat:tomcat-jdbc from 10.1.11 to 10.1.12 [\#348](https://github.com/liquibase/liquibase-percona/pull/348) (@dependabot[bot])
- Bump io.fabric8:docker-maven-plugin from 0.43.2 to 0.43.3 [\#347](https://github.com/liquibase/liquibase-percona/pull/347) (@dependabot[bot])

## [v4.23.1](https://github.com/liquibase/liquibase-percona/tree/v4.23.1) (2023-08-11)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.23.0...v4.23.1)

**📦 Dependency updates:**

- Bump percona toolkit from 3.5.3 to 3.5.4 [\#346](https://github.com/liquibase/liquibase-percona/pull/346) (@adangel)
- Bump org.liquibase:liquibase-core from 4.23.0 to 4.23.1 [\#345](https://github.com/liquibase/liquibase-percona/pull/345) (@dependabot[bot])
- Bump org.yaml:snakeyaml from 2.0 to 2.1 [\#344](https://github.com/liquibase/liquibase-percona/pull/344) (@dependabot[bot])
- Bump protocol-http1 from 0.14.2 to 0.15.1 [\#343](https://github.com/liquibase/liquibase-percona/pull/343) (@dependabot[bot])
- Bump io.fabric8:docker-maven-plugin from 0.43.0 to 0.43.2 [\#342](https://github.com/liquibase/liquibase-percona/pull/342) (@dependabot[bot])
- Bump org.junit.jupiter:junit-jupiter from 5.9.3 to 5.10.0 [\#341](https://github.com/liquibase/liquibase-percona/pull/341) (@dependabot[bot])
- Bump tomcat-jdbc from 10.1.10 to 10.1.11 [\#340](https://github.com/liquibase/liquibase-percona/pull/340) (@dependabot[bot])

## [v4.23.0](https://github.com/liquibase/liquibase-percona/tree/v4.23.0) (2023-06-30)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.22.0...v4.23.0)

**📦 Dependency updates:**

- Bump percona-toolkit from 3.5.2 to 3.5.3 [\#339](https://github.com/liquibase/liquibase-percona/pull/339) (@adangel)
- Bump liquibase-core from 4.22.0 to 4.23.0 [\#338](https://github.com/liquibase/liquibase-percona/pull/338) (@dependabot[bot])
- Bump spotbugs-maven-plugin from 4.7.3.4 to 4.7.3.5 [\#337](https://github.com/liquibase/liquibase-percona/pull/337) (@dependabot[bot])
- Bump maven-invoker-plugin from 3.5.1 to 3.6.0 [\#336](https://github.com/liquibase/liquibase-percona/pull/336) (@dependabot[bot])
- Bump tomcat-jdbc from 10.1.9 to 10.1.10 [\#335](https://github.com/liquibase/liquibase-percona/pull/335) (@dependabot[bot])
- Bump maven-surefire-plugin from 3.1.0 to 3.1.2 [\#334](https://github.com/liquibase/liquibase-percona/pull/334) (@dependabot[bot])
- Bump docker-maven-plugin from 0.42.1 to 0.43.0 [\#333](https://github.com/liquibase/liquibase-percona/pull/333) (@dependabot[bot])
- Bump maven-source-plugin from 3.2.1 to 3.3.0 [\#332](https://github.com/liquibase/liquibase-percona/pull/332) (@dependabot[bot])
- Bump tomcat-jdbc from 10.1.8 to 10.1.9 [\#331](https://github.com/liquibase/liquibase-percona/pull/331) (@dependabot[bot])
- Bump maven-pmd-plugin from 3.20.0 to 3.21.0 [\#330](https://github.com/liquibase/liquibase-percona/pull/330) (@dependabot[bot])

## [v4.22.0](https://github.com/liquibase/liquibase-percona/tree/v4.22.0) (2023-05-12)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.20.0...v4.22.0)

**🐛 Fixed bugs:**

- Branch Protection settings break "Update Changelog" workflow [\#316](https://github.com/liquibase/liquibase-percona/issues/316)

**📦 Dependency updates:**

- Bump percona toolkit from 3.5.1 to 3.5.2 [\#329](https://github.com/liquibase/liquibase-percona/pull/329) (@adangel)
- Bump liquibase-core from 4.20.0 to 4.22.0 [\#328](https://github.com/liquibase/liquibase-percona/pull/328) (@dependabot[bot])
- Bump build-helper-maven-plugin from 3.3.0 to 3.4.0 [\#327](https://github.com/liquibase/liquibase-percona/pull/327) (@dependabot[bot])
- Bump maven-surefire-plugin from 3.0.0 to 3.1.0 [\#326](https://github.com/liquibase/liquibase-percona/pull/326) (@dependabot[bot])
- Bump maven-gpg-plugin from 3.0.1 to 3.1.0 [\#325](https://github.com/liquibase/liquibase-percona/pull/325) (@dependabot[bot])
- Bump mariadb-java-client from 3.1.3 to 3.1.4 [\#324](https://github.com/liquibase/liquibase-percona/pull/324) (@dependabot[bot])
- Bump junit-jupiter from 5.9.2 to 5.9.3 [\#323](https://github.com/liquibase/liquibase-percona/pull/323) (@dependabot[bot])
- Bump tomcat-jdbc from 10.1.7 to 10.1.8 [\#322](https://github.com/liquibase/liquibase-percona/pull/322) (@dependabot[bot])
- Updated CHANGELOG.md to apply dependency updates [\#319](https://github.com/liquibase/liquibase-percona/pull/319) (@adangel)
- Bump spotbugs-maven-plugin from 4.7.3.3 to 4.7.3.4 [\#318](https://github.com/liquibase/liquibase-percona/pull/318) (@dependabot[bot])
- Bump docker-maven-plugin from 0.42.0 to 0.42.1 [\#317](https://github.com/liquibase/liquibase-percona/pull/317) (@dependabot[bot])
- Bump maven-enforcer-plugin from 3.2.1 to 3.3.0 [\#314](https://github.com/liquibase/liquibase-percona/pull/314) (@dependabot[bot])
- Bump maven-invoker-plugin from 3.5.0 to 3.5.1 [\#313](https://github.com/liquibase/liquibase-percona/pull/313) (@dependabot[bot])
- Bump maven-deploy-plugin from 3.1.0 to 3.1.1 [\#312](https://github.com/liquibase/liquibase-percona/pull/312) (@dependabot[bot])
- Bump maven-resources-plugin from 3.3.0 to 3.3.1 [\#311](https://github.com/liquibase/liquibase-percona/pull/311) (@dependabot[bot])
- Bump spotbugs-maven-plugin from 4.7.3.2 to 4.7.3.3 [\#310](https://github.com/liquibase/liquibase-percona/pull/310) (@dependabot[bot])
- Bump mariadb-java-client from 3.1.2 to 3.1.3 [\#309](https://github.com/liquibase/liquibase-percona/pull/309) (@dependabot[bot])
- Bump activesupport from 7.0.4.1 to 7.0.4.3 [\#308](https://github.com/liquibase/liquibase-percona/pull/308) (@dependabot[bot])
- Bump maven-surefire-plugin from 2.22.2 to 3.0.0 [\#307](https://github.com/liquibase/liquibase-percona/pull/307) (@dependabot[bot])

## [v4.20.0](https://github.com/liquibase/liquibase-percona/tree/v4.20.0) (2023-03-10)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.19.1...v4.20.0)

**📦 Dependency updates:**

- Bump liquibase-core from 4.19.1 to 4.20.0 [\#306](https://github.com/liquibase/liquibase-percona/pull/306) (@dependabot[bot])
- Bump tomcat-jdbc from 10.1.6 to 10.1.7 [\#305](https://github.com/liquibase/liquibase-percona/pull/305) (@dependabot[bot])

## [v4.19.1](https://github.com/liquibase/liquibase-percona/tree/v4.19.1) (2023-03-03)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.19.0...v4.19.1)

### 🎉 New Features

This release of Liquibase Percona extension ships two new features.

#### Support for Custom SQL changes

You can now use [Custom SQL changes](https://docs.liquibase.com/change-types/sql.html). The Liquibase Percona extension will automatically detect if it is a `ALTER TABLE` statement and will execute it using Percona Toolkit's `pt-online-schema-change` command.

There are some limitations: Only a single statement is supported. When multiple statements (e.g. separated by `;`) are used, then the change is executed as usual. Also, if it is not an `ALTER TABLE` statement, the change is executed as usual without Percona Toolkit. If the statement can't be executed, a warning is logged.

| :warning:     Support for Custom SQL changes is enabled by default. <br> The Liquibase Percona extension will automatically try to execute Custom SQL changes via the Percona Toolkit. If this is not what you want, either disable the extension for this change globally (e.g. via the system property `liquibase.percona.skipChanges=sql`) or individually per change via the [UsePercona flag](https://github.com/liquibase/liquibase-percona#usepercona-flag). You can also globally disable Percona Toolkit usage with the system property `liquibase.percona.defaultOn` and enable it for specific changes only. See [System Properties](https://github.com/liquibase/liquibase-percona#system-properties). |
|-----|

Example usage in XML:

```xml
<databaseChangeLog>
  <changeSet id="2" author="Alice">
    <sql>ALTER TABLE person ADD COLUMN address VARCHAR(255) NULL</sql>
  </changeSet>
  <changeSet id="3" author="Alice">
    <sql xmlns:liquibasePercona="http://www.liquibase.org/xml/ns/dbchangelog-ext/liquibase-percona"
         liquibasePercona:usePercona="false">
        ALTER TABLE person ADD COLUMN address VARCHAR(255) NULL
    </sql>
  </changeSet>
</databaseChangeLog>
```

Example usage in YAML:

```yaml
databaseChangeLog:
- changeSet:
    id: 2
    author: Alice
    changes:
    - sql:
        splitStatements: true
        sql: |
            ALTER TABLE person ADD COLUMN address VARCHAR(255) NULL;
- changeSet:
    id: 3
    author: Alice
    changes:
    - sql:
        usePercona: false
        splitStatements: true
        sql: |
            ALTER TABLE person ADD COLUMN address VARCHAR(255) NULL;
```


#### Support for Formatted SQL Changelogs

You can now use [Formatted SQL Changelogs](https://docs.liquibase.com/concepts/changelogs/sql-format.html). It also supports the `usePercona` flag.

The implementation reuses the support for Custom SQL changes. This means, that the same limitations apply to SQL Changelogs: Multiple statements are not supported. Only `ALTER TABLE` statements are executed with Percona Toolkit's `pt-online-schema-change`.

| :warning:   Support for SQL Changelogs is enabled by default. <br> If you apply a SQL changelog with Liquibase Percona extension, then it will try to execute all changeset with Percona Toolkit if possible. If this is not what you want, you need to make use of the [UsePercona flag](https://github.com/liquibase/liquibase-percona#usepercona-flag). You can also globally disable Percona Toolkit usage with the system property `liquibase.percona.defaultOn` and enable it for specific changes only. See [System Properties](https://github.com/liquibase/liquibase-percona#system-properties). |
|-----|

Example usage:

```sql
--changeset Alice:2
ALTER TABLE person ADD address VARCHAR(255) NULL;

--changeset Alice:3
--liquibasePercona:usePercona="false"
ALTER TABLE person ADD address VARCHAR(255) NULL;
```


**🚀 Implemented enhancements:**

- Support formatted SQL changelogs [\#287](https://github.com/liquibase/liquibase-percona/issues/287)
- Support usePercona on SQL change type [\#80](https://github.com/liquibase/liquibase-percona/issues/80)
- Include extension schema properly [\#294](https://github.com/liquibase/liquibase-percona/pull/294) (@adangel)
- Add support for Formatted SQL changelogs [\#292](https://github.com/liquibase/liquibase-percona/pull/292) (@adangel)
- Add support for SQL change type [\#291](https://github.com/liquibase/liquibase-percona/pull/291) (@adangel)

**🐛 Fixed bugs:**

- Changes with pt-osc are executed multiple times \(liquibase 4.19.1\) [\#303](https://github.com/liquibase/liquibase-percona/issues/303)
- Fix percona toolkit download after Percona homepage change [\#285](https://github.com/liquibase/liquibase-percona/pull/285) (@adangel)

**📦 Dependency updates:**

- Bump liquibase-core from 4.19.0 to 4.19.1 [\#302](https://github.com/liquibase/liquibase-percona/pull/302) (@dependabot[bot])
- Bump docker-maven-plugin from 0.41.0 to 0.42.0 [\#301](https://github.com/liquibase/liquibase-percona/pull/301) (@dependabot[bot])
- Bump snakeyaml from 1.33 to 2.0 [\#300](https://github.com/liquibase/liquibase-percona/pull/300) (@dependabot[bot])
- Bump spotbugs-maven-plugin from 4.7.3.0 to 4.7.3.2 [\#299](https://github.com/liquibase/liquibase-percona/pull/299) (@dependabot[bot])
- Bump maven-compiler-plugin from 3.10.1 to 3.11.0 [\#298](https://github.com/liquibase/liquibase-percona/pull/298) (@dependabot[bot])
- Bump tomcat-jdbc from 10.1.5 to 10.1.6 [\#297](https://github.com/liquibase/liquibase-percona/pull/297) (@dependabot[bot])
- Bump maven from 3.8.6 to 3.9.0 [\#290](https://github.com/liquibase/liquibase-percona/pull/290) (@adangel)
- Bump maven-javadoc-plugin from 3.4.1 to 3.5.0 [\#289](https://github.com/liquibase/liquibase-percona/pull/289) (@dependabot[bot])
- Bump maven-invoker-plugin from 3.4.0 to 3.5.0 [\#288](https://github.com/liquibase/liquibase-percona/pull/288) (@dependabot[bot])
- Bump percona toolkit from 3.5.0 to 3.5.1 [\#286](https://github.com/liquibase/liquibase-percona/pull/286) (@adangel)
- Bump maven-deploy-plugin from 3.0.0 to 3.1.0 [\#284](https://github.com/liquibase/liquibase-percona/pull/284) (@dependabot[bot])
- Bump docker-maven-plugin from 0.40.3 to 0.41.0 [\#283](https://github.com/liquibase/liquibase-percona/pull/283) (@dependabot[bot])
- Bump maven-enforcer-plugin from 3.1.0 to 3.2.1 [\#282](https://github.com/liquibase/liquibase-percona/pull/282) (@dependabot[bot])
- Bump mariadb-java-client from 3.1.0 to 3.1.2 [\#281](https://github.com/liquibase/liquibase-percona/pull/281) (@dependabot[bot])

**✔️ Closed issues:**

- Liquibase percona extension \(URL\) no longer available [\#275](https://github.com/liquibase/liquibase-percona/issues/275)

**🎉 Merged pull requests:**

- Refactor PTOnlineSchemaChangeStatement to be a ExecutablePreparedStatement [\#304](https://github.com/liquibase/liquibase-percona/pull/304) (@adangel)
- Fix deprecation warning about `set-output` [\#295](https://github.com/liquibase/liquibase-percona/pull/295) (@adangel)

## [v4.19.0](https://github.com/liquibase/liquibase-percona/tree/v4.19.0) (2023-01-20)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.18.0...v4.19.0)

**📦 Dependency updates:**

- Bump activesupport from 7.0.2.2 to 7.0.4.1 [\#280](https://github.com/liquibase/liquibase-percona/pull/280) (@dependabot[bot])
- Bump liquibase-core from 4.18.0 to 4.19.0 [\#279](https://github.com/liquibase/liquibase-percona/pull/279) (@dependabot[bot])
- Bump tomcat-jdbc from 10.1.4 to 10.1.5 [\#278](https://github.com/liquibase/liquibase-percona/pull/278) (@dependabot[bot])
- Bump maven-pmd-plugin from 3.19.0 to 3.20.0 [\#276](https://github.com/liquibase/liquibase-percona/pull/276) (@dependabot[bot])
- Bump junit-jupiter from 5.9.1 to 5.9.2 [\#274](https://github.com/liquibase/liquibase-percona/pull/274) (@dependabot[bot])
- Bump docker-maven-plugin from 0.40.2 to 0.40.3 [\#273](https://github.com/liquibase/liquibase-percona/pull/273) (@dependabot[bot])
- Bump maven-invoker-plugin from 3.3.0 to 3.4.0 [\#272](https://github.com/liquibase/liquibase-percona/pull/272) (@dependabot[bot])
- Bump tomcat-jdbc from 10.1.2 to 10.1.4 [\#271](https://github.com/liquibase/liquibase-percona/pull/271) (@dependabot[bot])

## [v4.18.0](https://github.com/liquibase/liquibase-percona/tree/v4.18.0) (2022-12-09)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.17.1...v4.18.0)

**🐛 Fixed bugs:**

- Fix percona toolkit download script [\#269](https://github.com/liquibase/liquibase-percona/pull/269) (@adangel)

**📦 Dependency updates:**

- Bump Percona Toolkit from 3.4.0 to 3.5.0 [\#270](https://github.com/liquibase/liquibase-percona/pull/270) (@adangel)
- Bump liquibase-core from 4.17.1 to 4.18.0 [\#268](https://github.com/liquibase/liquibase-percona/pull/268) (@dependabot[bot])
- Bump mariadb-java-client from 3.0.9 to 3.1.0 [\#267](https://github.com/liquibase/liquibase-percona/pull/267) (@dependabot[bot])
- Bump tomcat-jdbc from 10.1.1 to 10.1.2 [\#266](https://github.com/liquibase/liquibase-percona/pull/266) (@dependabot[bot])
- Bump mariadb-java-client from 3.0.8 to 3.0.9 [\#265](https://github.com/liquibase/liquibase-percona/pull/265) (@dependabot[bot])
- Bump spotbugs-maven-plugin from 4.7.2.1 to 4.7.3.0 [\#264](https://github.com/liquibase/liquibase-percona/pull/264) (@dependabot[bot])

**🎉 Merged pull requests:**

- \[ci\] Fix deprecated set-output GitHub Action command [\#262](https://github.com/liquibase/liquibase-percona/pull/262) (@adangel)

## [v4.17.1](https://github.com/liquibase/liquibase-percona/tree/v4.17.1) (2022-10-27)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.17.0...v4.17.1)

**📦 Dependency updates:**

- Bump liquibase-core from 4.17.0 to 4.17.1 [\#259](https://github.com/liquibase/liquibase-percona/pull/259) (@dependabot[bot])
- Bump actions/setup-java from 2 to 3 [\#257](https://github.com/liquibase/liquibase-percona/pull/257) (@dependabot[bot])
- Bump actions/download-artifact from 2 to 3 [\#256](https://github.com/liquibase/liquibase-percona/pull/256) (@dependabot[bot])
- Bump actions/upload-artifact from 2 to 3 [\#255](https://github.com/liquibase/liquibase-percona/pull/255) (@dependabot[bot])
- Bump tomcat-jdbc from 10.1.0 to 10.1.1 [\#254](https://github.com/liquibase/liquibase-percona/pull/254) (@dependabot[bot])

**🎉 Merged pull requests:**

- Install libdbd-mysql-perl with apt [\#258](https://github.com/liquibase/liquibase-percona/pull/258) (@adangel)

## [v4.17.0](https://github.com/liquibase/liquibase-percona/tree/v4.17.0) (2022-10-11)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.16.0...v4.17.0)

**📦 Dependency updates:**

- Bump liquibase-core from 4.16.0 to 4.17.0 [\#253](https://github.com/liquibase/liquibase-percona/pull/253) (@dependabot[bot])
- Bump spotbugs-maven-plugin from 4.7.2.0 to 4.7.2.1 [\#252](https://github.com/liquibase/liquibase-percona/pull/252) (@dependabot[bot])
- Bump tomcat-jdbc from 10.0.23 to 10.1.0 [\#251](https://github.com/liquibase/liquibase-percona/pull/251) (@dependabot[bot])
- Bump snakeyaml from 1.32 to 1.33 [\#250](https://github.com/liquibase/liquibase-percona/pull/250) (@dependabot[bot])
- Bump mariadb-java-client from 3.0.7 to 3.0.8 [\#249](https://github.com/liquibase/liquibase-percona/pull/249) (@dependabot[bot])
- Bump junit-jupiter from 5.9.0 to 5.9.1 [\#248](https://github.com/liquibase/liquibase-percona/pull/248) (@dependabot[bot])
- Bump maven-jar-plugin from 3.2.2 to 3.3.0 [\#246](https://github.com/liquibase/liquibase-percona/pull/246) (@dependabot[bot])

## [v4.16.0](https://github.com/liquibase/liquibase-percona/tree/v4.16.0) (2022-09-13)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.15.0...v4.16.0)

**📦 Dependency updates:**

- Bump snakeyaml from 1.31 to 1.32 [\#243](https://github.com/liquibase/liquibase-percona/pull/243) (@dependabot[bot])
- Bump liquibase-core from 4.15.0 to 4.16.0 [\#242](https://github.com/liquibase/liquibase-percona/pull/242) (@dependabot[bot])
- Bump spotbugs-maven-plugin from 4.7.1.1 to 4.7.2.0 [\#241](https://github.com/liquibase/liquibase-percona/pull/241) (@dependabot[bot])
- Bump maven-pmd-plugin from 3.18.0 to 3.19.0 [\#240](https://github.com/liquibase/liquibase-percona/pull/240) (@dependabot[bot])
- Bump snakeyaml from 1.30 to 1.31 [\#239](https://github.com/liquibase/liquibase-percona/pull/239) (@dependabot[bot])
- Bump maven-pmd-plugin from 3.17.0 to 3.18.0 [\#238](https://github.com/liquibase/liquibase-percona/pull/238) (@dependabot[bot])

**🎉 Merged pull requests:**

- Remove integration tests for older liquibase versions [\#244](https://github.com/liquibase/liquibase-percona/pull/244) (@adangel)

## [v4.15.0](https://github.com/liquibase/liquibase-percona/tree/v4.15.0) (2022-08-23)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.14.0...v4.15.0)

**📦 Dependency updates:**

- Bump maven-javadoc-plugin from 3.4.0 to 3.4.1 [\#237](https://github.com/liquibase/liquibase-percona/pull/237) (@dependabot[bot])
- Bump liquibase-core from 4.14.0 to 4.15.0 [\#236](https://github.com/liquibase/liquibase-percona/pull/236) (@dependabot[bot])
- Bump mariadb-java-client from 3.0.6 to 3.0.7 [\#235](https://github.com/liquibase/liquibase-percona/pull/235) (@dependabot[bot])
- Bump docker-maven-plugin from 0.40.1 to 0.40.2 [\#234](https://github.com/liquibase/liquibase-percona/pull/234) (@dependabot[bot])
- Bump junit-jupiter from 5.8.2 to 5.9.0 [\#233](https://github.com/liquibase/liquibase-percona/pull/233) (@dependabot[bot])

## [v4.14.0](https://github.com/liquibase/liquibase-percona/tree/v4.14.0) (2022-07-26)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.13.0...v4.14.0)

**📦 Dependency updates:**

- Bump maven-resources-plugin from 3.2.0 to 3.3.0 [\#232](https://github.com/liquibase/liquibase-percona/pull/232) (@dependabot[bot])
- Bump tomcat-jdbc from 10.0.22 to 10.0.23 [\#231](https://github.com/liquibase/liquibase-percona/pull/231) (@dependabot[bot])
- Bump liquibase-core from 4.13.0 to 4.14.0 [\#230](https://github.com/liquibase/liquibase-percona/pull/230) (@dependabot[bot])
- Bump spotbugs-maven-plugin from 4.7.1.0 to 4.7.1.1 [\#229](https://github.com/liquibase/liquibase-percona/pull/229) (@dependabot[bot])
- Bump mysql-connector-java from 8.0.29 to 8.0.30 [\#228](https://github.com/liquibase/liquibase-percona/pull/228) (@dependabot[bot])
- Bump maven-deploy-plugin from 3.0.0-M2 to 3.0.0 [\#227](https://github.com/liquibase/liquibase-percona/pull/227) (@dependabot[bot])

## [v4.13.0](https://github.com/liquibase/liquibase-percona/tree/v4.13.0) (2022-07-16)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.12.0...v4.13.0)

* Support for Liquibase 4.13.0.

**📦 Dependency updates:**

* Bump Percona Toolkit from 3.3.1 to 3.4.0 (4256c16f9b536d8d878f29b5509add0e9519cc85)
* Bump mariadb-java-client from 2.7.5 to 2.7.6 (aae68925eb6dfa8f3c550129e1a75730a2396cbe)

**📦 Dependency updates:**

- Bump exec-maven-plugin from 3.0.0 to 3.1.0 [\#224](https://github.com/liquibase/liquibase-percona/pull/224) (@dependabot[bot])
- Bump liquibase-core from 4.12.0 to 4.13.0 [\#223](https://github.com/liquibase/liquibase-percona/pull/223) (@dependabot[bot])
- Bump spotbugs-maven-plugin from 4.7.0.0 to 4.7.1.0 [\#222](https://github.com/liquibase/liquibase-percona/pull/222) (@dependabot[bot])
- Bump mariadb-java-client from 3.0.5 to 3.0.6 [\#221](https://github.com/liquibase/liquibase-percona/pull/221) (@dependabot[bot])
- Bump maven from 3.8.5 to 3.8.6 [\#220](https://github.com/liquibase/liquibase-percona/pull/220) (@adangel)

## [v4.12.0](https://github.com/liquibase/liquibase-percona/tree/v4.12.0) (2022-06-23)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.11.0...v4.12.0)

* Support for Liquibase 4.12.0.

**🚀 Implemented enhancements:**

- Add version info to manifest [\#212](https://github.com/liquibase/liquibase-percona/pull/212) (@adangel)

**📦 Dependency updates:**

- Bump liquibase-core from 4.11.0 to 4.12.0 [\#219](https://github.com/liquibase/liquibase-percona/pull/219) (@dependabot[bot])
- Bump tomcat-jdbc from 10.0.21 to 10.0.22 [\#218](https://github.com/liquibase/liquibase-percona/pull/218) (@dependabot[bot])
- Bump docker-maven-plugin from 0.40.0 to 0.40.1 [\#217](https://github.com/liquibase/liquibase-percona/pull/217) (@dependabot[bot])
- Bump maven-enforcer-plugin from 3.0.0 to 3.1.0 [\#216](https://github.com/liquibase/liquibase-percona/pull/216) (@dependabot[bot])
- Bump maven-pmd-plugin from 3.16.0 to 3.17.0 [\#215](https://github.com/liquibase/liquibase-percona/pull/215) (@dependabot[bot])
- Bump docker-maven-plugin from 0.39.1 to 0.40.0 [\#214](https://github.com/liquibase/liquibase-percona/pull/214) (@dependabot[bot])
- Bump maven-invoker-plugin from 3.2.2 to 3.3.0 [\#213](https://github.com/liquibase/liquibase-percona/pull/213) (@dependabot[bot])

## [v4.11.0](https://github.com/liquibase/liquibase-percona/tree/v4.11.0) (2022-05-27)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/v4.10.0...v4.11.0)

* Support for Liquibase 4.11.0

**📦 Dependency updates:**

- Bump spotbugs-maven-plugin from 4.6.0.0 to 4.7.0.0 [\#208](https://github.com/liquibase/liquibase-percona/pull/208) (@dependabot[bot])
- Bump liquibase-core from 4.10.0 to 4.11.0 [\#209](https://github.com/liquibase/liquibase-percona/pull/209) (@dependabot[bot])
- Bump mariadb-java-client from 3.0.4 to 3.0.5 [\#210](https://github.com/liquibase/liquibase-percona/pull/210) (@dependabot[bot])


**📦 Dependency updates:**

- Bump tomcat-jdbc from 10.0.20 to 10.0.21 [\#207](https://github.com/liquibase/liquibase-percona/pull/207) (@dependabot[bot])

## [v4.10.0](https://github.com/liquibase/liquibase-percona/tree/v4.10.0) (2022-05-06)

[Full Changelog](https://github.com/liquibase/liquibase-percona/compare/liquibase-percona-4.9.1...v4.10.0)

* Support for Liquibase 4.10.0

**🚀 Implemented enhancements:**

- Use github\_changelog\_generator [\#205](https://github.com/liquibase/liquibase-percona/pull/205) (@adangel)

**📦 Dependency updates:**

- Bump liquibase-core from 4.9.1 to 4.10.0 [\#204](https://github.com/liquibase/liquibase-percona/pull/204) (@dependabot[bot])
- Bump mysql-connector-java from 8.0.28 to 8.0.29 [\#203](https://github.com/liquibase/liquibase-percona/pull/203) (@dependabot[bot])
- Bump nexus-staging-maven-plugin from 1.6.12 to 1.6.13 [\#202](https://github.com/liquibase/liquibase-percona/pull/202) (@dependabot[bot])
- Bump maven-javadoc-plugin from 3.3.2 to 3.4.0 [\#201](https://github.com/liquibase/liquibase-percona/pull/201) (@dependabot[bot])
- Bump tomcat-jdbc from 10.0.18 to 10.0.20 [\#200](https://github.com/liquibase/liquibase-percona/pull/200) (@dependabot[bot])

## Version 4.9.1 (2022-03-31)

*   Support for Liquibase 4.9.1.
*   [#180](https://github.com/liquibase/liquibase-percona/pull/180): Added support for [MariaDB Connector 3.x](https://mariadb.com/kb/en/about-mariadb-connector-j/).

## Version 4.9.0 (2022-03-18)

*   Support for Liquibase 4.9.0.

## Version 4.8.0 (2022-02-24)

*   Support for Liquibase 4.8.0.
*   [#182](https://github.com/liquibase/liquibase-percona/pull/182): Added masking for slave password - [andreiMambu](https://github.com/andreiMambu)
*   [#183](https://github.com/liquibase/liquibase-percona/issues/183): The parameter --slave-password is not masked in logs

## Version 4.7.1 (2022-01-27)

*   Support for Liquibase 4.7.1.

## Version 4.7.0 (2022-01-13)

*   Support for Liquibase 4.7.0.

## Version 4.6.2 (2021-12-02)

*   Support for Liquibase 4.6.2.

## Version 4.6.1.1 (2021-11-19)

*   [#148](https://github.com/liquibase/liquibase-percona/issues/148): Support createIndex with specifying index prefix length

## Version 4.6.1 (2021-11-06)

*   Support for Liquibase 4.6.1.

## Version 4.5.0 (2021-10-04)

*   Support for Liquibase 4.5.0.

## Version 4.4.3 (2021-08-12)

*   Support for Liquibase 4.4.3.

## Version 4.4.2 (2021-07-23)

*   Support for Liquibase 4.4.2.

## Version 4.4.1 (2021-07-18)

*   Support for Liquibase 4.4.1.

*   [#122](https://github.com/liquibase/liquibase-percona/pull/122): Add docker image with liquibase, liquibase-percona and percona toolkit

## Version 4.4.0 (2021-06-20)

*  Support for Liquibase 4.4.0.

*   [#112](https://github.com/liquibase/liquibase-percona/pull/112): Fixing typos - [Jasper Vandemalle](https://github.com/jasper-vandemalle)
*   [#106](https://github.com/liquibase/liquibase-percona/issues/106): MySQL connection times out after pt-online-schema-change run
*   [#118](https://github.com/liquibase/liquibase-percona/pull/118): Use catalogName instead of schemaName

## Version 4.3.5 (2021-05-24)

*  Support for Liquibase 4.3.5.

## Version 4.3.4 (2021-04-28)

*  Support for Liquibase 4.3.4.

## Version 4.3.3 (2021-04-13)

*  Support for Liquibase 4.3.3.

## Version 4.3.2 (2021-03-26)

*   [#60](https://github.com/liquibase/liquibase-percona/issues/60): Add support for MariaDB JConnector
*   [#85](https://github.com/liquibase/liquibase-percona/issues/85): liquibase-percona 4.3.1 is not reproducible anymore
*   [#88](https://github.com/liquibase/liquibase-percona/pull/88): Support for Liquibase 4.3.2

## Version 4.3.1 (2021-02-23)

*   The maven coordinates have changed. This extension is now available like the other liquibase extensions in
    the group `org.liquibase.ext`.

    In order to add this extension, use the following snippet:

        <dependency>
            <groupId>org.liquibase.ext</groupId>
            <artifactId>liquibase-percona</artifactId>
            <version>4.3.1</version>
        </dependency>


*   [#66](https://github.com/liquibase/liquibase-percona/issues/66): Change maven coordinates to be org.liquibase.ext
*   [#74](https://github.com/liquibase/liquibase-percona/pull/74): Update Liquibase to 4.3.0
*   Support for Liquibase 4.3.1
*   Alignment with existing release process

## Version 2.0.0 (2021-02-04)

*   The minimum Java runtime version is now Java 1.8.
*   Liquibase 4+ is supported.
*   Support for older liquibase versions has been dropped.
*   The XML namespace for this extension is now "http://www.liquibase.org/xml/ns/dbchangelog-ext/liquibase-percona".

    This only is affecting you, if you use the [UsePercona flag](README.md#usepercona-flag).

    There is now also a XSD schema available, if you want to validate your XML changeset:
    <https://raw.githubusercontent.com/liquibase/liquibase-percona/liquibase-percona-2.0.0/src/main/resources/dbchangelog-ext-liquibase-percona.xsd>

    See the file [test-changelog.xml](https://github.com/liquibase/liquibase-percona/blob/master/src/test/resources/liquibase/ext/percona/changelog/test-changelog.xml) for an example.

    Note: Usage of the schema is optional. In order to use the custom flags provided by this extension, you
    only need to declare the namespace.
*   The checksum calculation for changes that used [UsePercona flag](README.md#usepercona-flag) changed. You might
    need to recreate the changelog entries for these changes with the
    [clearCheckSums](https://docs.liquibase.com/commands/community/clearchecksums.html) command.
    See [#64](https://github.com/liquibase/liquibase-percona/issues/64) for the explanation.

*   Fixed [#56](https://github.com/liquibase/liquibase-percona/issues/56): Support liquibase 4.x
*   Fixed [#57](https://github.com/liquibase/liquibase-percona/issues/57): Support perconaOptions per change
*   Fixed [#64](https://github.com/liquibase/liquibase-percona/issues/64): Different changeset checksums with and without liquibase-percona
*   Fixed [#65](https://github.com/liquibase/liquibase-percona/issues/65): Make build reproducible

## Version 1.7.1 (2021-01-28)

*   Fixed [#58](https://github.com/liquibase/liquibase-percona/pull/58): Update versions (liquibase, percona-toolkit, mysql)

## Version 1.7.0 (2020-07-04)

*   Fixed [#35](https://github.com/liquibase/liquibase-percona/issues/35): Add support for AddPrimaryKeyChange
*   Fixed [#37](https://github.com/liquibase/liquibase-percona/issues/37): Using quotes for liquibase.percona.options doesn't always work
*   Fixed [#53](https://github.com/liquibase/liquibase-percona/issues/53): Update to support latest liquibase 3.10.1
*   Fixed [#54](https://github.com/liquibase/liquibase-percona/issues/54): Update mysql-connector-java to 8.0.20
*   Fixed [#55](https://github.com/liquibase/liquibase-percona/issues/55): Update percona toolkit to 3.2.0

## Version 1.6.0 (2019-04-20)

The minimum Java runtime version is now Java 1.7.

The system property `liquibase.percona.options` uses now a default value of `--alter-foreign-keys-method=auto --nocheck-unique-key-change`.
These two options are **not** added by default anymore when pt-osc is executed. They are added
now via the additional options system property. In case you have overridden this system property, make sure, to add
these options as well, if you need them.

*   Fixed [#29](https://github.com/liquibase/liquibase-percona/issues/29): Allow to override --nocheck-unique-key-changes and --alter-foreign-keys-method=auto
*   Fixed [#30](https://github.com/liquibase/liquibase-percona/issues/30): Update liquibase

## Version 1.5.2 (2019-04-14)

*   Fixed [#28](https://github.com/liquibase/liquibase-percona/issues/28): Strange behavior when liquibase.percona.defaultOn is false

## Version 1.5.1 (2018-11-10)

*   Fixed [#26](https://github.com/liquibase/liquibase-percona/issues/26): Stack Overflow using defaultOn=false System Property
*   [#27](https://github.com/liquibase/liquibase-percona/pull/27): fix a typo - [kennethinsnow](https://github.com/kennethinsnow)

## Version 1.5.0 (2018-09-30)

`pt-online-schema-change` is executed now with the option `--nocheck-unique-key-change`.
This enables to add unique indexes, but can cause data loss, since duplicated rows are ignored.
See [Percona Toolkit Documentation](https://www.percona.com/doc/percona-toolkit/LATEST/pt-online-schema-change.html#id7)
for more information.

The plugin is only compatible with version 3.0.12 or later of Percona Toolkit.

*   Upgraded liquibase to 3.5.5
*   Verified compatibility to liquibase 3.6.2
*   Fixed [#14](https://github.com/liquibase/liquibase-percona/issues/14): Rollback of foreign key constraint changing constraint names problem
*   Fixed [#15](https://github.com/liquibase/liquibase-percona/issues/15): Unique key constraint cannot be added
*   Fixed [#20](https://github.com/liquibase/liquibase-percona/issues/20): Support "UsePercona flag" in XML changelogs
*   Fixed [#22](https://github.com/liquibase/liquibase-percona/issues/22): Cross database bug

## Version 1.4.1 (2018-09-27)

*   Fixed [#16](https://github.com/liquibase/liquibase-percona/issues/16): Failing test PerconaAddForeignKeyConstraintChangeTest
*   Fixed [#17](https://github.com/liquibase/liquibase-percona/issues/17): Include Percona Toolkit into integration test
*   Fixed [#18](https://github.com/liquibase/liquibase-percona/issues/18): Use spotbugs instead of findbugs
*   Fixed [#19](https://github.com/liquibase/liquibase-percona/issues/19): Upgrade liquibase to 3.5.4
*   Fixed [#21](https://github.com/liquibase/liquibase-percona/issues/21): Couldn't determine password: JdbcConnection is unsupported: dbcp.PoolingDataSource$PoolGuardConnectionWrapper
*   Fixed [#23](https://github.com/liquibase/liquibase-percona/pull/23): Add support for dbcp2
*   Added new system property `liquibase.percona.path` to specify the path where Percona Toolkit is installed.

## Version 1.4.0 (2017-07-21)

*   Fixed [#13](https://github.com/liquibase/liquibase-percona/issues/13): Use default liquibase.properties as fallback

## Version 1.3.1 (2017-07-21)

*   Fixed [#12](https://github.com/liquibase/liquibase-percona/issues/12): Cannot run migrations with the percona extension on a Spring Boot app with embedded Tomcat

## Version 1.3.0 (2016-12-18)

*   Upgraded liquibase to 3.5.3
*   Support for MySQL Connector 6.0.x in addition to 5.1.x.
*   Fixed [#7](https://github.com/liquibase/liquibase-percona/issues/7): Foreign key constraints of AddColumn is ignored
*   Fixed [#8](https://github.com/liquibase/liquibase-percona/issues/8): Support addForeignKeyConstraintChange, addUniqueConstraintChange
*   Fixed [#9](https://github.com/liquibase/liquibase-percona/issues/9): Support for enabling pt-online-schema-changes on a per-change basis
*   Fixed [#10](https://github.com/liquibase/liquibase-percona/issues/10): Build fails with java7: UnsupportedClassVersion when running DatabaseConnectionUtilTest.testGetPasswordMySQL\_6

## Version 1.2.1 (2016-09-13)

*   [#4](https://github.com/liquibase/liquibase-percona/pull/4): Allow passing additional command line options to pt-online-schema-change
*   [#5](https://github.com/liquibase/liquibase-percona/pull/5): Support afterColumn attribute

## Version 1.2.0 (2016-04-02)

*   Fixed [#2](https://github.com/liquibase/liquibase-percona/issues/2): Adding indexes via pt-online-schema-change
*   Fixed [#3](https://github.com/liquibase/liquibase-percona/issues/3): Altering column data types via pt-online-schema-change
*   Added configuration property "liquibase.percona.skipChanges"
*   Upgraded liquibase to 3.4.2

## Version 1.1.1 (2015-07-26)

*   Fixed [#1](https://github.com/liquibase/liquibase-percona/issues/1): Tables with foreign keys

## Version 1.1.0 (2014-11-06)

*   Initial version compatible with liquibase 3.3.0

## Version 1.0.0 (2014-10-09)

*   Initial version compatible with liquibase 3.2.0


\* *This Changelog was automatically generated by [github_changelog_generator](https://github.com/github-changelog-generator/github-changelog-generator)*
