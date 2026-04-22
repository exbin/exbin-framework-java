/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.options.settings;

import com.formdev.flatlaf.extras.FlatDesktop;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.context.api.ContextModuleApi;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SeparationSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsPanelType;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.options.settings.action.SettingsAction;
import org.exbin.jaguif.utils.DesktopUtils;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.SettingsOptionsOverrides;
import org.exbin.jaguif.options.settings.api.SettingsOptionsProvider;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.jaguif.options.settings.contribution.SettingsContribution;

/**
 * Implementation of framework options settings module.
 */
@ParametersAreNonnullByDefault
public class OptionsSettingsModule implements OptionsSettingsModuleApi {

    public static final String OPTIONS_PANEL_KEY = "options";
    public static final String OPTIONS_GROUP_PREFIX = "optionsGroup.";

    private ResourceBundle resourceBundle;

    private SettingsPanelType settingsPanelType = SettingsPanelType.TREE;
    private OptionsSettingsManager optionsSettingsManager;
    private String optionsRootCaption = null;

    public OptionsSettingsModule() {
    }

    @Nonnull
    private ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(OptionsSettingsModule.class);
        }

        return resourceBundle;
    }

    @Nonnull
    @Override
    public OptionsSettingsManager getMainSettingsManager() {
        if (optionsSettingsManager == null) {
            optionsSettingsManager = new OptionsSettingsManager();
        }

        return optionsSettingsManager;
    }

    @Nonnull
    @Override
    public SettingsAction createSettingsAction() {
        SettingsAction optionsAction = new SettingsAction();
        getMainSettingsManager();
        optionsAction.init(getResourceBundle(), (SettingsPageReceiver optionsPageReceiver) -> {
            getMainSettingsManager().passSettingsPages(optionsPageReceiver);
        });

        return optionsAction;
    }

    @Override
    public void initialLoadFromPreferences() {
        ContextModuleApi contextModule = App.getModule(ContextModuleApi.class);
        OptionsSettingsManager mainSettingsManager = getMainSettingsManager();
        SettingsOptionsProvider settingsOptionsProvider = mainSettingsManager.getSettingsOptionsProvider();
        ActiveContextManagement mainContextManager = contextModule.getMainContextManager();
        mainSettingsManager.applyAllOptions(mainContextManager, settingsOptionsProvider);
    }

    @Override
    public OptionsSettingsManagement getSettingsManagement(String moduleId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerMenuAction() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);

        boolean optionsActionRegistered = false;
        if (DesktopUtils.detectBasicOs() == DesktopUtils.OsType.MACOSX) {
            FlatDesktop.setPreferencesHandler(() -> {
                FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
                createSettingsAction().openSettingsDialog(frameModule.getFrame());
            });
            /* // TODO: Replace after migration to Java 9+
            Desktop desktop = Desktop.getDesktop();
            desktop.setPreferencesHandler((e) -> {
                FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
                optionsAction.openSettingsDialog(frameModule.getFrame());
            }); */
            optionsActionRegistered = true;
        }
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.TOOLS_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(TOOLS_OPTIONS_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM_LAST));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(optionsActionRegistered ? SeparationSequenceContributionRule.SeparationMode.NONE : SeparationSequenceContributionRule.SeparationMode.AROUND));
        if (!optionsActionRegistered) {
            contribution = new SettingsContribution();
            mgmt.registerMenuContribution(contribution);
            mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(TOOLS_OPTIONS_MENU_GROUP_ID));
        }
    }

    @Nonnull
    @Override
    public SettingsPanelType getSettingsPanelType() {
        return settingsPanelType;
    }

    @Override
    public void setSettingsPanelType(SettingsPanelType settingsPanelType) {
        this.settingsPanelType = settingsPanelType;
    }

    @Nonnull
    @Override
    public Optional<String> getOptionsRootCaption() {
        return Optional.ofNullable(optionsRootCaption);
    }

    @Override
    public void setOptionsRootCaption(@Nullable String optionsRootCaption) {
        this.optionsRootCaption = optionsRootCaption;
    }

    @Nonnull
    @Override
    public SettingsOptionsOverrides createSettingsOptionsOverrides(SettingsOptionsProvider settingsOptionsProvider) {
        return new DefaultSettingsOptionsOverrides(settingsOptionsProvider);
    }
}
