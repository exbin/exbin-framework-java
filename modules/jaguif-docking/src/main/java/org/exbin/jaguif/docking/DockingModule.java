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
package org.exbin.jaguif.docking;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.App;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.docking.action.CloseFileAction;
import org.exbin.jaguif.docking.action.NewFileAction;
import org.exbin.jaguif.docking.action.OpenFileAction;
import org.exbin.jaguif.docking.action.SaveAsFileAction;
import org.exbin.jaguif.docking.action.SaveFileAction;
import org.exbin.jaguif.docking.api.DockingModuleApi;
import org.exbin.jaguif.docking.api.DocumentDocking;
import org.exbin.jaguif.docking.contribution.CloseFileContribution;
import org.exbin.jaguif.docking.contribution.NewFileContribution;
import org.exbin.jaguif.docking.contribution.OpenFileContribution;
import org.exbin.jaguif.docking.contribution.SaveAsFileContribution;
import org.exbin.jaguif.docking.contribution.SaveFileContribution;
import org.exbin.jaguif.document.api.DocumentModuleApi;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.toolbar.api.ToolBarDefinitionManagement;
import org.exbin.jaguif.toolbar.api.ToolBarModuleApi;

/**
 * Interface for docking module.
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

    @Nonnull
    @Override
    public DocumentDocking createDefaultDocking() {
        return new DefaultSingleDocking();
    }

    @Override
    public void registerMenuFileHandlingActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuDefinition(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(DocumentModuleApi.FILE_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = new NewFileContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_MENU_GROUP_ID));
        contribution = new OpenFileContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_MENU_GROUP_ID));
        contribution = new SaveFileContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_MENU_GROUP_ID));
        contribution = new SaveAsFileContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_MENU_GROUP_ID));
        contribution = new CloseFileContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_MENU_GROUP_ID));
    }

    @Override
    public void registerToolBarFileHandlingActions() {
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        ToolBarDefinitionManagement mgmt = toolBarModule.getMainToolBarDefinition(MODULE_ID);
        SequenceContribution contribution = mgmt.registerToolBarGroup(DocumentModuleApi.FILE_TOOL_BAR_GROUP_ID);
        mgmt.registerToolBarRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = new NewFileContribution();
        mgmt.registerToolBarContribution(contribution);
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_TOOL_BAR_GROUP_ID));
        contribution = new OpenFileContribution();
        mgmt.registerToolBarContribution(contribution);
        mgmt.registerToolBarRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_TOOL_BAR_GROUP_ID));
        contribution = new SaveFileContribution();
        mgmt.registerToolBarContribution(contribution);
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
        NewFileAction newFileAction = new NewFileAction();
        newFileAction.init(getResourceBundle());
        return newFileAction;
    }

    @Nonnull
    @Override
    public OpenFileAction createOpenFileAction() {
        OpenFileAction openFileAction = new OpenFileAction();
        openFileAction.init(getResourceBundle());
        return openFileAction;
    }

    @Nonnull
    @Override
    public SaveFileAction createSaveFileAction() {
        SaveFileAction saveFileAction = new SaveFileAction();
        saveFileAction.init(getResourceBundle());
        return saveFileAction;
    }

    @Nonnull
    @Override
    public SaveAsFileAction createSaveAsFileAction() {
        SaveAsFileAction saveAsFileAction = new SaveAsFileAction();
        saveAsFileAction.init(getResourceBundle());
        return saveAsFileAction;
    }

    @Nonnull
    @Override
    public CloseFileAction createCloseFileAction() {
        CloseFileAction closeFileAction = new CloseFileAction();
        closeFileAction.init(getResourceBundle());
        return closeFileAction;
    }
}
