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
package org.exbin.framework.editor.xbup.gui.cell;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.exbin.framework.gui.utils.WindowUtils;

/**
 * Empty property column panel with operation button.
 *
 * @version 0.2.1 2019/07/13
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PropertyTableCellPanel extends javax.swing.JPanel {

    private int paramIndex;
    private JComponent cellComponent;

    public PropertyTableCellPanel() {
        this(new JLabel());
    }

    public PropertyTableCellPanel(JComponent cellComponent) {
        this.cellComponent = cellComponent;
        initComponents();
        init();
    }

    private void init() {
        add(cellComponent, BorderLayout.CENTER);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editorButton = new javax.swing.JButton();

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        editorButton.setText("..."); // NOI18N
        editorButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        editorButton.setName("editorButton"); // NOI18N
        add(editorButton, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeDialog(new PropertyTableCellPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton editorButton;
    // End of variables declaration//GEN-END:variables

    public void setEditorAction(ActionListener actionListener) {
        editorButton.addActionListener(actionListener);
    }

    public int getParamIndex() {
        return paramIndex;
    }

    public void setParamIndex(int paramIndex) {
        this.paramIndex = paramIndex;
    }

    @Nonnull
    public JComponent getCellComponent() {
        return cellComponent;
    }
}