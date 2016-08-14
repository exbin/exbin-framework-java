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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import org.exbin.framework.deltahex.panel.HexColorType;
import org.exbin.framework.deltahex.panel.HexPanel;
import org.exbin.framework.deltahex.panel.HexStatusPanel;
import org.exbin.framework.deltahex.panel.SearchParameters;
import org.exbin.framework.editor.text.dialog.TextFontDialog;
import org.exbin.framework.gui.docking.api.EditorViewHandling;
import org.exbin.framework.gui.file.api.FileType;

/**
 * Hexadecimal editor provider.
 *
 * @version 0.2.0 2016/08/14
 * @author ExBin Project (http://exbin.org)
 */
public class HexEditorHandler implements HexEditorProvider {

    private EditorMode editorMode = EditorMode.MULTI;
    private HexPanelInit hexPanelInit = null;
    private final List<HexPanel> panels = new ArrayList<>();
    private EditorViewHandling editorViewHandling = null;
    private HexPanel activePanel = null;
    private int lastIndex = 0;

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
    public void loadFromFile() {
        switch (editorMode) {
            case SINGLE: {
                panels.get(0).loadFromFile();
                break;
            }
            case MULTI: {
                activePanel.loadFromFile();
                break;
            }
            default:
                throw new IllegalStateException("Unexpected editor mode " + editorMode.name());
        }
    }

    @Override
    public void saveToFile() {
        switch (editorMode) {
            case SINGLE: {
                panels.get(0).saveToFile();
                break;
            }
            case MULTI: {
                activePanel.saveToFile();
                break;
            }
            default:
                throw new IllegalStateException("Unexpected editor mode " + editorMode.name());
        }
    }

    @Override
    public String getFileName() {
        switch (editorMode) {
            case SINGLE: {
                return panels.get(0).getFileName();
            }
            case MULTI: {
                return activePanel.getFileName();
            }
            default:
                throw new IllegalStateException("Unexpected editor mode " + editorMode.name());
        }
    }

    @Override
    public void setFileName(String fileName) {
        switch (editorMode) {
            case SINGLE: {
                panels.get(0).setFileName(fileName);
                break;
            }
            case MULTI: {
                activePanel.setFileName(fileName);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected editor mode " + editorMode.name());
        }
    }

    @Override
    public void setFileType(FileType fileType) {
        activePanel.setFileType(fileType);
    }

    @Override
    public void newFile() {
        switch (editorMode) {
            case SINGLE: {
                panels.get(0).newFile();
                break;
            }
            case MULTI: {
                HexPanel panel = createNewPanel();
                panel.newFile();
                break;
            }
            default:
                throw new IllegalStateException("Unexpected editor mode " + editorMode.name());
        }
    }

    @Override
    public boolean isModified() {
        return activePanel.isModified();
    }

    @Override
    public void registerTextStatus(HexStatusPanel hexStatusPanel) {
        switch (editorMode) {
            case SINGLE: {
                panels.get(0).registerTextStatus(hexStatusPanel);
                break;
            }
            case MULTI: {
                // TODO
                break;
            }
            default:
                throw new IllegalStateException("Unexpected editor mode " + editorMode.name());
        }
    }

    private synchronized HexPanel createNewPanel() {
        HexPanel panel = new HexPanel(lastIndex);
        lastIndex++;
        panels.add(panel);
        if (hexPanelInit != null) {
            hexPanelInit.init(panel);
        }
        editorViewHandling.addEditorView(panel);

        return panel;
    }

    public void init() {
        activePanel = createNewPanel();
        activePanel.newFile();
    }

    public EditorMode getEditorMode() {
        return editorMode;
    }

    public void setEditorMode(EditorMode editorMode) {
        this.editorMode = editorMode;
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

    /**
     * Method for initialization of new hexadecimal panel.
     */
    public static interface HexPanelInit {

        void init(HexPanel panel);
    }

    public static enum EditorMode {
        SINGLE, MULTI
    }
}
