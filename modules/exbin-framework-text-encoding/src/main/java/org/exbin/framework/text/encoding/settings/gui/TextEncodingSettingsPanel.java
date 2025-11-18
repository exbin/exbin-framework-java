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
package org.exbin.framework.text.encoding.settings.gui;

import org.exbin.framework.text.encoding.gui.TextEncodingListPanel;
import java.awt.BorderLayout;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.text.encoding.settings.TextEncodingOptions;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsModifiedListener;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;
import org.exbin.framework.options.settings.api.VerticallyExpandable;
import org.exbin.framework.context.api.ActiveContextProvider;
import org.exbin.framework.options.settings.api.SettingsPanelUpdater;
import org.exbin.framework.text.encoding.ContextEncoding;
import org.exbin.framework.text.encoding.CharsetEncodingState;
import org.exbin.framework.text.encoding.CharsetListEncodingState;
import org.exbin.framework.text.encoding.settings.TextEncodingListSettingsApplier;
import org.exbin.framework.text.encoding.settings.TextEncodingSettingsApplier;

/**
 * Text encoding settings panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextEncodingSettingsPanel extends javax.swing.JPanel implements SettingsComponent, VerticallyExpandable {

    private SettingsModifiedListener settingsModifiedListener;
    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(TextEncodingSettingsPanel.class);
    private final TextEncodingListPanel encodingPanel;
    private final DefaultEncodingComboBoxModel encodingComboBoxModel = new DefaultEncodingComboBoxModel();
    private ContextEncoding availableContextEncoding = null;

    public TextEncodingSettingsPanel() {
        encodingPanel = new TextEncodingListPanel();

        initComponents();
        init();
    }

    private void init() {
        encodingPanel.setEnabled(false);
        encodingPanel.setSettingsModifiedListener(() -> {
            notifyModified();
            updateEncodings();
        });
        super.add(encodingPanel, BorderLayout.CENTER);
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setListPanelController(TextEncodingListPanel.Controller listPanelController) {
        encodingPanel.setController(listPanelController);
    }

    @Override
    public void loadFromOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ActiveContextProvider contextProvider) {
        TextEncodingOptions options = settingsOptionsProvider.getSettingsOptions(TextEncodingOptions.class);
        encodingPanel.loadFromOptions(settingsOptionsProvider, contextProvider);
        defaultEncodingComboBox.setSelectedItem(options.getSelectedEncoding());

        if (contextProvider != null) {
            ContextEncoding contextEncoding = contextProvider.getActiveState(ContextEncoding.class);
            if (contextEncoding instanceof CharsetEncodingState) {
                SettingsPanelUpdater updater = new SettingsPanelUpdater(this::notifyModified);
                CharsetEncodingState state = (CharsetEncodingState) contextEncoding;
                updater.setComboBoxValue(defaultEncodingComboBox, state.getEncoding());
            }
            if (contextEncoding instanceof CharsetListEncodingState) {
                CharsetListEncodingState state = (CharsetListEncodingState) contextEncoding;
                List<String> encodings = state.getEncodings();
                if (!encodings.equals(encodingPanel.getEncodingList())) {
                    encodingPanel.setEncodingList(encodings);
                    notifyModified();
                }
            }
        }

        boolean fillCurrentEncoding = false;
        boolean fillCurrentEncodings = false;
        if (contextProvider != null) {
            ContextEncoding contextEncoding = contextProvider.getActiveState(ContextEncoding.class);
            if (contextEncoding != null) {
                this.availableContextEncoding = contextEncoding;
                if (contextEncoding instanceof CharsetEncodingState) {
                    fillCurrentEncoding = true;
                }
                if (contextEncoding instanceof CharsetListEncodingState) {
                    fillCurrentEncodings = true;
                }
            }
        }
        fillCurrentEncodingButton.setEnabled(fillCurrentEncoding);
        fillCurrentEncodingsButton.setEnabled(fillCurrentEncodings);
        updateEncodings();
    }

    @Override
    public void saveToOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ActiveContextProvider contextProvider) {
        TextEncodingOptions options = settingsOptionsProvider.getSettingsOptions(TextEncodingOptions.class);
        encodingPanel.saveToOptions(settingsOptionsProvider, contextProvider);
        options.setSelectedEncoding((String) defaultEncodingComboBox.getSelectedItem());

        if (contextProvider != null) {
            ContextEncoding contextEncoding = contextProvider.getActiveState(ContextEncoding.class);
            if (contextEncoding instanceof CharsetEncodingState) {
                TextEncodingSettingsApplier applier = new TextEncodingSettingsApplier();
                applier.applySettings(contextEncoding, settingsOptionsProvider);
                contextProvider.notifyStateChange(ContextEncoding.class, CharsetEncodingState.ChangeType.ENCODING);
            }
            if (contextEncoding instanceof CharsetListEncodingState) {
                TextEncodingListSettingsApplier applier = new TextEncodingListSettingsApplier();
                applier.applySettings(contextEncoding, settingsOptionsProvider);
                contextProvider.notifyStateChange(ContextEncoding.class, CharsetListEncodingState.ChangeType.ENCODING_LIST);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        defaultEncodingPanel = new javax.swing.JPanel();
        defaultEncodingComboBox = new javax.swing.JComboBox<>();
        defaultEncodingLabel = new javax.swing.JLabel();
        fillCurrentEncodingButton = new javax.swing.JButton();
        encodingsControlPanel = new javax.swing.JPanel();
        fillCurrentEncodingsButton = new javax.swing.JButton();

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        defaultEncodingPanel.setName("defaultEncodingPanel"); // NOI18N

        defaultEncodingComboBox.setModel(encodingComboBoxModel);
        defaultEncodingComboBox.setName("defaultEncodingComboBox"); // NOI18N
        defaultEncodingComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                defaultEncodingComboBoxItemStateChanged(evt);
            }
        });

        defaultEncodingLabel.setText(resourceBundle.getString("defaultEncodingLabel.text")); // NOI18N
        defaultEncodingLabel.setName("defaultEncodingLabel"); // NOI18N

        fillCurrentEncodingButton.setText(resourceBundle.getString("fillCurrentEncodingButton.text")); // NOI18N
        fillCurrentEncodingButton.setEnabled(false);
        fillCurrentEncodingButton.setName("fillCurrentEncodingButton"); // NOI18N
        fillCurrentEncodingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fillCurrentEncodingButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout defaultEncodingPanelLayout = new javax.swing.GroupLayout(defaultEncodingPanel);
        defaultEncodingPanel.setLayout(defaultEncodingPanelLayout);
        defaultEncodingPanelLayout.setHorizontalGroup(
            defaultEncodingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(defaultEncodingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(defaultEncodingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(defaultEncodingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(defaultEncodingPanelLayout.createSequentialGroup()
                        .addComponent(fillCurrentEncodingButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(defaultEncodingComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        defaultEncodingPanelLayout.setVerticalGroup(
            defaultEncodingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(defaultEncodingPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(defaultEncodingLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(defaultEncodingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fillCurrentEncodingButton))
        );

        add(defaultEncodingPanel, java.awt.BorderLayout.NORTH);

        encodingsControlPanel.setName("encodingsControlPanel"); // NOI18N

        fillCurrentEncodingsButton.setText(resourceBundle.getString("fillCurrentEncodingsButton.text")); // NOI18N
        fillCurrentEncodingsButton.setEnabled(false);
        fillCurrentEncodingsButton.setName("fillCurrentEncodingsButton"); // NOI18N
        fillCurrentEncodingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fillCurrentEncodingsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout encodingsControlPanelLayout = new javax.swing.GroupLayout(encodingsControlPanel);
        encodingsControlPanel.setLayout(encodingsControlPanelLayout);
        encodingsControlPanelLayout.setHorizontalGroup(
            encodingsControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(encodingsControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fillCurrentEncodingsButton)
                .addContainerGap(82, Short.MAX_VALUE))
        );
        encodingsControlPanelLayout.setVerticalGroup(
            encodingsControlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(encodingsControlPanelLayout.createSequentialGroup()
                .addComponent(fillCurrentEncodingsButton)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        add(encodingsControlPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void fillCurrentEncodingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fillCurrentEncodingsButtonActionPerformed
        encodingPanel.setEncodingList(((CharsetListEncodingState) availableContextEncoding).getEncodings());
        encodingPanel.repaint();
        updateEncodings();
        notifyModified();
    }//GEN-LAST:event_fillCurrentEncodingsButtonActionPerformed

    private void fillCurrentEncodingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fillCurrentEncodingButtonActionPerformed
        defaultEncodingComboBox.setSelectedItem(((CharsetEncodingState) availableContextEncoding).getEncoding());
        defaultEncodingComboBox.repaint();
        notifyModified();
    }//GEN-LAST:event_fillCurrentEncodingButtonActionPerformed

    private void defaultEncodingComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_defaultEncodingComboBoxItemStateChanged
        notifyModified();
    }//GEN-LAST:event_defaultEncodingComboBoxItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> defaultEncodingComboBox;
    private javax.swing.JLabel defaultEncodingLabel;
    private javax.swing.JPanel defaultEncodingPanel;
    private javax.swing.JPanel encodingsControlPanel;
    private javax.swing.JButton fillCurrentEncodingButton;
    private javax.swing.JButton fillCurrentEncodingsButton;
    // End of variables declaration//GEN-END:variables

    private void notifyModified() {
        if (settingsModifiedListener != null) {
            settingsModifiedListener.notifyModified();
        }
    }

    @Override
    public void setSettingsModifiedListener(SettingsModifiedListener settingsModifiedListener) {
        this.settingsModifiedListener = settingsModifiedListener;
    }

    private void updateEncodings() {
        encodingComboBoxModel.setAvailableEncodings(encodingPanel.getEncodingList());
        defaultEncodingComboBox.repaint();
    }
}
