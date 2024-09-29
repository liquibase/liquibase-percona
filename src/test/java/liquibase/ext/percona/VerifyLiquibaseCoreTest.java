package liquibase.ext.percona;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import liquibase.util.LiquibaseUtil;

class VerifyLiquibaseCoreTest {

    @Test
    void liquibaseUtilVersion() {
        String buildInfo = String.format("buildVersion=%s buildNumber=%s buildTime=%s buildVersionInfo=%s",
                LiquibaseUtil.getBuildVersion(), LiquibaseUtil.getBuildNumber(),
                LiquibaseUtil.getBuildTime(), LiquibaseUtil.getBuildVersionInfo());
        assertEquals("buildVersion=4.29.2 buildNumber=3683 buildTime=2024-08-29 16:45+0000 buildVersionInfo=4.29.2",
                buildInfo);
    }

    @Test
    void liquibaseCoreJar() throws URISyntaxException, IOException, InterruptedException {
        URL liquibaseCoreLocation = LiquibaseUtil.class.getProtectionDomain().getCodeSource().getLocation();
        assertTrue(liquibaseCoreLocation.toString().endsWith("liquibase-core-4.29.2.jar"), "Unexpected file path: " + liquibaseCoreLocation);
        Path liquibaseCore = Paths.get(liquibaseCoreLocation.toURI());
        assertTrue(Files.exists(liquibaseCore), "File doesn't exist: " + liquibaseCore);

        Process process = new ProcessBuilder("md5sum", liquibaseCore.toString()).start();
        String md5sum = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8).substring(0, 32);
        process.waitFor(10, TimeUnit.SECONDS);

        String md5sumCentral = IOUtils.toString(URI.create("https://repo.maven.apache.org/maven2/org/liquibase/liquibase-core/4.29.2/liquibase-core-4.29.2.jar.md5"),
                StandardCharsets.UTF_8);

        assertAll(
                () -> assertEquals(2884238L, Files.size(liquibaseCore), "Unexpected file size"),
                () -> assertEquals("82d2385a0349310b2c6c994b6d5add13", md5sum, "Unexpected checksum"),
                () -> assertEquals(md5sumCentral, md5sum)
        );
    }
}
