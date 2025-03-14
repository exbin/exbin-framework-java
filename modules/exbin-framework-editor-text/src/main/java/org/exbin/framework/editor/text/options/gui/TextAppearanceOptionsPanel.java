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
package org.exbin.framework.editor.text.options.gui;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.editor.text.options.TextAppearanceOptions;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.options.api.OptionsModifiedListener;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;

/**
 * Text encoding options panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextAppearanceOptionsPanel extends javax.swing.JPanel implements OptionsComponent<TextAppearanceOptions> {

    private OptionsModifiedListener optionsModifiedListener;
    private ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(TextAppearanceOptionsPanel.class);

    public TextAppearanceOptionsPanel() {
        initComponents();
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Override
    public void saveToOptions(TextAppearanceOptions options) {
        options.setWordWrapping(wordWrapCheckBox.isSelected());
    }

    @Override
    public void loadFromOptions(TextAppearanceOptions options) {
        wordWrapCheckBox.setSelected(options.isWordWrapping());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        wordWrapCheckBox = new javax.swing.JCheckBox();

        setName("Form"); // NOI18N

        wordWrapCheckBox.setSelected(true);
        wordWrapCheckBox.setText(resourceBundle.getString("wordWrapCheckBox.text")); // NOI18N
        wordWrapCheckBox.setName("wordWrapCheckBox"); // NOI18N
        wordWrapCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                wordWrapCheckBoxjCheckBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wordWrapCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wordWrapCheckBox))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void wordWrapCheckBoxjCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_wordWrapCheckBoxjCheckBoxItemStateChanged
        notifyModified();
    }//GEN-LAST:event_wordWrapCheckBoxjCheckBoxItemStateChanged

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestApplication testApplication = UtilsModule.createTestApplication();
        testApplication.launch(() -> {
            testApplication.addModule(org.exbin.framework.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.framework.language.api.utils.TestLanguageModule());
            WindowUtils.invokeWindow(new TextAppearanceOptionsPanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox wordWrapCheckBox;
    // End of variables declaration//GEN-END:variables

    private void notifyModified() {
        if (optionsModifiedListener != null) {
            optionsModifiedListener.wasModified();
        }
    }

    @Override
    public void setOptionsModifiedListener(OptionsModifiedListener listener) {
        optionsModifiedListener = listener;
    }
}
