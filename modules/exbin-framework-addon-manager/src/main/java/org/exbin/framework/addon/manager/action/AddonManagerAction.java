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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.ModuleProvider;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.addon.manager.gui.AddonManagerPanel;
import org.exbin.framework.addon.manager.operation.gui.AddonOperationPanel;
import org.exbin.framework.addon.manager.gui.AddonsControlPanel;
import org.exbin.framework.addon.manager.model.AddonRecord;
import org.exbin.framework.addon.manager.model.DependencyRecord;
import org.exbin.framework.addon.manager.model.ItemRecord;
import static org.exbin.framework.addon.manager.operation.gui.AddonOperationPanel.Step.DOWNLOAD;
import static org.exbin.framework.addon.manager.operation.gui.AddonOperationPanel.Step.OVERVIEW;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.addon.manager.service.AddonCatalogService;
import org.exbin.framework.addon.manager.service.impl.AddonCatalogServiceImpl;
import org.exbin.framework.basic.BasicModuleProvider;
import org.exbin.framework.basic.ModuleRecord;
import org.exbin.framework.window.api.gui.MultiStepControlPanel;
import org.exbin.framework.window.api.handler.MultiStepControlHandler;

/**
 * Addon manager action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonManagerAction extends AbstractAction {

    public static final String ACTION_ID = "addonManagerAction";

    private java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(AddonManagerAction.class);

    private AddonCatalogService addonManagerService;

    public AddonManagerAction() {
        init();
    }

    private void init() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);

        addonManagerService = new AddonCatalogServiceImpl();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AddonsControlPanel controlPanel = new AddonsControlPanel();

        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        AddonManagerPanel addonManagerPanel = new AddonManagerPanel();
        addonManagerPanel.setPreferredSize(new Dimension(800, 500));
        addonManagerPanel.setAddonCatalogService(addonManagerService);
        addonManagerPanel.setController(new AddonManagerPanel.Controller() {

            @Nonnull
            @Override
            public List<ItemRecord> getInstalledItems() {
                List<ItemRecord> installedAddons = new ArrayList<>();
                ModuleProvider moduleProvider = App.getModuleProvider();
                if (moduleProvider instanceof BasicModuleProvider) {
                    List<ModuleRecord> modulesList = ((BasicModuleProvider) moduleProvider).getModulesList();
                    for (ModuleRecord moduleRecord : modulesList) {
                        AddonRecord itemRecord = new AddonRecord(moduleRecord.getModuleId(), moduleRecord.getName());
                        itemRecord.setInstalled(true);
                        itemRecord.setVersion(moduleRecord.getVersion());
                        itemRecord.setProvider(moduleRecord.getProvider().orElse(null));
                        itemRecord.setHomepage(moduleRecord.getHomepage().orElse(null));
                        itemRecord.setDescription(moduleRecord.getDescription().orElse(null));
                        itemRecord.setIcon(moduleRecord.getIcon().orElse(null));
                        List<DependencyRecord> dependencyRecords = new ArrayList<>();
                        for (String dependencyModuleId : moduleRecord.getDependencyModuleIds()) {
                            dependencyRecords.add(new DependencyRecord(dependencyModuleId));
                        }
                        for (String dependencyLibraryId : moduleRecord.getDependencyLibraries()) {
                            dependencyRecords.add(new DependencyRecord(DependencyRecord.Type.JAR_LIBRARY, dependencyLibraryId));
                        }
                        itemRecord.setDependencies(dependencyRecords);
                        installedAddons.add(itemRecord);
                        // System.out.println(moduleRecord.getModuleId() + "," + moduleRecord.getName() + "," + moduleRecord.getDescription().orElse("") + "," + moduleRecord.getVersion() + "," + moduleRecord.getHomepage().orElse(""));
                    }
                }
                return installedAddons;
            }

            @Override
            public void installSelectionChanged(int toInstall) {
                controlPanel.setOperationState(AddonsControlPanel.OperationVariant.INSTALL, toInstall);
            }

            @Override
            public void updateSelectionChanged(int toUpdate) {
                controlPanel.setOperationState(AddonsControlPanel.OperationVariant.UPDATE, toUpdate);
            }

            @Override
            public void tabSwitched(AddonManagerPanel.Tab tab) {
                switch (tab) {
                    case PACKS:
                        throw new UnsupportedOperationException("Not supported yet.");
                    case ADDONS:
                        controlPanel.setOperationState(AddonsControlPanel.OperationVariant.INSTALL, addonManagerPanel.getToInstall());
                        break;
                    case INSTALLED:
                        controlPanel.setOperationState(AddonsControlPanel.OperationVariant.UPDATE, addonManagerPanel.getToUpdate());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        });

        controlPanel.setController(new AddonsControlPanel.Controller() {
            @Override
            public void performOperation() {
                AddonManagerPanel.Tab activeTab = addonManagerPanel.getActiveTab();

                MultiStepControlPanel controlPanel = new MultiStepControlPanel();
                AddonOperationPanel operationPanel = new AddonOperationPanel();
                operationPanel.setPreferredSize(new Dimension(600, 300));

                final WindowHandler dialog = windowModule.createDialog(operationPanel, controlPanel);
                windowModule.addHeaderPanel(dialog.getWindow(), addonManagerPanel.getClass(), operationPanel.getResourceBundle());
                windowModule.setWindowTitle(dialog, operationPanel.getResourceBundle());
                MultiStepControlHandler.MultiStepControlEnablementListener enablementListener = controlPanel.createEnablementListener();
                controlPanel.setHandler(new MultiStepControlHandler() {
                    
                    private AddonOperationPanel.Step step = AddonOperationPanel.Step.OVERVIEW;

                    @Override
                    public void controlActionPerformed(MultiStepControlHandler.ControlActionType actionType) {
                        switch (actionType) {
                            case CANCEL:
                                dialog.close();
                                break;
                            case NEXT:
                                switch (step) {
                                    case OVERVIEW:
                                        step = AddonOperationPanel.Step.DOWNLOAD;
                                        operationPanel.goToStep(step);
                                        enablementListener.actionEnabled(MultiStepControlHandler.ControlActionType.PREVIOUS, true);
                                        break;
                                    case DOWNLOAD:
                                        step = AddonOperationPanel.Step.SUCCESS;
                                        operationPanel.goToStep(step);
                                        enablementListener.actionEnabled(MultiStepControlHandler.ControlActionType.NEXT, false);
                                        enablementListener.actionEnabled(MultiStepControlHandler.ControlActionType.PREVIOUS, false);
                                        break;
                                    default:
                                        throw new AssertionError();
                                }
                                break;
                            case PREVIOUS:
                                switch (step) {
                                    case DOWNLOAD:
                                        step = AddonOperationPanel.Step.OVERVIEW;
                                        operationPanel.goToStep(step);
                                        enablementListener.actionEnabled(MultiStepControlHandler.ControlActionType.PREVIOUS, false);
                                        break;
                                    default:
                                        throw new AssertionError();
                                }
                                break;
                            case FINISH:
                                break;
                            default:
                                throw new AssertionError();
                        }
                    }
                });
                enablementListener.actionEnabled(MultiStepControlHandler.ControlActionType.NEXT, true);
                enablementListener.actionEnabled(MultiStepControlHandler.ControlActionType.FINISH, false);
                dialog.showCentered((Component) e.getSource());
                dialog.dispose();
            }
        });

        // controlPanel.showLegacyWarning();
        controlPanel.setOperationState(AddonsControlPanel.OperationVariant.UPDATE_ALL, 0);
        final WindowHandler dialog = windowModule.createDialog(addonManagerPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), addonManagerPanel.getClass(), addonManagerPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, addonManagerPanel.getResourceBundle());
        controlPanel.setHandler(dialog::close);
        dialog.showCentered((Component) e.getSource());
        dialog.dispose();
    }
}
