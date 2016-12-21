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
package org.exbin.framework.deltahex.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import org.exbin.deltahex.ScrollBarVisibility;
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.deltahex.swing.ColorsGroup;
import org.exbin.framework.deltahex.DeltaHexModule;
import org.exbin.framework.deltahex.panel.HexSearchComboBoxPanel;
import org.exbin.framework.deltahex.panel.ReplaceParameters;
import org.exbin.framework.deltahex.panel.SearchCondition;
import org.exbin.framework.deltahex.panel.SearchHistoryModel;
import org.exbin.framework.deltahex.panel.SearchParameters;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.utils.binary_data.ByteArrayEditableData;

/**
 * Find text/hexadecimal options dialog.
 *
 * @version 0.2.0 2016/07/21
 * @author ExBin Project (http://exbin.org)
 */
public class FindHexDialog extends javax.swing.JDialog {

    private int dialogOption = JOptionPane.CLOSED_OPTION;
    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(FindHexDialog.class);

    private final CodeArea findHexadecimalRenderer = new CodeArea();
    private HexSearchComboBoxPanel findComboBoxEditorComponent;
    private ComboBoxEditor findComboBoxEditor;
    private List<SearchCondition> searchHistory = new ArrayList<>();

    private final CodeArea replaceHexadecimalRenderer = new CodeArea();
    private HexSearchComboBoxPanel replaceComboBoxEditorComponent;
    private ComboBoxEditor replaceComboBoxEditor;
    private List<SearchCondition> replaceHistory = new ArrayList<>();

    private DeltaHexModule.CodeAreaPopupMenuHandler hexCodePopupMenuHandler;

