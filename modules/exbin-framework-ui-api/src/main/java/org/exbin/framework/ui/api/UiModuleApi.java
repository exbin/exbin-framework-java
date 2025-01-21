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
package org.exbin.framework.ui.api;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.options.api.OptionsPage;

/**
 * Interface for framework UI module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface UiModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(UiModuleApi.class);

    /**
     * Register look and feel provider.
     *
     * @param lafProvider look and feel provider
     */
    void registerLafPlugin(LafProvider lafProvider);

    /**
     * Returns list of available look and feel providers.
     *
     * @return list of look and feel providers
     */
    @Nonnull
    List<LafProvider> getLafProviders();

    /**
     * Initialize UI. Should be called before any GUI is created.
     */
    void initSwingUi();

    /**
     * Registers post UI initialization action.
     *
     * @param runnable runnable action
     */
    void addPostInitAction(Runnable runnable);

    /**
     * Registers options panels.
     */
    void registerOptionsPanels();

    /**
     * Extends main options panel.
     *
     * @param optionsPage options panel
     */
    void extendMainOptionsPage(OptionsPage<?> optionsPage);

    /**
     * Extends appearance options panel.
     *
     * @param optionsPage options panel
     */
    void extendAppearanceOptionsPage(OptionsPage<?> optionsPage);
}
