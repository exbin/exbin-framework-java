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

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.exbin.framework.deltahex.panel.HexPanel;
import org.exbin.framework.deltahex.panel.HexStatusPanel;
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
        switch (editorMode) {
            case SINGLE:
                return panels.get(0).getPanel();
            case MULTI: {
                return activePanel.getPanel();
            }
            default:
                throw new IllegalStateException("Unexpected editor mode " + editorMode.name());
        }
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        switch (editorMode) {
            case SINGLE: {
                panels.get(0).setPropertyChangeListener(propertyChangeListener);
                break;
            }
            case MULTI: {
                activePanel.setPropertyChangeListener(propertyChangeListener);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected editor mode " + editorMode.name());
        }
    }

    @Override
    public String getWindowTitle(String frameTitle) {
        switch (editorMode) {
            case SINGLE: {
                return panels.get(0).getWindowTitle(frameTitle);
            }
            case MULTI: {
                return activePanel.getWindowTitle(frameTitle);
            }
            default:
                throw new IllegalStateException("Unexpected editor mode " + editorMode.name());
        }
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
        switch (editorMode) {
            case SINGLE: {
                panels.get(0).setFileType(fileType);
                break;
            }
            case MULTI: {
                activePanel.setFileType(fileType);
                break;
            }
            default:
                throw new IllegalStateException("Unexpected editor mode " + editorMode.name());
        }
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
        switch (editorMode) {
            case SINGLE: {
                return panels.get(0).isModified();
            }
            case MULTI: {
                return activePanel.isModified();
            }
            default:
                throw new IllegalStateException("Unexpected editor mode " + editorMode.name());
        }
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