    public FindHexDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
    }

    private void init() {
        WindowUtils.initWindow(this);
        WindowUtils.addHeaderPanel(this, resourceBundle.getString("header.title"), resourceBundle.getString("header.description"), resourceBundle.getString("header.icon"));

        findHexadecimalRenderer.setShowHeader(false);
        findHexadecimalRenderer.setShowLineNumbers(false);
        findHexadecimalRenderer.setWrapMode(true);
        findHexadecimalRenderer.setBackgroundMode(CodeArea.BackgroundMode.PLAIN);
        findHexadecimalRenderer.setVerticalScrollBarVisibility(ScrollBarVisibility.NEVER);
        findHexadecimalRenderer.setHorizontalScrollBarVisibility(ScrollBarVisibility.NEVER);
        findHexadecimalRenderer.setData(new ByteArrayEditableData());

        findComboBoxEditorComponent = new HexSearchComboBoxPanel();
        findComboBox.setRenderer(new ListCellRenderer<SearchCondition>() {
            private final DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();

            @Override
            public Component getListCellRendererComponent(JList<? extends SearchCondition> list, SearchCondition value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value.getSearchMode() == SearchCondition.SearchMode.TEXT) {
                    return listCellRenderer.getListCellRendererComponent(list, value.getSearchText(), index, isSelected, cellHasFocus);
                } else {
                    findHexadecimalRenderer.setData(value.getBinaryData());
                    findHexadecimalRenderer.setPreferredSize(new Dimension(200, 20));
                    Color backgroundColor;
                    if (isSelected) {
                        backgroundColor = list.getSelectionBackground();
                    } else {
                        backgroundColor = list.getBackground();
                    }
                    ColorsGroup mainColors = findHexadecimalRenderer.getMainColors();
                    mainColors.setBothBackgroundColors(backgroundColor);
                    findHexadecimalRenderer.setMainColors(mainColors);
                    return findHexadecimalRenderer;
                }
            }
        });
        findComboBoxEditor = new ComboBoxEditor() {

            @Override
            public Component getEditorComponent() {
                return findComboBoxEditorComponent;
            }

            @Override
            public void setItem(Object item) {
                findComboBoxEditorComponent.setItem((SearchCondition) item);
                updateFindStatus();
            }

            @Override
            public Object getItem() {
                return findComboBoxEditorComponent.getItem();
            }

            @Override
            public void selectAll() {
                findComboBoxEditorComponent.selectAll();
            }

            @Override
            public void addActionListener(ActionListener l) {
            }

            @Override
            public void removeActionListener(ActionListener l) {
            }
        };
        findComboBox.setEditor(findComboBoxEditor);
        findComboBox.setModel(new SearchHistoryModel(searchHistory));

        replaceHexadecimalRenderer.setShowHeader(false);
        replaceHexadecimalRenderer.setShowLineNumbers(false);
        replaceHexadecimalRenderer.setWrapMode(true);
        replaceHexadecimalRenderer.setBackgroundMode(CodeArea.BackgroundMode.PLAIN);
        replaceHexadecimalRenderer.setVerticalScrollBarVisibility(ScrollBarVisibility.NEVER);
        replaceHexadecimalRenderer.setHorizontalScrollBarVisibility(ScrollBarVisibility.NEVER);
        replaceHexadecimalRenderer.setData(new ByteArrayEditableData());

        replaceComboBoxEditorComponent = new HexSearchComboBoxPanel();
        replaceComboBox.setRenderer(new ListCellRenderer<SearchCondition>() {
            private final DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();

            @Override
            public Component getListCellRendererComponent(JList<? extends SearchCondition> list, SearchCondition value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value.getSearchMode() == SearchCondition.SearchMode.TEXT) {
                    return listCellRenderer.getListCellRendererComponent(list, value.getSearchText(), index, isSelected, cellHasFocus);
                } else {
                    replaceHexadecimalRenderer.setData(value.getBinaryData());
                    replaceHexadecimalRenderer.setPreferredSize(new Dimension(200, 20));
                    Color backgroundColor;
                    if (isSelected) {
                        backgroundColor = list.getSelectionBackground();
                    } else {
                        backgroundColor = list.getBackground();
                    }
                    ColorsGroup mainColors = replaceHexadecimalRenderer.getMainColors();
                    mainColors.setBothBackgroundColors(backgroundColor);
                    replaceHexadecimalRenderer.setMainColors(mainColors);
                    return replaceHexadecimalRenderer;
                }
            }
        });
        replaceComboBoxEditor = new ComboBoxEditor() {

            @Override
            public Component getEditorComponent() {
                return replaceComboBoxEditorComponent;
            }

            @Override
            public void setItem(Object item) {
                replaceComboBoxEditorComponent.setItem((SearchCondition) item);
                updateReplaceStatus();
            }

            @Override
            public Object getItem() {
                return replaceComboBoxEditorComponent.getItem();
            }

            @Override
            public void selectAll() {
                replaceComboBoxEditorComponent.selectAll();
            }

            @Override
            public void addActionListener(ActionListener l) {
            }

            @Override
            public void removeActionListener(ActionListener l) {
            }
        };
        replaceComboBox.setEditor(replaceComboBoxEditor);
        replaceComboBox.setModel(new SearchHistoryModel(replaceHistory));

        WindowUtils.assignGlobalKeyListener(this, findButton, cancelButton);
        pack();
    }

    public void setSelected() {
        findComboBox.requestFocusInWindow();
        findComboBox.getEditor().selectAll();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        findPanel = new javax.swing.JPanel();
        findMultilineButton = new javax.swing.JButton();
        findComboBox = new javax.swing.JComboBox<>();
        searchFromCursorCheckBox = new javax.swing.JCheckBox();
        matchCaseCheckBox = new javax.swing.JCheckBox();
        multipleMatchesCheckBox = new javax.swing.JCheckBox();
        findLabel = new javax.swing.JLabel();
        searchTypeButton = new javax.swing.JButton();
        replacePanel = new javax.swing.JPanel();
        performReplaceCheckBox = new javax.swing.JCheckBox();
        replaceLabel = new javax.swing.JLabel();
        replaceTypeButton = new javax.swing.JButton();
        replaceComboBox = new javax.swing.JComboBox<>();
        replaceMultilineButton = new javax.swing.JButton();
        replaceAllMatchesCheckBox = new javax.swing.JCheckBox();
        controlPanel = new javax.swing.JPanel();
        findButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setTitle(resourceBundle.getString("Form.title")); // NOI18N
        setModal(true);
        setName("Form"); // NOI18N

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.BorderLayout());

        findPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle.getString("findPanel.border.title"))); // NOI18N
        findPanel.setName("findPanel"); // NOI18N

        findMultilineButton.setText(resourceBundle.getString("FindHexDialog.findMultilineButton.text")); // NOI18N
        findMultilineButton.setToolTipText(resourceBundle.getString("FindHexDialog.findMultilineButton.toolTipText")); // NOI18N
        findMultilineButton.setName("findMultilineButton"); // NOI18N
        findMultilineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findMultilineButtonActionPerformed(evt);
            }
        });

        findComboBox.setEditable(true);
        findComboBox.setMinimumSize(new java.awt.Dimension(136, 30));
        findComboBox.setName("findComboBox"); // NOI18N
        findComboBox.setPreferredSize(new java.awt.Dimension(136, 30));

        searchFromCursorCheckBox.setSelected(true);
        searchFromCursorCheckBox.setText(resourceBundle.getString("searchFromCursorCheckBox.text")); // NOI18N
        searchFromCursorCheckBox.setName("searchFromCursorCheckBox"); // NOI18N

        matchCaseCheckBox.setText(resourceBundle.getString("matchCaseCheckBox.text")); // NOI18N
        matchCaseCheckBox.setName("matchCaseCheckBox"); // NOI18N

        multipleMatchesCheckBox.setSelected(true);
        multipleMatchesCheckBox.setText(resourceBundle.getString("FindHexDialog.multipleMatchesCheckBox.text")); // NOI18N
        multipleMatchesCheckBox.setName("multipleMatchesCheckBox"); // NOI18N

        findLabel.setText(resourceBundle.getString("FindHexDialog.findLabel.text")); // NOI18N
        findLabel.setName("findLabel"); // NOI18N

        searchTypeButton.setText(resourceBundle.getString("FindHexDialog.searchTypeButton.text")); // NOI18N
        searchTypeButton.setToolTipText(resourceBundle.getString("FindHexDialog.searchTypeButton.toolTipText")); // NOI18N
        searchTypeButton.setFocusable(false);
        searchTypeButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        searchTypeButton.setMaximumSize(new java.awt.Dimension(27, 27));
        searchTypeButton.setMinimumSize(new java.awt.Dimension(27, 27));
        searchTypeButton.setName("searchTypeButton"); // NOI18N
        searchTypeButton.setPreferredSize(new java.awt.Dimension(27, 27));
        searchTypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchTypeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout findPanelLayout = new javax.swing.GroupLayout(findPanel);
        findPanel.setLayout(findPanelLayout);
        findPanelLayout.setHorizontalGroup(
            findPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(findPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(findPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(matchCaseCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(searchFromCursorCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(findPanelLayout.createSequentialGroup()
                        .addComponent(searchTypeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(findComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(findMultilineButton))
                    .addComponent(multipleMatchesCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
                    .addGroup(findPanelLayout.createSequentialGroup()
                        .addComponent(findLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        findPanelLayout.setVerticalGroup(
            findPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(findPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(findLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(findPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(findComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(findMultilineButton)
                    .addComponent(searchTypeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(searchFromCursorCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(matchCaseCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(multipleMatchesCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(findPanel, java.awt.BorderLayout.PAGE_START);

        replacePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceBundle.getString("replacePanel.border.title"))); // NOI18N
        replacePanel.setName("replacePanel"); // NOI18N

        performReplaceCheckBox.setText(resourceBundle.getString("performReplaceCheckBox.text")); // NOI18N
        performReplaceCheckBox.setName("performReplaceCheckBox"); // NOI18N
        performReplaceCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                performReplaceCheckBoxActionPerformed(evt);
            }
        });

        replaceLabel.setText(resourceBundle.getString("textToReplaceLabel.text")); // NOI18N
        replaceLabel.setEnabled(false);
        replaceLabel.setName("replaceLabel"); // NOI18N

        replaceTypeButton.setText(resourceBundle.getString("FindHexDialog.replaceTypeButton.text")); // NOI18N
        replaceTypeButton.setToolTipText(resourceBundle.getString("FindHexDialog.replaceTypeButton.toolTipText")); // NOI18N
        replaceTypeButton.setEnabled(false);
        replaceTypeButton.setFocusable(false);
        replaceTypeButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        replaceTypeButton.setMaximumSize(new java.awt.Dimension(27, 27));
        replaceTypeButton.setMinimumSize(new java.awt.Dimension(27, 27));
        replaceTypeButton.setName("replaceTypeButton"); // NOI18N
        replaceTypeButton.setPreferredSize(new java.awt.Dimension(27, 27));
        replaceTypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceTypeButtonActionPerformed(evt);
            }
        });

        replaceComboBox.setEditable(true);
        replaceComboBox.setEnabled(false);
        replaceComboBox.setMinimumSize(new java.awt.Dimension(136, 30));
        replaceComboBox.setName("replaceComboBox"); // NOI18N
        replaceComboBox.setPreferredSize(new java.awt.Dimension(136, 30));

        replaceMultilineButton.setText(resourceBundle.getString("FindHexDialog.replaceMultilineButton.text")); // NOI18N
        replaceMultilineButton.setToolTipText(resourceBundle.getString("FindHexDialog.replaceMultilineButton.toolTipText")); // NOI18N
        replaceMultilineButton.setEnabled(false);
        replaceMultilineButton.setName("replaceMultilineButton"); // NOI18N
        replaceMultilineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceMultilineButtonActionPerformed(evt);
            }
        });

        replaceAllMatchesCheckBox.setText(resourceBundle.getString("replaceAllMatchesCheckBox.text")); // NOI18N
        replaceAllMatchesCheckBox.setEnabled(false);
        replaceAllMatchesCheckBox.setName("replaceAllMatchesCheckBox"); // NOI18N

        javax.swing.GroupLayout replacePanelLayout = new javax.swing.GroupLayout(replacePanel);
        replacePanel.setLayout(replacePanelLayout);
        replacePanelLayout.setHorizontalGroup(
            replacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(replacePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(replacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(performReplaceCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
                    .addGroup(replacePanelLayout.createSequentialGroup()
                        .addComponent(replaceLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(replacePanelLayout.createSequentialGroup()
                        .addComponent(replaceTypeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(replaceComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(replaceMultilineButton))
                    .addComponent(replaceAllMatchesCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        replacePanelLayout.setVerticalGroup(
            replacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(replacePanelLayout.createSequentialGroup()
                .addComponent(performReplaceCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(replaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(replacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(replaceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(replaceMultilineButton)
                    .addComponent(replaceTypeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(replaceAllMatchesCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(replacePanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        controlPanel.setName("controlPanel"); // NOI18N

        findButton.setText(resourceBundle.getString("findButton.text")); // NOI18N
        findButton.setName("findButton"); // NOI18N
        findButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(resourceBundle.getString("cancelButton.text")); // NOI18N
        cancelButton.setName("cancelButton"); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout controlPanelLayout = new javax.swing.GroupLayout(controlPanel);
        controlPanel.setLayout(controlPanelLayout);
        controlPanelLayout.setHorizontalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, controlPanelLayout.createSequentialGroup()
                .addContainerGap(316, Short.MAX_VALUE)
                .addComponent(findButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cancelButton)
                .addContainerGap())
        );
        controlPanelLayout.setVerticalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, controlPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(findButton))
                .addContainerGap())
        );

        getContentPane().add(controlPanel, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dialogOption = JOptionPane.CANCEL_OPTION;
        WindowUtils.closeWindow(this);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void findButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findButtonActionPerformed
        dialogOption = JOptionPane.OK_OPTION;
        WindowUtils.closeWindow(this);
    }//GEN-LAST:event_findButtonActionPerformed

    private void performReplaceCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_performReplaceCheckBoxActionPerformed
        updateReplaceEnablement();
    }//GEN-LAST:event_performReplaceCheckBoxActionPerformed

    private void findMultilineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findMultilineButtonActionPerformed
        SearchCondition condition = (SearchCondition) findComboBoxEditor.getItem();
        HexMultilineDialog multilineDialog = new HexMultilineDialog(WindowUtils.getFrame(this), true);
        multilineDialog.setHexCodePopupMenuHandler(hexCodePopupMenuHandler);
        multilineDialog.setCondition(condition);
        multilineDialog.setVisible(true);
        if (multilineDialog.getDialogOption() == JOptionPane.OK_OPTION) {
            findComboBoxEditorComponent.setItem(multilineDialog.getCondition());
            updateFindStatus();
        }
        multilineDialog.detachMenu();
    }//GEN-LAST:event_findMultilineButtonActionPerformed

    private void searchTypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchTypeButtonActionPerformed
        SearchCondition condition = (SearchCondition) findComboBoxEditor.getItem();
        if (condition.getSearchMode() == SearchCondition.SearchMode.TEXT) {
            condition.setSearchMode(SearchCondition.SearchMode.BINARY);
        } else {
            condition.setSearchMode(SearchCondition.SearchMode.TEXT);
        }
        findComboBoxEditor.setItem(condition);
        findComboBox.setEditor(findComboBoxEditor);
        updateFindStatus();
        findComboBox.repaint();
    }//GEN-LAST:event_searchTypeButtonActionPerformed

    private void replaceTypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceTypeButtonActionPerformed
        SearchCondition condition = (SearchCondition) replaceComboBoxEditor.getItem();
        if (condition.getSearchMode() == SearchCondition.SearchMode.TEXT) {
            condition.setSearchMode(SearchCondition.SearchMode.BINARY);
        } else {
            condition.setSearchMode(SearchCondition.SearchMode.TEXT);
        }
        replaceComboBoxEditor.setItem(condition);
        replaceComboBox.setEditor(replaceComboBoxEditor);
        updateReplaceStatus();
        replaceComboBox.repaint();
    }//GEN-LAST:event_replaceTypeButtonActionPerformed

    private void replaceMultilineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceMultilineButtonActionPerformed
        SearchCondition condition = (SearchCondition) replaceComboBoxEditor.getItem();
        HexMultilineDialog multilineDialog = new HexMultilineDialog(WindowUtils.getFrame(this), true);
        multilineDialog.setHexCodePopupMenuHandler(hexCodePopupMenuHandler);
        multilineDialog.setCondition(condition);
        multilineDialog.setVisible(true);
        if (multilineDialog.getDialogOption() == JOptionPane.OK_OPTION) {
            replaceComboBoxEditorComponent.setItem(multilineDialog.getCondition());
            updateFindStatus();
        }
        multilineDialog.detachMenu();
    }//GEN-LAST:event_replaceMultilineButtonActionPerformed

    private void updateFindStatus() {
        SearchCondition condition = (SearchCondition) findComboBoxEditor.getItem();
        if (condition.getSearchMode() == SearchCondition.SearchMode.TEXT) {
            searchTypeButton.setText("T");
            matchCaseCheckBox.setEnabled(true);
        } else {
            searchTypeButton.setText("B");
            matchCaseCheckBox.setEnabled(false);
        }
    }

    private void updateReplaceStatus() {
        SearchCondition condition = (SearchCondition) replaceComboBoxEditor.getItem();
        if (condition.getSearchMode() == SearchCondition.SearchMode.TEXT) {
            replaceTypeButton.setText("T");
        } else {
            replaceTypeButton.setText("B");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        WindowUtils.invokeWindow(new FindHexDialog(new javax.swing.JFrame(), true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton findButton;
    private javax.swing.JComboBox<SearchCondition> findComboBox;
    private javax.swing.JLabel findLabel;
    private javax.swing.JButton findMultilineButton;
    private javax.swing.JPanel findPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JCheckBox matchCaseCheckBox;
    private javax.swing.JCheckBox multipleMatchesCheckBox;
    private javax.swing.JCheckBox performReplaceCheckBox;
    private javax.swing.JCheckBox replaceAllMatchesCheckBox;
    private javax.swing.JComboBox<SearchCondition> replaceComboBox;
    private javax.swing.JLabel replaceLabel;
    private javax.swing.JButton replaceMultilineButton;
    private javax.swing.JPanel replacePanel;
    private javax.swing.JButton replaceTypeButton;
    private javax.swing.JCheckBox searchFromCursorCheckBox;
    private javax.swing.JButton searchTypeButton;
    // End of variables declaration//GEN-END:variables

    public int getDialogOption() {
        return dialogOption;
    }

    public String getFindText() {
        return (String) findComboBox.getEditor().getItem();
    }

    public boolean getShallReplace() {
        return performReplaceCheckBox.isSelected();
    }

    public SearchParameters getSearchParameters() {
        SearchParameters result = new SearchParameters();
        result.setCondition((SearchCondition) findComboBox.getEditor().getItem());
        result.setSearchFromCursor(searchFromCursorCheckBox.isSelected());
        result.setMatchCase(matchCaseCheckBox.isSelected());
        result.setMultipleMatches(multipleMatchesCheckBox.isSelected());
        return result;
    }

    public void setSearchParameters(SearchParameters parameters) {
        searchFromCursorCheckBox.setSelected(parameters.isSearchFromCursor());
        matchCaseCheckBox.setSelected(parameters.isMatchCase());
        multipleMatchesCheckBox.setSelected(parameters.isMultipleMatches());
        findComboBoxEditorComponent.setItem(parameters.getCondition());
        findComboBox.setEditor(findComboBoxEditor);
        findComboBox.repaint();
        updateFindStatus();
    }

    public ReplaceParameters getReplaceParameters() {
        ReplaceParameters result = new ReplaceParameters();
        result.setCondition((SearchCondition) replaceComboBox.getEditor().getItem());
        result.setPerformReplace(performReplaceCheckBox.isSelected());
        result.setReplaceAll(replaceAllMatchesCheckBox.isSelected());
        return result;
    }

    public void setReplaceParameters(ReplaceParameters parameters) {
        performReplaceCheckBox.setSelected(parameters.isPerformReplace());
        replaceAllMatchesCheckBox.setSelected(parameters.isReplaceAll());
        replaceComboBoxEditorComponent.setItem(parameters.getCondition());
        replaceComboBox.setEditor(replaceComboBoxEditor);
        replaceComboBox.repaint();
        updateReplaceStatus();
        updateReplaceEnablement();
    }

    public void setSearchHistory(List<SearchCondition> searchHistory) {
        this.searchHistory = searchHistory;
        findComboBox.setModel(new SearchHistoryModel(searchHistory));
    }

    public void setHexCodePopupMenuHandler(DeltaHexModule.CodeAreaPopupMenuHandler hexCodePopupMenuHandler) {
        this.hexCodePopupMenuHandler = hexCodePopupMenuHandler;
        findComboBoxEditorComponent.setHexCodePopupMenuHandler(hexCodePopupMenuHandler, "FindHexDialog");
    }

    public void detachMenu() {
        hexCodePopupMenuHandler.dropPopupMenu(".searchFindHexDialog");
    }

    private void updateReplaceEnablement() {
        boolean replaceEnabled = performReplaceCheckBox.isSelected();
        replaceTypeButton.setEnabled(replaceEnabled);
        replaceComboBox.setEnabled(replaceEnabled);
        replaceMultilineButton.setEnabled(replaceEnabled);
        replaceAllMatchesCheckBox.setEnabled(replaceEnabled);
        replaceLabel.setEnabled(replaceEnabled);
    }
}
