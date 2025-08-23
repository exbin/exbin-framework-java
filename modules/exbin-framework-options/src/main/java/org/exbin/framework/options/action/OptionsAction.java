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
package org.exbin.framework.options.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.DialogParentComponent;
import org.exbin.framework.options.api.OptionsPageReceiver;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsPanelType;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.options.gui.OptionsListPanel;
import org.exbin.framework.options.gui.OptionsTreePanel;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.gui.OptionsControlPanel;

/**
 * Options action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsAction extends AbstractAction {

    public static final String ACTION_ID = "optionsAction";

    private ResourceBundle resourceBundle;
    private OptionsPagesProvider optionsPagesProvider;
    private DialogParentComponent dialogParentComponent;

    public OptionsAction() {
    }

    public void setup(ResourceBundle resourceBundle, OptionsPagesProvider optionsPagesProvider) {
        this.resourceBundle = resourceBundle;
        this.optionsPagesProvider = optionsPagesProvider;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ActionContextChangeManager manager) -> {
            manager.registerUpdateListener(DialogParentComponent.class, (DialogParentComponent instance) -> {
                dialogParentComponent = instance;
                setEnabled(instance != null);
            });
        });
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        OptionsPanelType optionsPanelType = optionsModule.getOptionsPanelType();
        OptionsControlPanel controlPanel = new OptionsControlPanel();
        String optionsRootCaption = optionsModule.getOptionsRootCaption().orElse(null);
        WindowHandler dialog;
        switch (optionsPanelType) {
            case LIST:
                OptionsListPanel optionsListPanel = new OptionsListPanel();
                optionsPagesProvider.registerOptionsPages(optionsListPanel);
                optionsListPanel.setPreferences(preferencesModule.getAppPreferences());
                optionsListPanel.pagesFinished();
                optionsListPanel.loadAllFromPreferences();
                if (optionsRootCaption != null) {
                    optionsListPanel.setRootCaption(optionsRootCaption);
                }

                dialog = windowModule.createDialog(optionsListPanel, controlPanel);
                dialog.getWindow().setSize(780, 500);
                windowModule.setWindowTitle(dialog, optionsListPanel.getResourceBundle());
                controlPanel.setController((actionType) -> {
                    switch (actionType) {
                        case SAVE: {
                            optionsListPanel.saveAndApplyAll();
                            break;
                        }
                        case CANCEL: {
                            break;
                        }
                        case APPLY_ONCE: {
                            optionsListPanel.applyPreferencesChanges();
                            break;
                        }
                    }
                    dialog.close();
                });
                dialog.showCentered(dialogParentComponent.getComponent());
                break;
            case TREE:
                OptionsTreePanel optionsTreePanel = new OptionsTreePanel();
                optionsPagesProvider.registerOptionsPages(optionsTreePanel);
                optionsTreePanel.setPreferences(preferencesModule.getAppPreferences());
                optionsTreePanel.pagesFinished();
                optionsTreePanel.loadAllFromPreferences();
                if (optionsRootCaption != null) {
                    optionsTreePanel.setRootCaption(optionsRootCaption);
                }

                dialog = windowModule.createDialog(optionsTreePanel, controlPanel);
                dialog.getWindow().setSize(780, 500);
                windowModule.setWindowTitle(dialog, optionsTreePanel.getResourceBundle());
                controlPanel.setController((actionType) -> {
                    switch (actionType) {
                        case SAVE: {
                            optionsTreePanel.saveAndApplyAll();
                            break;
                        }
                        case CANCEL: {
                            break;
                        }
                        case APPLY_ONCE: {
                            optionsTreePanel.applyPreferencesChanges();
                            break;
                        }
                    }
                    dialog.close();
                });
                dialog.showCentered(dialogParentComponent.getComponent());
                break;
            default:
                throw new IllegalStateException("Illegal options panel type " + optionsPanelType.name());
        }
    }

    @ParametersAreNonnullByDefault
    public interface OptionsPagesProvider {

        void registerOptionsPages(OptionsPageReceiver optionsPageReceiver);
    }
}
