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
package org.exbin.jaguif.docking.multi.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Icon;
import org.exbin.jaguif.App;
import org.exbin.jaguif.document.api.ComponentDocument;
import org.exbin.jaguif.menu.api.MenuModuleApi;

/**
 * Multi document panel.
 */
@ParametersAreNonnullByDefault
public class MultiDocumentPanel extends javax.swing.JPanel {

    protected Controller controller;
    protected int activeIndex = -1;

    public MultiDocumentPanel() {
        initComponents();
        init();
    }

    private void init() {
        tabbedPane.addChangeListener((e) -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            changeActiveIndex(selectedIndex);
        });

        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        tabbedPane.setComponentPopupMenu(menuModule.getMenuBuilder().createPopupMenu((invoker, x, y) -> {
            if (controller != null) {
                int index = tabbedPane.indexAtLocation(x, y);
                controller.showPopupMenu(index, invoker, x, y);
            }
        }));

        MouseAdapter dragAdapter = new MouseAdapter() {

            private boolean isDragging = false;
            private int dragTabIndex = -1;
            private Cursor originalCursor;

            @Override
            public void mousePressed(MouseEvent e) {
                dragTabIndex = tabbedPane.indexAtLocation(e.getX(), e.getY());
                if (isDragging) {
                    tabbedPane.setCursor(originalCursor);
                    isDragging = false;
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragTabIndex == -1) {
                    return;
                }

                if (!isDragging) {
                    isDragging = true;
                    originalCursor = tabbedPane.getCursor();
                    tabbedPane.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }

                int currentTabIndex = tabbedPane.indexAtLocation(e.getX(), e.getY());
                if (currentTabIndex != -1 && currentTabIndex != dragTabIndex) {
                    Rectangle tabBounds = tabbedPane.getBoundsAt(dragTabIndex);
                    if (tabBounds != null && tabbedPane.indexAtLocation(e.getX() + tabBounds.width, e.getY()) == currentTabIndex) {
                        return;
                    }

                    moveDocument(dragTabIndex, currentTabIndex);
                    tabbedPane.setSelectedIndex(currentTabIndex);
                    dragTabIndex = currentTabIndex;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragTabIndex = -1;
                if (isDragging) {
                    tabbedPane.setCursor(originalCursor);
                    isDragging = false;
                }
            }
        };
        tabbedPane.addMouseListener(dragAdapter);
        tabbedPane.addMouseMotionListener(dragAdapter);
    }

    public void moveDocument(int position, int targetPosition) {
        Component comp = tabbedPane.getComponentAt(position);
        String title = tabbedPane.getTitleAt(position);
        Icon icon = tabbedPane.getIconAt(position);
        String tip = tabbedPane.getToolTipTextAt(position);
        boolean isEnabled = tabbedPane.isEnabledAt(position);
        Component tabComponent = tabbedPane.getTabComponentAt(position);

        tabbedPane.removeTabAt(position);
        tabbedPane.insertTab(title, icon, comp, tip, targetPosition);
        tabbedPane.setEnabledAt(targetPosition, isEnabled);
        tabbedPane.setTabComponentAt(targetPosition, tabComponent);
        if (controller != null) {
            controller.documentMoved(position, targetPosition);
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void addDocument(ComponentDocument document, String text) {
        tabbedPane.addTab(text, document.getComponent());
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    public void insertDocument(int position, ComponentDocument document, String text) {
        tabbedPane.insertTab(text, null, document.getComponent(), null, position);
        tabbedPane.setSelectedIndex(position);
    }

    public void removeDocumentAtIndex(int index) {
        changeActiveIndex(-1);
        tabbedPane.removeTabAt(index);
        changeActiveIndex(tabbedPane.getSelectedIndex());
    }

    public void removeAllDocuments() {
        tabbedPane.removeAll();
        changeActiveIndex(-1);
    }

    public void removeAllExceptIndex(int index) {
        for (int i = tabbedPane.getTabCount() - 1; i >= 0; i--) {
            if (i != index) {
                removeDocumentAtIndex(i);
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

    public void setDocumentName(int index, String text) {
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables

    @ParametersAreNonnullByDefault
    public interface Controller {

        void activeIndexChanged(int index);

        void showPopupMenu(int index, Component component, int positionX, int positionY);

        void documentMoved(int position, int targetPosition);
    }
}
