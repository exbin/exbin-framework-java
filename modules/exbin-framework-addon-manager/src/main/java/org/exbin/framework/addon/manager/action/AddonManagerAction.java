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
package org.exbin.framework.addon.manager.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.DialogParentComponent;
import org.exbin.framework.addon.manager.AddonManager;
import org.exbin.framework.addon.manager.AddonManagerModule;
import org.exbin.framework.addon.manager.AddonsManagerTab;
import org.exbin.framework.addon.manager.InstalledManagerTab;
import org.exbin.framework.addon.manager.api.AddonManagerModuleApi;
import org.exbin.framework.addon.manager.api.AddonManagerTab;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.addon.manager.gui.AddonManagerPanel;
import org.exbin.framework.addon.manager.gui.AddonsControlPanel;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.context.api.ContextChangeRegistration;
import org.exbin.framework.help.api.HelpLink;

/**
 * Addon manager action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonManagerAction extends AbstractAction {

    public static final String ACTION_ID = "addonManagerAction";
    public static final String HELP_ID = "addon-manager";

    private java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonManagerAction.class);
    private DialogParentComponent dialogParentComponent;

    public AddonManagerAction() {
        init();
    }

    private void init() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ContextChangeRegistration registrar) -> {
            registrar.registerUpdateListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
                dialogParentComponent = instance;
                setEnabled(instance != null);
            });
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: Extract to separate class to not share fields
        AddonsControlPanel controlPanel = new AddonsControlPanel();
        controlPanel.addHelpButton(new HelpLink(HELP_ID));

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        AddonManagerPanel addonManagerPanel = new AddonManagerPanel();
        addonManagerPanel.setPreferredSize(new Dimension(800, 500));
        addonManagerPanel.setController(new AddonManagerPanel.Controller() {
            @Override
            public void tabSwitched(AddonManagerTab managerTab) {
                if (managerTab instanceof AddonsManagerTab) {
                    controlPanel.setOperationCount(((AddonsManagerTab) managerTab).getToInstallCount());
                } else if (managerTab instanceof InstalledManagerTab) {
                    controlPanel.setOperationCount(((InstalledManagerTab) managerTab).getToUpdateCount());
                } else {
                    throw new IllegalStateException();
                }
            }
        });

        AddonManagerModuleApi addonManagerModule = App.getModule(AddonManagerModuleApi.class);
        AddonManager addonManager = ((AddonManagerModule) addonManagerModule).getAddonManager();

        AddonsManagerTab addonsManagerTab = new AddonsManagerTab();
        addonsManagerTab.setAddonManager(addonManager);
        addonManagerPanel.addManagerTab(addonsManagerTab);

        InstalledManagerTab installedManagerTab = new InstalledManagerTab();
        installedManagerTab.setAddonManager(addonManager);
        addonManagerPanel.addManagerTab(installedManagerTab);

        final WindowHandler dialog = windowModule.createDialog(addonManagerPanel, controlPanel);
        controlPanel.setController(new AddonsControlPanel.Controller() {
            @Override
            public void performOpenCart() {
                AddonManagerTab managerTab = addonManagerPanel.getActiveTab();
                if (managerTab instanceof AddonsManagerTab) {
                    ((AddonsManagerTab) managerTab).installAddons();
                } else if (managerTab instanceof InstalledManagerTab) {
                    ((InstalledManagerTab) managerTab).updateAddons();
                } else {
                    throw new IllegalStateException();
                }
            }

            @Override
            public void controlActionPerformed() {
                dialog.close();
            }
        });

        windowModule.addHeaderPanel(dialog.getWindow(), addonManagerPanel.getClass(), addonManagerPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, addonManagerPanel.getResourceBundle());
        dialog.showCentered(dialogParentComponent.getComponent());
        dialog.dispose();
    }
}
