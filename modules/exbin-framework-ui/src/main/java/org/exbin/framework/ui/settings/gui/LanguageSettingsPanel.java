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
package org.exbin.framework.ui.settings.gui;

import java.awt.Component;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import org.exbin.framework.App;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.ui.model.LanguageRecord;
import org.exbin.framework.utils.TestApplication;
import org.exbin.framework.utils.UtilsModule;
import org.exbin.framework.ui.settings.LanguageOptions;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsModifiedListener;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;
import org.exbin.framework.context.api.ActiveContextProvider;

/**
 * Language settings panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class LanguageSettingsPanel extends javax.swing.JPanel implements SettingsComponent {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(LanguageSettingsPanel.class);
    private SettingsModifiedListener settingsModifiedListener;
    private String defaultLocaleName = "";

    public LanguageSettingsPanel() {
        init();
    }

    private void init() {
        initComponents();
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Override
    public void loadFromOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ActiveContextProvider applicationContextProvider) {
        LanguageOptions options = settingsOptionsProvider.getSettingsOptions(LanguageOptions.class);
        Locale languageLocale = options.getLocale();
        ComboBoxModel<LanguageRecord> languageComboBoxModel = languageComboBox.getModel();
        for (int i = 0; i < languageComboBoxModel.getSize(); i++) {
            LanguageRecord languageRecord = languageComboBoxModel.getElementAt(i);
            if (languageLocale.equals(languageRecord.getLocale())) {
                languageComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    @Override
    public void saveToOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ActiveContextProvider applicationContextProvider) {
        LanguageOptions options = settingsOptionsProvider.getSettingsOptions(LanguageOptions.class);
        options.setLocale(((LanguageRecord) languageComboBox.getSelectedItem()).getLocale());
    }

    public void setLanguageLocales(List<LanguageRecord> languageLocales) {
        DefaultComboBoxModel<LanguageRecord> languageComboBoxModel = new DefaultComboBoxModel<>();
        languageLocales.forEach((language) -> {
            languageComboBoxModel.addElement(language);
        });
        languageComboBox.setModel(languageComboBoxModel);
        languageComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                DefaultListCellRenderer renderer = (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                LanguageRecord record = (LanguageRecord) value;
                String languageText = record.getText();
                if ("".equals(languageText)) {
                    languageText = defaultLocaleName;
                }
                renderer.setText(languageText);
                Optional<ImageIcon> flag = record.getFlag();
                if (flag.isPresent()) {
                    renderer.setIcon(flag.get());
                }
                return renderer;
            }
        });
    }

    public void setDefaultLocaleName(String defaultLocaleName) {
        this.defaultLocaleName = defaultLocaleName;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainOptionsBasicPanel = new javax.swing.JPanel();
        languageLabel = new javax.swing.JLabel();
        languageComboBox = new javax.swing.JComboBox<>();

        setLayout(new java.awt.BorderLayout());

        languageLabel.setText(resourceBundle.getString("languageLabel.text") + " *"); // NOI18N

        languageComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                languageComboBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout mainOptionsBasicPanelLayout = new javax.swing.GroupLayout(mainOptionsBasicPanel);
        mainOptionsBasicPanel.setLayout(mainOptionsBasicPanelLayout);
        mainOptionsBasicPanelLayout.setHorizontalGroup(
            mainOptionsBasicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainOptionsBasicPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainOptionsBasicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(languageComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(mainOptionsBasicPanelLayout.createSequentialGroup()
                        .addComponent(languageLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        mainOptionsBasicPanelLayout.setVerticalGroup(
            mainOptionsBasicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainOptionsBasicPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(languageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(languageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(mainOptionsBasicPanel, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    private void languageComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_languageComboBoxItemStateChanged
        notifyModified();
    }//GEN-LAST:event_languageComboBoxItemStateChanged

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TestApplication testApplication = UtilsModule.createTestApplication();
        testApplication.launch(() -> {
            testApplication.addModule(org.exbin.framework.language.api.LanguageModuleApi.MODULE_ID, new org.exbin.framework.language.api.utils.TestLanguageModule());
            WindowUtils.invokeWindow(new LanguageSettingsPanel());
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<LanguageRecord> languageComboBox;
    private javax.swing.JLabel languageLabel;
    private javax.swing.JPanel mainOptionsBasicPanel;
    // End of variables declaration//GEN-END:variables

    private void notifyModified() {
        if (settingsModifiedListener != null) {
            settingsModifiedListener.wasModified();
        }
    }

    @Override
    public void setSettingsModifiedListener(SettingsModifiedListener listener) {
        settingsModifiedListener = listener;
    }
}
