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
package org.exbin.framework.docking;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.docking.action.NewFileAction;
import org.exbin.framework.docking.action.OpenFileAction;
import org.exbin.framework.docking.action.SaveAsFileAction;
import org.exbin.framework.docking.action.SaveFileAction;
import org.exbin.framework.docking.api.DockingModuleApi;
import org.exbin.framework.docking.api.DocumentDocking;
import org.exbin.framework.document.api.DocumentModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.api.MenuDefinitionManagement;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.toolbar.api.ToolBarDefinitionManagement;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;

/**
 * Interface for docking module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DockingModule implements DockingModuleApi {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(DockingModule.class);

    private ResourceBundle resourceBundle;

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(DockingModule.class);
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
    public DocumentDocking createDefaultDocking() {
        return new DefaultSingleDocking();
    }

    @Override
    public void registerMenuFileHandlingActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(DocumentModuleApi.FILE_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(createNewFileAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(createOpenFileAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(createSaveFileAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_MENU_GROUP_ID));
        contribution = mgmt.registerMenuItem(createSaveAsFileAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_MENU_GROUP_ID));
    }

    @Override
    public void registerToolBarFileHandlingActions() {
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarDefinitionManagement mgmt = toolBarModule.getMainToolBarManager(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(DocumentModuleApi.FILE_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerToolBarItem(createNewFileAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerToolBarItem(createOpenFileAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_TOOL_BAR_GROUP_ID));
        contribution = mgmt.registerToolBarItem(createSaveFileAction());
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_TOOL_BAR_GROUP_ID));
    }
    
    @Override
    public void registerDocumentReceiver(DocumentDocking documentDocking) {
        DocumentModuleApi documentModule = App.getModule(DocumentModuleApi.class);
        documentModule.getMainDocumentManager().addDocumentReceiver(documentDocking::openDocument);
    }

    @Nonnull
    @Override
    public NewFileAction createNewFileAction() {
        ensureSetup();
        NewFileAction newFileAction = new NewFileAction();
        newFileAction.init(resourceBundle);
        return newFileAction;
    }

    @Nonnull
    @Override
    public OpenFileAction createOpenFileAction() {
        ensureSetup();
        OpenFileAction openFileAction = new OpenFileAction();
        openFileAction.init(resourceBundle);
        return openFileAction;
    }

    @Nonnull
    @Override
    public SaveFileAction createSaveFileAction() {
        ensureSetup();
        SaveFileAction saveFileAction = new SaveFileAction();
        saveFileAction.init(resourceBundle);
        return saveFileAction;
    }

    @Nonnull
    @Override
    public SaveAsFileAction createSaveAsFileAction() {
        ensureSetup();
        SaveAsFileAction saveAsFileAction = new SaveAsFileAction();
        saveAsFileAction.init(resourceBundle);
        return saveAsFileAction;
    }
}
