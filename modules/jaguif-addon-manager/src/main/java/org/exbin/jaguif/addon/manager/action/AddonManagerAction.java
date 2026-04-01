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
package org.exbin.jaguif.addon.manager.action;

import java.awt.event.ActionEvent;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.DialogParentComponent;
import org.exbin.jaguif.addon.manager.AddonManager;
import org.exbin.jaguif.addon.manager.AddonManagerModule;
import org.exbin.jaguif.addon.manager.api.AddonManagerModuleApi;
import org.exbin.jaguif.window.api.WindowModuleApi;
import org.exbin.jaguif.addon.manager.gui.AddonsManagerPanel;
import org.exbin.jaguif.addon.manager.gui.AddonsManagerControlPanel;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.window.api.WindowHandler;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.help.api.HelpLink;

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
        AddonsManagerControlPanel controlPanel = new AddonsManagerControlPanel();
        controlPanel.addHelpButton(new HelpLink(HELP_ID));

        AddonManagerModuleApi addonManagerModule = App.getModule(AddonManagerModuleApi.class);
        AddonManager addonManager = ((AddonManagerModule) addonManagerModule).getAddonManager();
        addonManager.init();
        AddonsManagerPanel addonManagerPanel = addonManager.getManagerPanel();
        addonManager.refreshCatalog();
        addonManager.setStatusListener(new AddonManager.AddonManagerStatusListener() {
            @Override
            public void setProgressStatus(String status) {
                controlPanel.setProgressStatus(status);
            }

            @Override
            public void setStatusLabel(String text) {
                controlPanel.setStatusLabel(text);
            }

            @Override
            public void clear() {
                controlPanel.setStatusLabel("");
            }

            @Override
            public void setAvailableUpdates(int updatesCount) {
                controlPanel.setAvailableUpdates(updatesCount);
            }

            @Override
            public void setManualOnlyMode() {
                controlPanel.showManualOnlyWarning();
            }

            @Override
            public void setCatalogNotAvailable() {
                controlPanel.setConnectionFailed();
            }
        });

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);

        final WindowHandler dialog = windowModule.createDialog(addonManagerPanel, controlPanel);
        controlPanel.setController(new AddonsManagerControlPanel.Controller() {
            @Override
            public void performRefresh() {
                controlPanel.clearStatus();
                addonManager.refreshCatalog();
            }

            @Override
            public void performUpdateAll() {
                addonManager.updateAll();
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
