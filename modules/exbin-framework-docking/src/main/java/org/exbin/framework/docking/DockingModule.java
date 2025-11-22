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

import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.framework.App;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.docking.action.CloseAllFilesAction;
import org.exbin.framework.docking.action.CloseFileAction;
import org.exbin.framework.docking.action.CloseOtherFilesAction;
import org.exbin.framework.docking.api.BasicDockingType;
import org.exbin.framework.docking.api.DockingModuleApi;
import org.exbin.framework.docking.api.DockingType;
import org.exbin.framework.docking.api.DocumentDocking;
import org.exbin.framework.docking.gui.ModifiedDocumentsPanel;
import org.exbin.framework.document.api.Document;
import org.exbin.framework.file.api.FileModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.menu.api.MenuDefinitionManagement;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;

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
    public DocumentDocking createDefaultDocking(DockingType dockingType) {
        if (BasicDockingType.SINGLE.equals(dockingType)) {
            return new DefaultSingleDocking();
        } else if (BasicDockingType.MULTI.equals(dockingType)) {
            return new DefaultMultiDocking();
        }
        
        throw new IllegalStateException("Unsupported docking type " + dockingType.toString());
    }

    public boolean showAskForSaveDialog(List<Document> documents, JComponent parentComponent) {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        ModifiedDocumentsPanel modifiedDocumentsPanel = new ModifiedDocumentsPanel();
        modifiedDocumentsPanel.setDocuments(documents);
        final boolean[] result = new boolean[1];
        final WindowHandler dialog = windowModule.createDialog(modifiedDocumentsPanel);
        modifiedDocumentsPanel.setController(new ModifiedDocumentsPanel.Controller() {
            @Override
            public boolean saveFile(Document document) {
                // TODO
//                editorProvider.saveFile(document);
//                return !((EditableFileHandler) document).isModified();
                return false;
            }

            @Override
            public void discardAll(List<Document> documents) {
                result[0] = true;
                dialog.close();
            }

            @Override
            public void cancel() {
                result[0] = false;
                dialog.close();
            }
        });

        windowModule.setWindowTitle(dialog, modifiedDocumentsPanel.getResourceBundle());
        modifiedDocumentsPanel.assignGlobalKeys();
        dialog.showCentered(parentComponent);

        return result[0];
    }

    @Nonnull
    @Override
    public CloseFileAction createCloseFileAction() {
        CloseFileAction closeFileAction = new CloseFileAction();
        ensureSetup();
        closeFileAction.setup(resourceBundle);
        return closeFileAction;
    }

    @Nonnull
    @Override
    public CloseAllFilesAction createCloseAllFilesAction() {
        CloseAllFilesAction closeAllFilesAction = new CloseAllFilesAction();
        ensureSetup();
        closeAllFilesAction.setup(resourceBundle);
        return closeAllFilesAction;
    }

    @Nonnull
    @Override
    public CloseOtherFilesAction createCloseOtherFilesAction() {
        CloseOtherFilesAction closeOtherFilesAction = new CloseOtherFilesAction();
        ensureSetup();
        closeOtherFilesAction.setup(resourceBundle);
        return closeOtherFilesAction;
    }

    @Override
    public void registerMenuFileCloseActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(FileModuleApi.FILE_MENU_GROUP_ID);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(createCloseFileAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(FileModuleApi.FILE_MENU_GROUP_ID));
    }
}
