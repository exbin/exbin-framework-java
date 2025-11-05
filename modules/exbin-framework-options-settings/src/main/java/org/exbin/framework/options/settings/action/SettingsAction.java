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
package org.exbin.framework.options.settings.action;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.DialogParentComponent;
import org.exbin.framework.options.settings.api.SettingsPanelType;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.options.settings.gui.SettingsListPanel;
import org.exbin.framework.options.settings.gui.SettingsTreePanel;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.gui.OptionsControlPanel;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.settings.SettingsPageReceiver;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.action.api.ActionContextChangeRegistration;
import org.exbin.framework.options.settings.SettingsPage;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;

/**
 * Options settings action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SettingsAction extends AbstractAction {

    public static final String ACTION_ID = "settingsAction";

    private ResourceBundle resourceBundle;
    private SettingsPagesProvider optionsPagesProvider;
    private DialogParentComponent dialogParentComponent;

    public SettingsAction() {
    }

    public void setup(ResourceBundle resourceBundle, SettingsPagesProvider optionsPagesProvider) {
        this.resourceBundle = resourceBundle;
        this.optionsPagesProvider = optionsPagesProvider;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ActionContextChangeRegistration registrar) -> {
            registrar.registerUpdateListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
                dialogParentComponent = instance;
                setEnabled(instance != null);
            });
        });
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        SettingsPanelType settingsPanelType = optionsSettingsModule.getSettingsPanelType();
        OptionsControlPanel controlPanel = new OptionsControlPanel();
        OptionsSettingsManagement settingsManagement = optionsSettingsModule.getMainSettingsManager();
        String optionsRootCaption = optionsSettingsModule.getOptionsRootCaption().orElse(null);
        WindowHandler dialog;
        switch (settingsPanelType) {
            case LIST:
                SettingsListPanel optionsListPanel = new SettingsListPanel();
                optionsPagesProvider.registerSettingsPages(optionsListPanel);
                optionsListPanel.setSettingsOptionsProvider(settingsManagement.getSettingsOptionsProvider());
                optionsListPanel.pagesFinished();
                loadAll(optionsListPanel.getSettingsPages());
                if (optionsRootCaption != null) {
                    optionsListPanel.setRootCaption(optionsRootCaption);
                }

                dialog = windowModule.createDialog(optionsListPanel, controlPanel);
                dialog.getWindow().setSize(780, 500);
                windowModule.setWindowTitle(dialog, optionsListPanel.getResourceBundle());
                controlPanel.setController((actionType) -> {
                    switch (actionType) {
                        case SAVE: {
                            saveAndApplyAll(optionsListPanel.getSettingsPages());
                            break;
                        }
                        case CANCEL: {
                            break;
                        }
                        case APPLY_ONCE: {
                            applyOnlyAll(optionsListPanel.getSettingsPages());
                            break;
                        }
                    }
                    dialog.close();
                });
                dialog.showCentered(dialogParentComponent.getComponent());
                break;
            case TREE:
                SettingsTreePanel optionsTreePanel = new SettingsTreePanel();
                optionsPagesProvider.registerSettingsPages(optionsTreePanel);
                optionsTreePanel.setSettingsOptionsProvider(settingsManagement.getSettingsOptionsProvider());
                optionsTreePanel.pagesFinished();
                loadAll(optionsTreePanel.getSettingsPages());
                if (optionsRootCaption != null) {
                    optionsTreePanel.setRootCaption(optionsRootCaption);
                }

                dialog = windowModule.createDialog(optionsTreePanel, controlPanel);
                dialog.getWindow().setSize(780, 500);
                windowModule.setWindowTitle(dialog, optionsTreePanel.getResourceBundle());
                controlPanel.setController((actionType) -> {
                    switch (actionType) {
                        case SAVE: {
                            saveAndApplyAll(optionsTreePanel.getSettingsPages());
                            break;
                        }
                        case CANCEL: {
                            break;
                        }
                        case APPLY_ONCE: {
                            applyOnlyAll(optionsTreePanel.getSettingsPages());
                            break;
                        }
                    }
                    dialog.close();
                });
                dialog.showCentered(dialogParentComponent.getComponent());
                break;
            default:
                throw new IllegalStateException("Illegal options panel type " + settingsPanelType.name());
        }
    }

    private void loadAll(Collection<SettingsPage> pages) {
        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        SettingsOptionsProvider settingsOptionsProvider = optionsSettingsModule.getMainSettingsManager().getSettingsOptionsProvider();

        for (SettingsPage page : pages) {
            try {
                page.loadFromOptions(settingsOptionsProvider, null);
            } catch (Exception ex) {
                Logger.getLogger(SettingsAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void saveAndApplyAll(Collection<SettingsPage> pages) {
        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        SettingsOptionsProvider settingsOptionsProvider = optionsSettingsModule.getMainSettingsManager().getSettingsOptionsProvider();

        for (SettingsPage page : pages) {
            try {
                page.saveAndApply(settingsOptionsProvider, null);
            } catch (Exception ex) {
                Logger.getLogger(SettingsAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        OptionsSettingsModuleApi optionsModule = App.getModule(OptionsSettingsModuleApi.class);
        optionsModule.notifyOptionsChanged();
    }

    private void applyOnlyAll(Collection<SettingsPage> pages) {
        throw new UnsupportedOperationException("Not supported yet.");
//        for (SettingsPage page : pages) {
//            try {
//                page.applyPreferencesChanges(settingsOptionsProvider, null);
//            } catch (Exception ex) {
//                Logger.getLogger(SettingsAction.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//
//        OptionsSettingsModuleApi windowModule = App.getModule(OptionsSettingsModuleApi.class);
//        windowModule.notifyOptionsChanged();
    }

    public void setDialogParentComponent(DialogParentComponent dialogParentComponent) {
        this.dialogParentComponent = dialogParentComponent;
    }

    @ParametersAreNonnullByDefault
    public interface SettingsPagesProvider {

        void registerSettingsPages(SettingsPageReceiver settingsPageReceiver);
    }
}
