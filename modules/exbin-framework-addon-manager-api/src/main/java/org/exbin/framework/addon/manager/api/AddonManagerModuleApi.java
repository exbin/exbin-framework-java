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
package org.exbin.framework.addon.manager.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface of the addon manager module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface AddonManagerModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(AddonManagerModuleApi.class);

    @Nonnull
    Action createAddonManagerAction();

    @Nonnull
    String getAddonServiceUrl();

    @Nonnull
    String getManualLegacyUrl();

    @Nonnull
    String getAddonServiceCoreUrl();

    void setAddonServiceCoreUrl(String addonServiceCoreUrl);

    @Nonnull
    String getManualLegacyGitHubUrl();

    void setManualLegacyGitHubUrl(String manualLegacyGitHubUrl);

    void registerAddonManagerMenuItem();

    boolean isDevMode();

    void setDevMode(boolean devMode);

    void registerOptionsPanels();
}
