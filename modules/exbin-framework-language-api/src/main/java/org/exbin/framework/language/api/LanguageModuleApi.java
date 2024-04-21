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
package org.exbin.framework.language.api;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface for framework language module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface LanguageModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(LanguageModuleApi.class);

    /**
     * Returns applications's language resource bundle.
     *
     * @return resource bundle
     */
    @Nonnull
    ResourceBundle getAppBundle();

    /**
     * Sets application resource bundle handler.
     *
     * @param appBundle application resource bundle
     */
    void setAppBundle(ResourceBundle appBundle);

    /**
     * Returns resource bundle for properties file with path derived from class
     * name.
     *
     * @param targetClass target class
     * @return resource bundle
     */
    @Nonnull
    ResourceBundle getBundle(Class<?> targetClass);

    /**
     * Returns resource bundle for properties file with path derived from bundle
     * name.
     *
     * @param bundleName bundle name
     * @return resource bundle
     */
    @Nonnull
    ResourceBundle getResourceBundleByBundleName(String bundleName);

    /**
     * Enhances action title to indicate action which is opening dialog.
     *
     * @param actionTitle action title
     * @return enhanced action title
     */
    @Nonnull
    String getActionWithDialogText(String actionTitle);

    /**
     * Enhances action title to indicate action which is opening dialog.
     *
     * @param bundle resource bundle
     * @param key resource key
     * @return enhanced action title
     */
    @Nonnull
    String getActionWithDialogText(ResourceBundle bundle, String key);

    /**
     * Registers language provider plugin.
     *
     * @param languageProvider language provider
     */
    void registerLanguagePlugin(LanguageProvider languageProvider);

    /**
     * Returns registered language plugins.
     *
     * @return language providers
     */
    @Nonnull
    List<LanguageProvider> getLanguagePlugins();

    /**
     * Switches application to given language if available.
     *
     * @param locale language locale
     */
    void switchToLanguage(Locale locale);
}
