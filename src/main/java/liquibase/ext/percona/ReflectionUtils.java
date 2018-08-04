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

import java.lang.reflect.Method;

public class ReflectionUtils {

    public static Class<?> loadClass(String name, ClassLoader loader) {
        try {
            return loader.loadClass(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static <T> T invokeMethod(String className, Object instance, String methodName) {
        Class<?> clazz = loadClass(className, instance.getClass().getClassLoader());
        return invokeMethod(clazz, instance, methodName);
    }

    public static <T> T invokeMethod(Class<?> clazz, Object instance, String methodName) {
        try {
            if (clazz != null && clazz.isInstance(instance)) {
                Method method = findMethod(clazz, methodName);
                method.setAccessible(true);

                @SuppressWarnings("unchecked")
                T result = (T) method.invoke(instance);
                return result;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Method findMethod(Class<?> clazz, String name) throws NoSuchMethodException, SecurityException {
        try {
            return clazz.getMethod(name);
        } catch (NoSuchMethodException e) {
            return clazz.getDeclaredMethod(name);
        }
    }
}
