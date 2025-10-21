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
package org.exbin.framework.options.settings;

import com.formdev.flatlaf.extras.FlatDesktop;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.SeparationSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.options.settings.api.SettingsPanelType;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.options.settings.action.SettingsAction;
import org.exbin.framework.utils.DesktopUtils;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;

/**
 * Implementation of framework options settings module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsSettingsModule implements OptionsSettingsModuleApi {

    public static final String OPTIONS_PANEL_KEY = "options";
    public static final String OPTIONS_GROUP_PREFIX = "optionsGroup.";

    private ResourceBundle resourceBundle;

    private SettingsPanelType settingsPanelType = SettingsPanelType.TREE;
    private OptionsSetingsManager optionsSettingsManager;
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

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    @Override
    public OptionsSetingsManager getMainSettingsManager() {
        if (optionsSettingsManager == null) {
            optionsSettingsManager = new OptionsSetingsManager();
        }

        return optionsSettingsManager;
    }

    @Nonnull
    @Override
    public SettingsAction createSettingsAction() {
        ensureSetup();
        SettingsAction optionsAction = new SettingsAction();
        getMainSettingsManager();
        optionsAction.setup(resourceBundle, (SettingsPageReceiver optionsPageReceiver) -> {
            getMainSettingsManager().passSettingsPages(optionsPageReceiver);
        });

        return optionsAction;
    }

    @Override
    public void notifyOptionsChanged() {
        // TODO Remove
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        frameModule.notifyFrameUpdated();
    }

    @Override
    public void initialLoadFromPreferences() {
        // TODO
//        getMainSettingsManager().initialLoadFromPreferences();
//        notifyOptionsChanged();
    }

    @Override
    public OptionsSettingsManagement getSettingsManagement(String moduleId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

/* @Nonnull
    @Override
    public OptionsGroup createOptionsGroup(String groupId, ResourceBundle resourceBundle) {
        String groupName = resourceBundle.getString(OPTIONS_GROUP_PREFIX + groupId + ".name");
        BasicOptionsGroup optionsGroup = new BasicOptionsGroup(groupId, groupName);
        return optionsGroup;
    } */

    @Override
    public void registerMenuAction() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        SettingsAction optionsAction = createSettingsAction();

        boolean optionsActionRegistered = false;
        if (DesktopUtils.detectBasicOs() == DesktopUtils.OsType.MACOSX) {
            FlatDesktop.setPreferencesHandler(() -> {
                optionsAction.actionPerformed(null);
            });
            /* // TODO: Replace after migration to Java 9+
            Desktop desktop = Desktop.getDesktop();
            desktop.setPreferencesHandler((e) -> {
                optionsAction.actionPerformed(null);
            }); */
            optionsActionRegistered = true;
        }
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.TOOLS_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(TOOLS_OPTIONS_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM_LAST));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(optionsActionRegistered ? SeparationSequenceContributionRule.SeparationMode.NONE : SeparationSequenceContributionRule.SeparationMode.AROUND));
        if (!optionsActionRegistered) {
            contribution = mgmt.registerMenuItem(optionsAction);
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
}
