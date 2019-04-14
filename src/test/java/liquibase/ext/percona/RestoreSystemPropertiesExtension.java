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

import java.util.Properties;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

public class RestoreSystemPropertiesExtension implements BeforeEachCallback, AfterEachCallback {
    private static final String ORIGINAL_PROPERTIES_KEY = "originalProperties";

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Properties originalProperties = System.getProperties();
        getStore(context).put(ORIGINAL_PROPERTIES_KEY, originalProperties);
        Properties copy = new Properties();
        copy.putAll(originalProperties);
        System.setProperties(copy);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        Properties originalProperties = getStore(context).remove(ORIGINAL_PROPERTIES_KEY, Properties.class);
        System.setProperties(originalProperties);
    }

    private Store getStore(ExtensionContext context) {
        Namespace namespace = Namespace.create(getClass(), context.getRequiredTestMethod());
        return context.getStore(namespace);
    }
}
