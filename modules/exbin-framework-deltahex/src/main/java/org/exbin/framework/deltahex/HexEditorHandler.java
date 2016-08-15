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
package org.exbin.framework.deltahex;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import org.exbin.framework.deltahex.panel.HexColorType;
import org.exbin.framework.deltahex.panel.HexPanel;
import org.exbin.framework.deltahex.panel.SearchParameters;
import org.exbin.framework.editor.text.TextEncodingStatusApi;
import org.exbin.framework.editor.text.dialog.TextFontDialog;
import org.exbin.framework.gui.docking.api.EditorViewHandling;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.editor.api.MultiEditorProvider;
import org.exbin.framework.gui.file.api.FileType;

/**
 * Hexadecimal editor provider.
 *
 * @version 0.2.0 2016/08/15
 * @author ExBin Project (http://exbin.org)
 */
public class HexEditorHandler implements HexEditorProvider, MultiEditorProvider {

    private HexPanelInit hexPanelInit = null;
    private final List<HexPanel> panels = new ArrayList<>();
    private EditorViewHandling editorViewHandling = null;
    private HexPanel activePanel = null;
    private int lastIndex = 0;
    private HexStatusApi hexStatus = null;
    private TextEncodingStatusApi encodingStatus;

    public HexEditorHandler() {
    }

    @Override
    public JPanel getPanel() {
        return activePanel.getPanel();
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        activePanel.setPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public String getWindowTitle(String frameTitle) {
        return activePanel.getWindowTitle(frameTitle);
    }

    @Override
    public void loadFromFile(URI fileUri, FileType fileType) {
        HexPanel panel = createNewPanel();
        panel.newFile();
        panel.loadFromFile(fileUri, fileType);
        activePanel = panel;
    }

    @Override
    public void saveToFile(URI fileUri, FileType fileType) {
        activePanel.saveToFile(fileUri, fileType);
    }

    @Override
    public URI getFileUri() {
        return activePanel.getFileUri();
    }

    @Override
    public String getName() {
        return activePanel.getName();
    }

    @Override
    public FileType getFileType() {
        return activePanel.getFileType();
    }

    @Override
    public void setFileType(FileType fileType) {
        activePanel.setFileType(fileType);
    }

    @Override
    public void newFile() {
        HexPanel panel = createNewPanel();
        panel.newFile();
        activePanel = panel;
    }

    @Override
    public boolean isModified() {
        return activePanel.isModified();
    }

    @Override
    public void registerHexStatus(HexStatusApi hexStatusApi) {
        this.hexStatus = hexStatusApi;
        if (!panels.isEmpty()) {
            for (HexPanel panel : panels) {
                panel.registerHexStatus(hexStatusApi);
            }
        }
    }

    @Override
    public void registerEncodingStatus(TextEncodingStatusApi encodingStatusApi) {
        this.encodingStatus = encodingStatusApi;
        if (!panels.isEmpty()) {
            for (HexPanel panel : panels) {
                panel.registerEncodingStatus(encodingStatusApi);
            }
        }
    }

    private synchronized HexPanel createNewPanel() {
        HexPanel panel = new HexPanel(lastIndex);
        lastIndex++;
        panels.add(panel);
        if (hexPanelInit != null) {
            hexPanelInit.init(panel);
        }
        if (hexStatus != null) {
            panel.registerHexStatus(hexStatus);
            panel.registerEncodingStatus(encodingStatus);
        }
        editorViewHandling.addEditorView(panel, this);

        return panel;
    }

    public void init() {
        activePanel = createNewPanel();
        activePanel.newFile();
    }

    public HexPanelInit getHexPanelInit() {
        return hexPanelInit;
    }

    public void setHexPanelInit(HexPanelInit hexPanelInit) {
        this.hexPanelInit = hexPanelInit;
    }

    public EditorViewHandling getEditorViewHandling() {
        return editorViewHandling;
    }

    public void setEditorViewHandling(EditorViewHandling editorViewHandling) {
        this.editorViewHandling = editorViewHandling;
    }

    @Override
    public Map<HexColorType, Color> getCurrentColors() {
        return activePanel.getCurrentColors();
    }

    @Override
    public Map<HexColorType, Color> getDefaultColors() {
        return activePanel.getDefaultColors();
    }

    @Override
    public void setCurrentColors(Map<HexColorType, Color> colors) {
        activePanel.setCurrentColors(colors);
    }

    @Override
    public boolean isWordWrapMode() {
        return activePanel.getCodeArea().isWrapMode();
    }

    @Override
    public void setWordWrapMode(boolean mode) {
        activePanel.getCodeArea().setWrapMode(mode);
    }

    @Override
    public Charset getCharset() {
        return activePanel.getCharset();
    }

    @Override
    public void setCharset(Charset charset) {
        activePanel.setCharset(charset);
    }

    @Override
    public void findText(SearchParameters searchParameters) {
        activePanel.findText(searchParameters);
    }

    @Override
    public boolean changeShowNonprintables() {
        return activePanel.changeShowNonprintables();
    }

    @Override
    public void showFontDialog(TextFontDialog dialog) {
        activePanel.showFontDialog(dialog);
    }

    @Override
    public boolean changeLineWrap() {
        return activePanel.changeLineWrap();
    }

    @Override
    public HexPanel getDocument() {
        return activePanel;
    }

    @Override
    public void printFile() {
        activePanel.printFile();
    }

    @Override
    public void setActiveEditor(EditorProvider editorProvider) {
        if (editorProvider instanceof HexPanel) {
            HexPanel hexPanel = (HexPanel) editorProvider;
            activePanel = hexPanel;
            hexPanel.notifyListeners();
        }
    }

    /**
     * Method for initialization of new hexadecimal panel.
     */
    public static interface HexPanelInit {

        void init(HexPanel panel);
    }
}
