/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.language;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Language module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class LanguageModule implements LanguageModuleApi {

    private java.util.ResourceBundle resourceBundle = null;

    private ResourceBundle appBundle;
    private ClassLoader languageClassLoader = null;

    public LanguageModule() {
    }

    @Nonnull
    @Override
    public ResourceBundle getAppBundle() {
        if (appBundle == null) {
            throw new IllegalStateException("Language was not initialized");
        }
        return appBundle;
    }

    @Override
    public void setAppBundle(ResourceBundle appBundle) {
        this.appBundle = appBundle;
    }
    
    @Nonnull
    @Override
    public ResourceBundle getBundle(Class<?> targetClass) {
        if (languageClassLoader == null) {
            return ResourceBundle.getBundle(getResourceBaseNameBundleByClass(targetClass));
        } else {
            return new LanguageResourceBundle(getResourceBaseNameBundleByClass(targetClass));
        }
    }

    @Nullable
    public ClassLoader getLanguageClassLoader() {
        return languageClassLoader;
    }

    public void setLanguageClassLoader(ClassLoader languageClassLoader) {
        this.languageClassLoader = languageClassLoader;
    }

    /**
     * Returns class name path.
     *
     * Result is canonical name with dots replaced with slashes.
     *
     * @param targetClass target class
     * @return name path
     */
    @Nonnull
    public static String getClassNamePath(Class<?> targetClass) {
        return targetClass.getCanonicalName().replace(".", "/");
    }

    /**
     * Returns resource bundle base name for properties file with path derived
     * from class name.
     *
     * @param targetClass target class
     * @return base name string
     */
    @Nonnull
    public static String getResourceBaseNameBundleByClass(Class<?> targetClass) {
        String classNamePath = getClassNamePath(targetClass);
        int classNamePos = classNamePath.lastIndexOf("/");
        return classNamePath.substring(0, classNamePos + 1) + "resources" + classNamePath.substring(classNamePos);
    }

    /**
     * Resource bundle which looks for language resources first and main
     * resources as fallback.
     */
    @ParametersAreNonnullByDefault
    private class LanguageResourceBundle extends ResourceBundle {

        private final ResourceBundle mainResourceBundle;
        private final ResourceBundle languageResourceBundle;

        public LanguageResourceBundle(String baseName) {
            mainResourceBundle = ResourceBundle.getBundle(baseName);
            languageResourceBundle = ResourceBundle.getBundle(baseName, Locale.getDefault(), languageClassLoader);
        }

        @Nullable
        @Override
        protected Object handleGetObject(String key) {
            Object object = languageResourceBundle.getObject(key);
            if (object == null) {
                object = mainResourceBundle.getObject(key);
            }

            return object;
        }

        @Nonnull
        @Override
        public Enumeration<String> getKeys() {
            Set<String> keys = new HashSet<>();
            keys.addAll(Collections.list(languageResourceBundle.getKeys()));
            keys.addAll(Collections.list(mainResourceBundle.getKeys()));
            return Collections.enumeration(keys);
        }
    }
}
