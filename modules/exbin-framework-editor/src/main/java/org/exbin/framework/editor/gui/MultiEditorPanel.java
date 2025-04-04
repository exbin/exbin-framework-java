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
package org.exbin.framework.editor.gui;

import java.awt.Component;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.file.api.FileHandler;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UiUtils;

/**
 * Multi editor panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MultiEditorPanel extends javax.swing.JPanel {

    private Controller controller;
    private int activeIndex = -1;

    public MultiEditorPanel() {
        initComponents();
        init();
    }

    private void init() {
        tabbedPane.addChangeListener((e) -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            changeActiveIndex(selectedIndex);
        });

        tabbedPane.setComponentPopupMenu(UiUtils.createPopupMenu((invoker, x, y) -> {
            int index = tabbedPane.indexAtLocation(x, y);
            if (controller != null) {
                controller.showPopupMenu(index, invoker, x, y);
            }
        }));
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void addFileHandler(FileHandler fileHandler, String text) {
        tabbedPane.addTab(text, fileHandler.getComponent());
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    public void insertFileHandler(int position, FileHandler fileHandler, String text) {
        tabbedPane.insertTab(text, null, fileHandler.getComponent(), null, position);
        tabbedPane.setSelectedIndex(position);
    }

    public void removeFileHandlerAt(int index) {
        if (index == activeIndex) {
            changeActiveIndex(-1);
        } else if (index < activeIndex) {
            changeActiveIndex(activeIndex--);
        }

        tabbedPane.removeTabAt(index);
    }

    public void removeAllFileHandlers() {
        tabbedPane.removeAll();
        changeActiveIndex(-1);
    }

    public void removeAllFileHandlersExceptFile(int index) {
        for (int i = tabbedPane.getTabCount() - 1; i >= 0; i--) {
            if (i != index) {
                removeFileHandlerAt(i);
            }
        }
    }

    private void changeActiveIndex(int index) {
        if (activeIndex != index) {
            activeIndex = index;
            notifyActiveIndexChanged();
        }
    }

    private void notifyActiveIndexChanged() {
        if (controller != null) {
            controller.activeIndexChanged(activeIndex);
        }
    }

    public void updateFileHandlerAt(int index, String text) {
        Component component = tabbedPane.getTabComponentAt(index);
        component.setName(text);
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();

        setPreferredSize(new java.awt.Dimension(400, 300));
        setLayout(new java.awt.BorderLayout());
        add(tabbedPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestApplication.run(() -> WindowUtils.invokeWindow(new MultiEditorPanel()));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables

    @ParametersAreNonnullByDefault
    public interface Controller {

        void activeIndexChanged(int index);

        void showPopupMenu(int index, Component component, int positionX, int positionY);
    }
}
