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
package org.exbin.framework.options;

import com.formdev.flatlaf.extras.FlatDesktop;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsPanelType;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.OptionsPageReceiver;
import org.exbin.framework.options.api.OptionsPage;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.menu.GroupMenuContributionRule;
import org.exbin.framework.action.api.menu.MenuContribution;
import org.exbin.framework.action.api.menu.MenuManagement;
import org.exbin.framework.action.api.menu.PositionMenuContributionRule;
import org.exbin.framework.action.api.menu.SeparationMenuContributionRule;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.options.action.OptionsAction;
import org.exbin.framework.options.api.OptionsPageManagement;
import org.exbin.framework.options.api.OptionsPageRule;
import org.exbin.framework.utils.DesktopUtils;

/**
 * Implementation of framework options module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsModule implements OptionsModuleApi {

    private ResourceBundle resourceBundle;

    private OptionsPanelType optionsPanelType = OptionsPanelType.TREE;
    private OptionsPageManager optionsPageManager;

    public OptionsModule() {
    }

    @Nonnull
    private ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(OptionsModule.class);
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
    public OptionsAction createOptionsAction() {
        ensureSetup();
        OptionsAction optionsAction = new OptionsAction();
        optionsAction.setup(resourceBundle, (OptionsPageReceiver optionsTreePanel) -> {
            getOptionsPageManager().passOptionsPages(optionsTreePanel);
        });

        return optionsAction;
    }

    @Override
    public void notifyOptionsChanged() {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        frameModule.notifyFrameUpdated();
    }

    @Override
    public void initialLoadFromPreferences() {
        getOptionsPageManager().initialLoadFromPreferences();
        notifyOptionsChanged();
    }

    @Nonnull
    public OptionsPageManager getOptionsPageManager() {
        if (optionsPageManager == null) {
            optionsPageManager = new OptionsPageManager();
        }

        return optionsPageManager;
    }

    @Nonnull
    @Override
    public OptionsPageManagement getOptionsPageManagement(String moduleId) {
        return new OptionsPageManagement() {
            @Override
            public void registerOptionsPage(OptionsPage<?> optionsPage) {
                getOptionsPageManager().registerOptionsPage(optionsPage);
            }

            @Override
            public void registerOptionsPageRule(OptionsPage<?> optionsPage, OptionsPageRule optionsPageRule) {
                getOptionsPageManager().registerOptionsPageRule(optionsPage, optionsPageRule);
            }
        };
    }

    @Override
    public void registerMenuAction() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        OptionsAction optionsAction = createOptionsAction();

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
        MenuManagement mgmt = actionModule.getMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuGroup(ActionConsts.TOOLS_MENU_ID, TOOLS_OPTIONS_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMode.BOTTOM_LAST));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(optionsActionRegistered ? SeparationMode.NONE : SeparationMode.AROUND));
        if (!optionsActionRegistered) {
            contribution = mgmt.registerMenuItem(ActionConsts.TOOLS_MENU_ID, optionsAction);
            mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(TOOLS_OPTIONS_MENU_GROUP_ID));
        }
    }

    @Nonnull
    @Override
    public OptionsPanelType getOptionsPanelType() {
        return optionsPanelType;
    }

    @Override
    public void setOptionsPanelType(OptionsPanelType optionsPanelType) {
        this.optionsPanelType = optionsPanelType;
    }
}
