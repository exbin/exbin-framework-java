/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.editor.xbup.viewer;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.gui.BlockPropertiesPanel;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.file.api.FileType;
import org.exbin.framework.gui.file.api.GuiFileModuleApi;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.utils.ClipboardActionsHandler;
import org.exbin.framework.gui.utils.ClipboardActionsUpdateListener;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.gui.CloseControlPanel;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.operation.undo.XBUndoHandler;
import org.exbin.xbup.parser_tree.XBTTreeDocument;
import org.exbin.xbup.plugin.XBPluginRepository;
import org.exbin.framework.gui.file.api.FileHandler;

/**
 * Viewer provider.
 *
 * @version 0.2.1 2021/12/05
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class XbupSingleEditorProvider implements XbupEditorProvider, ClipboardActionsHandler {

    private XBACatalog catalog;
    private PropertyChangeListener propertyChangeListener = null;

    private ClipboardActionsHandler activeHandler;

    private XBApplication application;
    private XBPluginRepository pluginRepository;
    private final List<DocumentItemSelectionListener> itemSelectionListeners = new ArrayList<>();
    private ClipboardActionsUpdateListener clipboardActionsUpdateListener;
    private XbupFileHandler activeFile;
    private boolean devMode = false;
    @Nullable
    private File lastUsedDirectory;

    public XbupSingleEditorProvider(XBUndoHandler undoHandler) {
        activeFile = new XbupFileHandler();
        activeFile.setUndoHandler(undoHandler);
        activeFile.setItemSelectionListener((block) -> {
            itemSelectionListeners.forEach(listener -> {
                listener.itemSelected(block);
            });
        });
    }

    @Nonnull
    @Override
    public JPanel getEditorComponent() {
        return activeFile.getComponent();
    }

    @Nonnull
    @Override
    public Optional<FileHandler> getActiveFile() {
        return Optional.of(activeFile);
    }

    @Nonnull
    @Override
    public Optional<File> getLastUsedDirectory() {
        return Optional.ofNullable(lastUsedDirectory);
    }

    @Override
    public void setLastUsedDirectory(@Nullable File directory) {
        lastUsedDirectory = directory;
    }

    @Override
    public void updateRecentFilesList(URI fileUri, FileType fileType) {
        GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
        fileModule.updateRecentFilesList(fileUri, fileType);
    }

    @Override
    public void setModificationListener(EditorProvider.EditorModificationListener editorModificationListener) {
        // TODO
    }

    @Override
    public XBACatalog getCatalog() {
        return catalog;
    }

    @Override
    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
        activeFile.setCatalog(catalog);
    }

    @Override
    public XBApplication getApplication() {
        return application;
    }

    public void setApplication(XBApplication application) {
        this.application = application;
        activeFile.setApplication(application);
    }

    @Override
    public XBPluginRepository getPluginRepository() {
        return pluginRepository;
    }

    @Override
    public void setPluginRepository(XBPluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
        activeFile.setPluginRepository(pluginRepository);
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
        activeFile.setDevMode(devMode);
    }

    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.propertyChangeListener = propertyChangeListener;
        activeFile.getComponent().setPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public String getWindowTitle(String frameTitle) {
        XBTTreeDocument treeDocument = activeFile.getDoc();
        String fileName = treeDocument.getFileName();
        if (!"".equals(fileName)) {
            int pos;
            int newpos = 0;
            do {
                pos = newpos;
                newpos = fileName.indexOf(File.separatorChar, pos) + 1;
            } while (newpos > 0);
            return fileName.substring(pos) + " - " + frameTitle;
        }

        return frameTitle;
    }

    @Override
    public void performCut() {
        activeHandler.performCut();
    }

    @Override
    public void performCopy() {
        activeHandler.performCopy();
    }

    @Override
    public void performPaste() {
        activeHandler.performPaste();
    }

    @Override
    public void performDelete() {
        activeHandler.performDelete();
    }

    @Override
    public void performSelectAll() {
        activeHandler.performSelectAll();
    }

    @Override
    public boolean isSelection() {
        return activeHandler.isSelection();
    }

    @Override
    public boolean isEditable() {
        return activeHandler.isEditable();
    }

    @Override
    public boolean canSelectAll() {
        return activeHandler.canSelectAll();
    }

    @Override
    public boolean canPaste() {
        return activeHandler.canPaste();
    }

    @Override
    public boolean canDelete() {
        return activeHandler.canDelete();
    }

    @Override
    public void setUpdateListener(ClipboardActionsUpdateListener updateListener) {
        clipboardActionsUpdateListener = updateListener;
        activeFile.setUpdateListener(updateListener);
    }

    public void actionItemProperties() {
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        BlockPropertiesPanel panel = new BlockPropertiesPanel();
        panel.setApplication(application);
        panel.setCatalog(catalog);
        panel.setBlock(activeFile.getSelectedItem().orElse(null));
        CloseControlPanel controlPanel = new CloseControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(panel, controlPanel);
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(dialogPanel);
        controlPanel.setHandler(() -> {
            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(null);
    }

    @Override
    public void addItemSelectionListener(DocumentItemSelectionListener listener) {
        itemSelectionListeners.add(listener);
    }

    @Override
    public void removeItemSelectionListener(DocumentItemSelectionListener listener) {
        itemSelectionListeners.remove(listener);
    }

    @Override
    public void newFile() {
        activeFile.newFile();
    }

    @Override
    public void openFile(URI fileUri, FileType fileType) {
        activeFile.loadFromFile(fileUri, fileType);
    }

    @Override
    public void openFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadFromFile(String fileName) throws URISyntaxException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public void saveFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveAsFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean releaseFile(FileHandler fileHandler) {
        if (fileHandler.isModified()) {
            GuiFileModuleApi fileModule = application.getModuleRepository().getModuleByInterface(GuiFileModuleApi.class);
            return fileModule.getFileActions().showAskForSaveDialog(fileHandler, null, this);
        }

        return true;
    }

    @Override
    public boolean releaseAllFiles() {
        return releaseFile(activeFile);
    }
}