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
package org.exbin.framework.docking.multi;

import org.exbin.framework.docking.multi.gui.ModifiedDocumentsPanel;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.framework.App;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.docking.multi.action.CloseAllFilesAction;
import org.exbin.framework.docking.multi.action.CloseFileAction;
import org.exbin.framework.docking.multi.action.CloseOtherFilesAction;
import org.exbin.framework.docking.api.DocumentDocking;
import org.exbin.framework.docking.multi.api.DockingMultiModuleApi;
import org.exbin.framework.document.api.Document;
import org.exbin.framework.document.api.DocumentModuleApi;
import org.exbin.framework.document.api.DocumentSource;
import org.exbin.framework.document.api.EditableDocument;
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
public class DockingMultiModule implements DockingMultiModuleApi {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(DockingMultiModule.class);
    public static final String FILE_CONTEXT_MENU_ID = "fileContextMenu";

    private ResourceBundle resourceBundle;

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(DockingMultiModule.class);
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
        return new DefaultMultiDocking();
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
                EditableDocument editableDocument = (EditableDocument) document;
                Optional<DocumentSource> optDocumentSource = editableDocument.getDocumentSource();
                if (optDocumentSource.isPresent()) {
                    editableDocument.saveTo(optDocumentSource.get());
                    return true;
                } else {
                    DocumentModuleApi documentModule = App.getModule(DocumentModuleApi.class);
                    Optional<DocumentSource> documentSource = documentModule.getMainDocumentManager().saveDocumentAs(document);
                    if (documentSource.isPresent()) {
                        editableDocument.saveTo(documentSource.get());
                        return true;
                    }
                }
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
        {
            MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
            SequenceContribution contribution = mgmt.registerMenuGroup(DocumentModuleApi.FILE_MENU_GROUP_ID);
            mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
            contribution = mgmt.registerMenuItem(createCloseFileAction());
            mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(DocumentModuleApi.FILE_MENU_GROUP_ID));
        }

        menuModule.registerMenu(FILE_CONTEXT_MENU_ID, MODULE_ID);
        MenuDefinitionManagement mgmt = menuModule.getMenuManager(FILE_CONTEXT_MENU_ID, MODULE_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(createCloseFileAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(createCloseAllFilesAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(createCloseOtherFilesAction());
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
    }
}
