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
import org.exbin.framework.context.api.ActiveContextManagement;
import org.exbin.framework.frame.api.ApplicationFrameHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.options.settings.SettingsOptionsStorage;
import org.exbin.framework.options.settings.SettingsPage;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;
import org.exbin.framework.context.api.ContextChangeRegistration;

/**
 * Options settings action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SettingsAction extends AbstractAction {

    public static final String ACTION_ID = "settingsAction";

    private ResourceBundle resourceBundle;
    private SettingsPagesProvider settingsPagesProvider;
    private DialogParentComponent dialogParentComponent;

    public SettingsAction() {
    }

    public void setup(ResourceBundle resourceBundle, SettingsPagesProvider settingsPagesProvider) {
        this.resourceBundle = resourceBundle;
        this.settingsPagesProvider = settingsPagesProvider;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ContextChangeRegistration registrar) -> {
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
        String optionsRootCaption = optionsSettingsModule.getOptionsRootCaption().orElse(null);
        WindowHandler dialog;
        switch (settingsPanelType) {
            case LIST:
                SettingsListPanel settingsListPanel = new SettingsListPanel();
                settingsPagesProvider.registerSettingsPages(settingsListPanel);
                settingsListPanel.pagesFinished();
                loadAll(settingsListPanel.getSettingsPages());
                if (optionsRootCaption != null) {
                    settingsListPanel.setRootCaption(optionsRootCaption);
                }

                dialog = windowModule.createDialog(settingsListPanel, controlPanel);
                dialog.getWindow().setSize(780, 500);
                windowModule.setWindowTitle(dialog, settingsListPanel.getResourceBundle());
                controlPanel.setController((actionType) -> {
                    switch (actionType) {
                        case SAVE: {
                            saveAndApplyAll(settingsListPanel.getSettingsPages());
                            break;
                        }
                        case CANCEL: {
                            break;
                        }
                        case APPLY_ONCE: {
                            applyOnlyAll(settingsListPanel.getSettingsPages());
                            break;
                        }
                    }
                    dialog.close();
                });
                dialog.showCentered(dialogParentComponent.getComponent());
                break;
            case TREE:
                SettingsTreePanel settingsTreePanel = new SettingsTreePanel();
                settingsPagesProvider.registerSettingsPages(settingsTreePanel);
                settingsTreePanel.pagesFinished();
                loadAll(settingsTreePanel.getSettingsPages());
                if (optionsRootCaption != null) {
                    settingsTreePanel.setRootCaption(optionsRootCaption);
                }

                dialog = windowModule.createDialog(settingsTreePanel, controlPanel);
                dialog.getWindow().setSize(780, 500);
                windowModule.setWindowTitle(dialog, settingsTreePanel.getResourceBundle());
                controlPanel.setController((actionType) -> {
                    switch (actionType) {
                        case SAVE: {
                            saveAndApplyAll(settingsTreePanel.getSettingsPages());
                            break;
                        }
                        case CANCEL: {
                            break;
                        }
                        case APPLY_ONCE: {
                            applyOnlyAll(settingsTreePanel.getSettingsPages());
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

        // TODO Run in top context
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ApplicationFrameHandler frameHandler = frameModule.getFrameHandler();
        ActiveContextManagement contextManager = frameHandler.getContextManager();
        
        for (SettingsPage page : pages) {
            try {
                page.loadAll(settingsOptionsProvider, contextManager);
            } catch (Exception ex) {
                Logger.getLogger(SettingsAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void saveAndApplyAll(Collection<SettingsPage> pages) {
        OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement mainSettingsManager = optionsSettingsModule.getMainSettingsManager();
        SettingsOptionsProvider settingsOptionsProvider = mainSettingsManager.getSettingsOptionsProvider();

        // TODO Run in top context
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ApplicationFrameHandler frameHandler = frameModule.getFrameHandler();
        ActiveContextManagement contextManager = frameHandler.getContextManager();
        
        for (SettingsPage page : pages) {
            try {
                page.saveAll(settingsOptionsProvider, contextManager);
            } catch (Exception ex) {
                Logger.getLogger(SettingsAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void applyOnlyAll(Collection<SettingsPage> pages) {
        // TODO Run in top context
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ApplicationFrameHandler frameHandler = frameModule.getFrameHandler();
        ActiveContextManagement contextManager = frameHandler.getContextManager();
        
        SettingsOptionsStorage settingsOptionsStorage = new SettingsOptionsStorage();
        for (SettingsPage page : pages) {
            try {
                page.saveAll(settingsOptionsStorage, contextManager);
            } catch (Exception ex) {
                Logger.getLogger(SettingsAction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setDialogParentComponent(DialogParentComponent dialogParentComponent) {
        this.dialogParentComponent = dialogParentComponent;
    }

    @ParametersAreNonnullByDefault
    public interface SettingsPagesProvider {

        void registerSettingsPages(SettingsPageReceiver settingsPageReceiver);
    }
}
