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
package org.exbin.framework.language.api.utils;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import org.exbin.framework.language.api.*;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Test implementation of language module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TestLanguageModule implements LanguageModuleApi {

    private ResourceBundle appBundle;

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
        return ResourceBundle.getBundle(getResourceBaseNameBundleByClass(targetClass));
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundleByBundleName(String bundleName) {
        return ResourceBundle.getBundle(bundleName);
    }

    @Nonnull
    @Override
    public String getActionWithDialogText(String actionTitle) {
        return actionTitle + "...";
    }

    @Nonnull
    @Override
    public String getActionWithDialogText(ResourceBundle bundle, String key) {
        return bundle.getString(key) + "...";
    }

    @Override
    public void registerLanguagePlugin(LanguageProvider languageProvider) {
        throw new IllegalStateException();
    }

    @Nonnull
    @Override
    public List<LanguageProvider> getLanguagePlugins() {
        throw new IllegalStateException();
    }

    @Override
    public void switchToLanguage(Locale locale) {
        throw new IllegalStateException();
    }

    /**
     * Returns class name path.
     * <br>
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
}
