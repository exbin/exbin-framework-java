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
import org.exbin.framework.deltahex.panel.SearchCondition;
import org.exbin.framework.deltahex.panel.SearchHistoryModel;
import org.exbin.framework.deltahex.panel.SearchParameters;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.utils.binary_data.ByteArrayEditableData;

/**
 * Find text/hexadecimal options dialog.
 *
 * @version 0.1.0 2016/07/21
 * @author ExBin Project (http://exbin.org)
 */
public class FindHexDialog extends javax.swing.JDialog {

    private int dialogOption = JOptionPane.CLOSED_OPTION;
    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(FindHexDialog.class);

    private final CodeArea hexadecimalRenderer = new CodeArea();
    private HexSearchComboBoxPanel comboBoxEditorComponent;
    private List<SearchCondition> searchHistory = new ArrayList<>();
    private ComboBoxEditor comboBoxEditor;
    private DeltaHexModule.CodeAreaPopupMenuHandler hexCodePopupMenuHandler;

    public FindHexDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
    }

    private void init() {
        WindowUtils.initWindow(this);
        WindowUtils.addHeaderPanel(this, resourceBundle.getString("header.title"), resourceBundle.getString("header.description"), resourceBundle.getString("header.icon"));

        hexadecimalRenderer.setShowHeader(false);
        hexadecimalRenderer.setShowLineNumbers(false);
        hexadecimalRenderer.setWrapMode(true);
        hexadecimalRenderer.setBackgroundMode(CodeArea.BackgroundMode.PLAIN);
        hexadecimalRenderer.setVerticalScrollBarVisibility(ScrollBarVisibility.NEVER);
        hexadecimalRenderer.setHorizontalScrollBarVisibility(ScrollBarVisibility.NEVER);
        hexadecimalRenderer.setData(new ByteArrayEditableData());

        comboBoxEditorComponent = new HexSearchComboBoxPanel();

        findComboBox.setRenderer(new ListCellRenderer<SearchCondition>() {
            private final DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();

            @Override
            public Component getListCellRendererComponent(JList<? extends SearchCondition> list, SearchCondition value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value.getSearchMode() == SearchCondition.SearchMode.TEXT) {
                    return listCellRenderer.getListCellRendererComponent(list, value.getSearchText(), index, isSelected, cellHasFocus);
                } else {
                    hexadecimalRenderer.setData(value.getBinaryData());
                    hexadecimalRenderer.setPreferredSize(new Dimension(200, 20));
                    Color backgroundColor;
                    if (isSelected) {
                        backgroundColor = list.getSelectionBackground();
                    } else {
                        backgroundColor = list.getBackground();
                    }
                    ColorsGroup mainColors = hexadecimalRenderer.getMainColors();
                    mainColors.setBothBackgroundColors(backgroundColor);
                    hexadecimalRenderer.setMainColors(mainColors);
                    return hexadecimalRenderer;
                }
            }
        });

        comboBoxEditor = new ComboBoxEditor() {

            @Override
            public Component getEditorComponent() {
                return comboBoxEditorComponent;
            }

            @Override
            public void setItem(Object item) {
                comboBoxEditorComponent.setItem((SearchCondition) item);
                updateFindStatus();
            }

            @Override
            public Object getItem() {
                return comboBoxEditorComponent.getItem();
            }

            @Override
            public void selectAll() {
                comboBoxEditorComponent.selectAll();
            }

            @Override
            public void addActionListener(ActionListener l) {
            }

            @Override
            public void removeActionListener(ActionListener l) {
            }
        };
        findComboBox.setEditor(comboBoxEditor);
        findComboBox.setModel(new SearchHistoryModel(searchHistory));

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
        findTextMultilineButton = new javax.swing.JButton();
        findComboBox = new javax.swing.JComboBox<>();
        searchFromCursorCheckBox = new javax.swing.JCheckBox();
        matchCaseCheckBox = new javax.swing.JCheckBox();
        multipleMatchesCheckBox = new javax.swing.JCheckBox();
        findLabel = new javax.swing.JLabel();
        searchTypeButton = new javax.swing.JButton();
        replacePanel = new javax.swing.JPanel();
        performReplaceCheckBox = new javax.swing.JCheckBox();
        textToReplaceLabel = new javax.swing.JLabel();
        textToReplaceTextField = new javax.swing.JTextField();
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

        findTextMultilineButton.setText(resourceBundle.getString("FindHexDialog.findTextMultilineButton.text")); // NOI18N
        findTextMultilineButton.setToolTipText(resourceBundle.getString("FindHexDialog.findTextMultilineButton.toolTipText")); // NOI18N
        findTextMultilineButton.setName("findTextMultilineButton"); // NOI18N
        findTextMultilineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findTextMultilineButtonActionPerformed(evt);
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
                        .addComponent(findTextMultilineButton))
                    .addComponent(multipleMatchesCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
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
                    .addComponent(findTextMultilineButton)
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
        performReplaceCheckBox.setEnabled(false);
        performReplaceCheckBox.setName("performReplaceCheckBox"); // NOI18N
        performReplaceCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                performReplaceCheckBoxActionPerformed(evt);
            }
        });

        textToReplaceLabel.setText(resourceBundle.getString("textToReplaceLabel.text")); // NOI18N
        textToReplaceLabel.setName("textToReplaceLabel"); // NOI18N

        textToReplaceTextField.setEnabled(false);
        textToReplaceTextField.setName("textToReplaceTextField"); // NOI18N

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
                    .addComponent(performReplaceCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                    .addComponent(textToReplaceLabel)
                    .addComponent(textToReplaceTextField)
                    .addComponent(replaceAllMatchesCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        replacePanelLayout.setVerticalGroup(
            replacePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(replacePanelLayout.createSequentialGroup()
                .addComponent(performReplaceCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textToReplaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textToReplaceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addContainerGap(395, Short.MAX_VALUE)
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
        textToReplaceTextField.setEnabled(performReplaceCheckBox.isSelected());
        textToReplaceLabel.setEnabled(performReplaceCheckBox.isSelected());
    }//GEN-LAST:event_performReplaceCheckBoxActionPerformed

    private void findTextMultilineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findTextMultilineButtonActionPerformed
        SearchCondition condition = (SearchCondition) comboBoxEditor.getItem();
        HexMultilineDialog multilineDialog = new HexMultilineDialog(WindowUtils.getFrame(this), true);
        multilineDialog.setHexCodePopupMenuHandler(hexCodePopupMenuHandler);
        multilineDialog.setCondition(condition);
        multilineDialog.setVisible(true);
        if (multilineDialog.getDialogOption() == JOptionPane.OK_OPTION) {
            comboBoxEditorComponent.setItem(multilineDialog.getCondition());
            updateFindStatus();
        }
        multilineDialog.detachMenu();
    }//GEN-LAST:event_findTextMultilineButtonActionPerformed

    private void searchTypeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchTypeButtonActionPerformed
        SearchCondition condition = (SearchCondition) comboBoxEditor.getItem();
        if (condition.getSearchMode() == SearchCondition.SearchMode.TEXT) {
            condition.setSearchMode(SearchCondition.SearchMode.BINARY);
        } else {
            condition.setSearchMode(SearchCondition.SearchMode.TEXT);
        }
        comboBoxEditor.setItem(condition);
        findComboBox.setEditor(comboBoxEditor);
        updateFindStatus();
        findComboBox.repaint();
    }//GEN-LAST:event_searchTypeButtonActionPerformed

    private void updateFindStatus() {
        SearchCondition condition = (SearchCondition) comboBoxEditor.getItem();
        if (condition.getSearchMode() == SearchCondition.SearchMode.TEXT) {
            searchTypeButton.setText("T");
            matchCaseCheckBox.setEnabled(true);
        } else {
            searchTypeButton.setText("B");
            matchCaseCheckBox.setEnabled(false);
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
    private javax.swing.JPanel findPanel;
    private javax.swing.JButton findTextMultilineButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JCheckBox matchCaseCheckBox;
    private javax.swing.JCheckBox multipleMatchesCheckBox;
    private javax.swing.JCheckBox performReplaceCheckBox;
    private javax.swing.JCheckBox replaceAllMatchesCheckBox;
    private javax.swing.JPanel replacePanel;
    private javax.swing.JCheckBox searchFromCursorCheckBox;
    private javax.swing.JButton searchTypeButton;
    private javax.swing.JLabel textToReplaceLabel;
    private javax.swing.JTextField textToReplaceTextField;
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

    public void setShallReplace(boolean shallReplace) {
        // TODO support for replace
//        performReplaceCheckBox.setSelected(shallReplace);
//        performReplaceCheckBoxActionPerformed(null);
    }

    public String getReplaceText() {
        return textToReplaceTextField.getText();
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
        comboBoxEditorComponent.setItem(parameters.getCondition());
        findComboBox.setEditor(comboBoxEditor);
        findComboBox.repaint();
        updateFindStatus();
    }

    public void setSearchHistory(List<SearchCondition> searchHistory) {
        this.searchHistory = searchHistory;
        findComboBox.setModel(new SearchHistoryModel(searchHistory));
    }

    public void setHexCodePopupMenuHandler(DeltaHexModule.CodeAreaPopupMenuHandler hexCodePopupMenuHandler) {
        this.hexCodePopupMenuHandler = hexCodePopupMenuHandler;
        comboBoxEditorComponent.setHexCodePopupMenuHandler(hexCodePopupMenuHandler, "FindHexDialog");
    }

    public void detachMenu() {
        hexCodePopupMenuHandler.dropPopupMenu(".searchFindHexDialog");
    }
}
