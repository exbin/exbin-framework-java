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
package org.exbin.framework.bined;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.exbin.bined.operation.undo.BinaryDataUndoHandler;
import org.exbin.bined.swing.extended.ExtCodeArea;
import org.exbin.bined.swing.extended.color.ExtendedCodeAreaColorProfile;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.bined.gui.BinEdComponentPanel;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.gui.editor.action.CloseFileAction;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.editor.api.GuiEditorModuleApi;
import org.exbin.framework.gui.editor.api.MultiEditorProvider;
import org.exbin.framework.gui.editor.gui.MultiEditorPanel;
import org.exbin.framework.gui.file.api.FileHandlerApi;
import org.exbin.framework.gui.file.api.FileType;

/**
 * Binary editor provider.
 *
 * @version 0.2.2 2021/10/06
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryMultiEditorProvider implements MultiEditorProvider, BinaryEditorControl {

    private final MultiEditorPanel multiEditorPanel = new MultiEditorPanel();
    private int lastIndex = 0;
    private int lastNewFileIndex = 0;
    private Map<Integer, Integer> newFilesMap = new HashMap<>();

    public BinaryMultiEditorProvider() {
    }

    public void setup(XBApplication application, ResourceBundle resourceBundle) {
        multiEditorPanel.setControl(new MultiEditorPanel.Control() {
            @Override
            public void activeIndexChanged(int index) {

            }

            @Override
            public void showPopupMenu(int index, Component component, int positionX, int positionY) {
                GuiEditorModuleApi editorModule = application.getModuleRepository().getModuleByInterface(GuiEditorModuleApi.class);
                JPopupMenu fileTabPopupMenu = new JPopupMenu();
                CloseFileAction closeFileAction = (CloseFileAction) editorModule.getCloseFileAction();
                JMenuItem closeMenuItem = new JMenuItem(closeFileAction);
                closeMenuItem.addActionListener((ActionEvent e) -> {
                    FileHandlerApi fileHandler = multiEditorPanel.getFileHandler(index);
                    if (releaseFile(fileHandler)) {
                        closeFile(fileHandler);
                        multiEditorPanel.removeFileHandler(fileHandler);
                    }
                });
                fileTabPopupMenu.add(closeMenuItem);
                fileTabPopupMenu.show(component, positionX, positionY);
            }
        });
    }

    @Nonnull
    @Override
    public BinEdFileHandler getActiveFile() {
        return (BinEdFileHandler) multiEditorPanel.getActiveFile();
    }

    @Nonnull
    @Override
    public JComponent getEditorComponent() {
        return multiEditorPanel;
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        ((BinEdComponentPanel) getComponent()).setPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public void setModificationListener(EditorModificationListener editorModificationListener) {
        ((BinEdComponentPanel) getComponent()).setModificationListener(editorModificationListener);
    }

    @Nonnull
    @Override
    public String getWindowTitle(String parentTitle) {
        return getActiveFile().getWindowTitle(parentTitle);
    }

    @Nonnull
    private BinEdComponentPanel getComponent() {
        return getActiveFile().getComponent();
    }

    @Override
    public void newFile() {
        int fileIndex = ++lastIndex;
        newFilesMap.put(fileIndex, ++lastNewFileIndex);
        BinEdFileHandler newFile = new BinEdFileHandler(fileIndex);
        newFile.newFile();
        multiEditorPanel.addFileHandler(newFile, getFileName(newFile));
    }

    @Override
    public void openFile(URI fileUri, FileType fileType) {
        BinEdFileHandler file = new BinEdFileHandler(++lastIndex);
        file.loadFromFile(fileUri, fileType);
        multiEditorPanel.addFileHandler(file, file.getFileName().orElse(""));
    }

    @Override
    public void openFile() {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public boolean releaseFile() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean releaseFile(FileHandlerApi fileHandler) {
        return false; //fileHandler.
    }

    @Nonnull
    private String getFileName(FileHandlerApi fileHandler) {
        Optional<String> fileName = fileHandler.getFileName();
        if (!fileName.isPresent()) {
            return "New File " + newFilesMap.get(fileHandler.getId());
        }

        return fileName.orElse("");
    }

    @Override
    public void setActiveEditor(EditorProvider editorProvider) {

    }

    @Override
    public void closeFile() {

    }

    @Override
    public void closeFile(FileHandlerApi file) {

    }

    @Override
    public void registerBinaryStatus(BinaryStatusApi binaryStatus) {
        getComponent().registerBinaryStatus(binaryStatus);
    }

    @Override
    public void registerEncodingStatus(TextEncodingStatusApi encodingStatus) {
        getComponent().registerEncodingStatus(encodingStatus);
    }

    @Nonnull
    @Override
    public ExtendedCodeAreaColorProfile getCurrentColors() {
        return getComponent().getCurrentColors();
    }

    @Nonnull
    @Override
    public ExtendedCodeAreaColorProfile getDefaultColors() {
        return getComponent().getDefaultColors();
    }

    @Override
    public void setCurrentColors(ExtendedCodeAreaColorProfile colorsProfile) {
        getComponent().setCurrentColors(colorsProfile);
    }

    @Override
    public boolean isWordWrapMode() {
        return getComponent().isWordWrapMode();
    }

    @Override
    public void setWordWrapMode(boolean mode) {
        getComponent().setWordWrapMode(mode);
    }

    @Nonnull
    @Override
    public Charset getCharset() {
        return getComponent().getCharset();
    }

    @Override
    public int getId() {
        return getActiveFile().getId();
    }

    @Override
    public void setCharset(Charset charset) {
        getComponent().setCharset(charset);
    }

    @Override
    public boolean isShowNonprintables() {
        return getComponent().isShowNonprintables();
    }

    @Override
    public void setShowNonprintables(boolean show) {
        getComponent().setShowNonprintables(show);
    }

    @Override
    public boolean isShowValuesPanel() {
        return getComponent().isShowValuesPanel();
    }

    @Override
    public void setShowValuesPanel(boolean show) {
        getComponent().setShowValuesPanel(show);
    }

    @Override
    public boolean changeLineWrap() {
        return getComponent().changeLineWrap();
    }

    @Nonnull
    @Override
    public BinEdComponentPanel getComponentPanel() {
        return getComponent();
    }

    @Nonnull
    @Override
    public BinaryDataUndoHandler getBinaryUndoHandler() {
        return getComponent().getUndoHandler();
    }

    @Nonnull
    @Override
    public ExtCodeArea getCodeArea() {
        return getComponent().getCodeArea();
    }

    @Override
    public void setFileHandlingMode(FileHandlingMode fileHandlingMode) {
        getComponent().setFileHandlingMode(fileHandlingMode);
    }
}
