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
    private static final String VERSION = "4.31.0";

    @Test
    void liquibaseUtilVersion() {
        String buildInfo = String.format("buildVersion=%s buildNumber=%s buildTime=%s buildVersionInfo=%s",
                LiquibaseUtil.getBuildVersion(), LiquibaseUtil.getBuildNumber(),
                LiquibaseUtil.getBuildTime(), LiquibaseUtil.getBuildVersionInfo());
        assertEquals(String.format("buildVersion=%1$s buildNumber=6261 buildTime=2025-01-14 14:24+0000 buildVersionInfo=%1$s", VERSION),
                buildInfo);
    }

    @Test
    void liquibaseCoreJar() throws URISyntaxException, IOException, InterruptedException {
        URL liquibaseCoreLocation = LiquibaseUtil.class.getProtectionDomain().getCodeSource().getLocation();
        assertTrue(liquibaseCoreLocation.toString().endsWith(String.format("liquibase-core-%s.jar", VERSION)),
                "Unexpected file path: " + liquibaseCoreLocation);
        Path liquibaseCore = Paths.get(liquibaseCoreLocation.toURI());
        assertTrue(Files.exists(liquibaseCore), "File doesn't exist: " + liquibaseCore);

        Process process = new ProcessBuilder("md5sum", liquibaseCore.toString()).start();
        String md5sum = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8).substring(0, 32);
        process.waitFor(10, TimeUnit.SECONDS);

        String md5sumCentral = IOUtils.toString(URI.create(
                String.format("https://repo.maven.apache.org/maven2/org/liquibase/liquibase-core/%1$s/liquibase-core-%1$s.jar.md5", VERSION)),
                StandardCharsets.UTF_8);

        assertAll(
                () -> assertEquals(3155816L, Files.size(liquibaseCore), "Unexpected file size"),
                () -> assertEquals("88825aab27786bd6d28f770fe0e3f4c8", md5sum, "Unexpected checksum"),
                () -> assertEquals(md5sumCentral, md5sum)
        );
    }
}
