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

    @Nonnull
    ResourceBundle getAppBundle();

    /**
     * Sets application resource bundle handler.
     *
     * @param appBundle application resource bundle
     */
    void setAppBundle(ResourceBundle appBundle);

    @Nonnull
    ResourceBundle getBundle(Class<?> targetClass);

    @Nonnull
    String getActionWithDialogText(String actionTitle);

    @Nonnull
    String getActionWithDialogText(ResourceBundle bundle, String key);

    void registerLanguagePlugin(LanguageProvider languageProvider);

    @Nonnull
    List<LanguageProvider> getLanguagePlugins();
}
