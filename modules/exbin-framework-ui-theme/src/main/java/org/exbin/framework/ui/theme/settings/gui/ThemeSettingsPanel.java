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
package org.exbin.framework.ui.theme.settings.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import org.exbin.framework.App;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.ui.theme.api.ConfigurableLafProvider;
import org.exbin.framework.ui.theme.settings.ThemeOptions;
import org.exbin.framework.utils.DesktopUtils;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsModifiedListener;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;
import org.exbin.framework.context.api.ActiveContextProvider;

/**
 * UI theme options panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ThemeSettingsPanel extends javax.swing.JPanel implements SettingsComponent {

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ThemeSettingsPanel.class);
    private SettingsModifiedListener settingsModifiedListener;
    private Controller controller;
    private SettingsOptionsProvider settingsOptionsProvider;
    private Map<String, ConfigurableLafProvider> themeOptions = null;

    public ThemeSettingsPanel() {
        init();
    }

    private void init() {
        initComponents();

        if (DesktopUtils.detectBasicOs() == DesktopUtils.OsType.MACOSX) {
            mainOptionsBasicPanel.add(macOsOptionsPanel, BorderLayout.SOUTH);
        }
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Override
    public void loadFromOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ActiveContextProvider contextProvider) {
        this.settingsOptionsProvider = settingsOptionsProvider;
        ThemeOptions options = settingsOptionsProvider.getSettingsOptions(ThemeOptions.class);
        visualThemeComboBox.setSelectedIndex(findMatchingElement(visualThemeComboBox.getModel(), options.getLookAndFeel()));
        iconSetComboBox.setSelectedIndex(findMatchingElement(iconSetComboBox.getModel(), options.getIconSet()));
        renderingModeComboBox.setSelectedIndex(findMatchingElement(renderingModeComboBox.getModel(), options.getRenderingMode()));
        fontAntialiasingComboBox.setSelectedIndex(findMatchingElement(fontAntialiasingComboBox.getModel(), options.getFontAntialiasing()));
        guiScalingComboBox.setSelectedIndex(findMatchingElement(guiScalingComboBox.getModel(), options.getGuiScaling()));
        guiScalingSpinner.setValue(options.getGuiScalingRate());
        if (DesktopUtils.detectBasicOs() == DesktopUtils.OsType.MACOSX) {
            macOsAppearanceComboBox.setSelectedIndex(findMatchingElement(macOsAppearanceComboBox.getModel(), options.getMacOsAppearance()));
            useScreenMenuBarCheckBox.setSelected(options.isUseScreenMenuBar());
        }
    }

    private static int findMatchingElement(ComboBoxModel<String> model, String value) {
        for (int i = 0; i < model.getSize(); i++) {
            if (value.equals(model.getElementAt(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void saveToOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ActiveContextProvider contextProvider) {
        ThemeOptions options = settingsOptionsProvider.getSettingsOptions(ThemeOptions.class);
        options.setLookAndFeel((String) visualThemeComboBox.getSelectedItem());
        options.setIconSet((String) iconSetComboBox.getSelectedItem());
        options.setRenderingMode((String) renderingModeComboBox.getSelectedItem());
        options.setFontAntialiasing((String) fontAntialiasingComboBox.getSelectedItem());
        options.setGuiScaling((String) guiScalingComboBox.getSelectedItem());
        options.setGuiScalingRate((Float) guiScalingSpinner.getValue());
        if (DesktopUtils.detectBasicOs() == DesktopUtils.OsType.MACOSX) {
            options.setMacOsAppearance((String) macOsAppearanceComboBox.getSelectedItem());
            options.setUseScreenMenuBar(useScreenMenuBarCheckBox.isSelected());
        }
    }

    public void setThemes(List<String> themeKeys, List<String> themeNames, Map<String, ConfigurableLafProvider> themeOptions) {
        this.themeOptions = themeOptions;
        DefaultComboBoxModel<String> themeComboBoxModel = new DefaultComboBoxModel<>();
        themeKeys.forEach((themeKey) -> {
            themeComboBoxModel.addElement(themeKey);
        });
        visualThemeComboBox.setModel(themeComboBoxModel);
        visualThemeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Nonnull
            @Override
            public Component getListCellRendererComponent(JList<?> list, @Nullable Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (index >= 0) {
                    return super.getListCellRendererComponent(list, themeNames.get(index), index, isSelected, cellHasFocus);
                }
                int selectedIndex = visualThemeComboBox.getSelectedIndex();
                return super.getListCellRendererComponent(list, selectedIndex >= 0 ? themeNames.get(selectedIndex) : value, index, isSelected, cellHasFocus);
            }
        });
    }

    public void setIconSets(List<String> iconSetKeys, List<String> iconSetNames) {
        DefaultComboBoxModel<String> iconSetComboBoxModel = new DefaultComboBoxModel<>();
        iconSetKeys.forEach((iconSetKey) -> {
            iconSetComboBoxModel.addElement(iconSetKey);
        });
        iconSetComboBox.setModel(iconSetComboBoxModel);
        iconSetComboBox.setRenderer(new DefaultListCellRenderer() {
            @Nonnull
            @Override
            public Component getListCellRendererComponent(JList<?> list, @Nullable Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (index >= 0) {
                    return super.getListCellRendererComponent(list, iconSetNames.get(index), index, isSelected, cellHasFocus);
                }
                int selectedIndex = iconSetComboBox.getSelectedIndex();
                return super.getListCellRendererComponent(list, selectedIndex >= 0 ? iconSetNames.get(selectedIndex) : value, index, isSelected, cellHasFocus);
            }
        });
    }

    public void setRenderingModes(List<String> renderingModeKeys, List<String> renderingModeNames) {
        DefaultComboBoxModel<String> renderingModeComboBoxModel = new DefaultComboBoxModel<>();
        renderingModeKeys.forEach((renderingModeKey) -> {
            renderingModeComboBoxModel.addElement(renderingModeKey);
        });
        renderingModeComboBox.setModel(renderingModeComboBoxModel);
        renderingModeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Nonnull
            @Override
            public Component getListCellRendererComponent(JList<?> list, @Nullable Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (index >= 0) {
                    return super.getListCellRendererComponent(list, renderingModeNames.get(index), index, isSelected, cellHasFocus);
                }
                int selectedIndex = renderingModeComboBox.getSelectedIndex();
                return super.getListCellRendererComponent(list, selectedIndex >= 0 ? renderingModeNames.get(selectedIndex) : value, index, isSelected, cellHasFocus);
            }
        });
    }

    public void setFontAntialiasings(List<String> fontAntialiasingKeys, List<String> fontAntialiasingNames) {
        DefaultComboBoxModel<String> fontAntialiasingComboBoxModel = new DefaultComboBoxModel<>();
        fontAntialiasingKeys.forEach((fontAntialiasingKey) -> {
            fontAntialiasingComboBoxModel.addElement(fontAntialiasingKey);
        });
        fontAntialiasingComboBox.setModel(fontAntialiasingComboBoxModel);
        fontAntialiasingComboBox.setRenderer(new DefaultListCellRenderer() {
            @Nonnull
            @Override
            public Component getListCellRendererComponent(JList<?> list, @Nullable Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (index >= 0) {
                    return super.getListCellRendererComponent(list, fontAntialiasingNames.get(index), index, isSelected, cellHasFocus);
                }
                int selectedIndex = fontAntialiasingComboBox.getSelectedIndex();
                return super.getListCellRendererComponent(list, selectedIndex >= 0 ? fontAntialiasingNames.get(selectedIndex) : value, index, isSelected, cellHasFocus);
            }
        });
    }

    public void setGuiScalings(List<String> guiScalingKeys, List<String> guiScalingNames) {
        DefaultComboBoxModel<String> guiScalingComboBoxModel = new DefaultComboBoxModel<>();
        guiScalingKeys.forEach((guiScalingKey) -> {
            guiScalingComboBoxModel.addElement(guiScalingKey);
        });
        guiScalingComboBox.setModel(guiScalingComboBoxModel);
        guiScalingComboBox.setRenderer(new DefaultListCellRenderer() {
            @Nonnull
            @Override
            public Component getListCellRendererComponent(JList<?> list, @Nullable Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (index >= 0) {
                    return super.getListCellRendererComponent(list, guiScalingNames.get(index), index, isSelected, cellHasFocus);
                }
                int selectedIndex = guiScalingComboBox.getSelectedIndex();
                return super.getListCellRendererComponent(list, selectedIndex >= 0 ? guiScalingNames.get(selectedIndex) : value, index, isSelected, cellHasFocus);
            }
        });
        guiScalingComboBox.addItemListener((ItemEvent e) -> {
            guiScalingSpinner.setEnabled(guiScalingComboBox.getSelectedIndex() == guiScalingKeys.size() - 1);
        });
    }

    public void setMacOsAppearances(List<String> macOsAppearancesKeys, List<String> macOsAppearanceNames) {
        DefaultComboBoxModel<String> macOsAppearanceComboBoxModel = new DefaultComboBoxModel<>();
        macOsAppearancesKeys.forEach((macOsAppearanceKey) -> {
            macOsAppearanceComboBoxModel.addElement(macOsAppearanceKey);
        });
        macOsAppearanceComboBox.setModel(macOsAppearanceComboBoxModel);
        macOsAppearanceComboBox.setRenderer(new DefaultListCellRenderer() {
            @Nonnull
            @Override
            public Component getListCellRendererComponent(JList<?> list, @Nullable Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (index >= 0) {
                    return super.getListCellRendererComponent(list, macOsAppearanceNames.get(index), index, isSelected, cellHasFocus);
                }
                int selectedIndex = macOsAppearanceComboBox.getSelectedIndex();
                return super.getListCellRendererComponent(list, selectedIndex >= 0 ? macOsAppearanceNames.get(selectedIndex) : value, index, isSelected, cellHasFocus);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        macOsOptionsPanel = new javax.swing.JPanel();
        macOsAppearanceLabel = new javax.swing.JLabel();
        macOsAppearanceComboBox = new javax.swing.JComboBox<>();
        useScreenMenuBarCheckBox = new javax.swing.JCheckBox();
        mainOptionsBasicPanel = new javax.swing.JPanel();
        generalPanel = new javax.swing.JPanel();
        visualThemeLabel = new javax.swing.JLabel();
        visualThemeComboBox = new javax.swing.JComboBox<>();
        visualThemeOptionsButton = new javax.swing.JButton();
        iconSetLabel = new javax.swing.JLabel();
        iconSetComboBox = new javax.swing.JComboBox<>();
        renderingModeLabel = new javax.swing.JLabel();
        renderingModeComboBox = new javax.swing.JComboBox<>();
        fontAntialiasingLabel = new javax.swing.JLabel();
        fontAntialiasingComboBox = new javax.swing.JComboBox<>();
        guiScalingLabel = new javax.swing.JLabel();
        guiScalingComboBox = new javax.swing.JComboBox<>();
        guiScalingSpinner = new javax.swing.JSpinner();
        mainOptionsNotePanel = new javax.swing.JPanel();
        requireRestartLabel = new javax.swing.JLabel();

        macOsAppearanceLabel.setText(resourceBundle.getString("macOsAppearanceLabel.text") + " *"); // NOI18N

        macOsAppearanceComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                macOsAppearanceComboBoxjComboBoxItemStateChanged(evt);
            }
        });

        useScreenMenuBarCheckBox.setSelected(true);
        useScreenMenuBarCheckBox.setText(resourceBundle.getString("useScreenMenuBarCheckBox.text") + " *"); // NOI18N

        javax.swing.GroupLayout macOsOptionsPanelLayout = new javax.swing.GroupLayout(macOsOptionsPanel);
        macOsOptionsPanel.setLayout(macOsOptionsPanelLayout);
        macOsOptionsPanelLayout.setHorizontalGroup(
            macOsOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(macOsOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(macOsOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(macOsAppearanceComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 645, Short.MAX_VALUE)
                    .addGroup(macOsOptionsPanelLayout.createSequentialGroup()
                        .addComponent(macOsAppearanceLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(useScreenMenuBarCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        macOsOptionsPanelLayout.setVerticalGroup(
            macOsOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(macOsOptionsPanelLayout.createSequentialGroup()
                .addComponent(macOsAppearanceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(macOsAppearanceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useScreenMenuBarCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setLayout(new java.awt.BorderLayout());

        mainOptionsBasicPanel.setLayout(new java.awt.BorderLayout());

        visualThemeLabel.setText(resourceBundle.getString("visualThemeLabel.text")); // NOI18N

        visualThemeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                visualThemeComboBoxjComboBoxItemStateChanged(evt);
            }
        });

        visualThemeOptionsButton.setText(resourceBundle.getString("visualThemeOptionsButton.text")); // NOI18N
        visualThemeOptionsButton.setEnabled(false);
        visualThemeOptionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visualThemeOptionsButtonActionPerformed(evt);
            }
        });

        iconSetLabel.setText(resourceBundle.getString("iconSetLabel.text")); // NOI18N

        iconSetComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                iconSetComboBoxItemStateChanged(evt);
            }
        });

        renderingModeLabel.setText(resourceBundle.getString("renderingModeLabel.text") + " *"); // NOI18N

        renderingModeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                renderingModeComboBoxjComboBoxItemStateChanged(evt);
            }
        });

        fontAntialiasingLabel.setText(resourceBundle.getString("fontAntialiasingLabel.text") + " *"); // NOI18N

        fontAntialiasingComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fontAntialiasingComboBoxjComboBoxItemStateChanged(evt);
            }
        });

        guiScalingLabel.setText(resourceBundle.getString("guiScalingLabel.text") + " *"); // NOI18N

        guiScalingComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                guiScalingComboBoxjComboBoxItemStateChanged(evt);
            }
        });
        guiScalingComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiScalingComboBoxActionPerformed(evt);
            }
        });

        guiScalingSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0f, null, null, 1.0f));
        guiScalingSpinner.setEnabled(false);

        javax.swing.GroupLayout generalPanelLayout = new javax.swing.GroupLayout(generalPanel);
        generalPanel.setLayout(generalPanelLayout);
        generalPanelLayout.setHorizontalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(generalPanelLayout.createSequentialGroup()
                        .addComponent(visualThemeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(visualThemeOptionsButton))
                    .addComponent(iconSetComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(renderingModeComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fontAntialiasingComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(generalPanelLayout.createSequentialGroup()
                        .addComponent(guiScalingComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiScalingSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(generalPanelLayout.createSequentialGroup()
                        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(visualThemeLabel)
                            .addComponent(iconSetLabel)
                            .addComponent(renderingModeLabel)
                            .addComponent(fontAntialiasingLabel)
                            .addComponent(guiScalingLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        generalPanelLayout.setVerticalGroup(
            generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(visualThemeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(visualThemeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(visualThemeOptionsButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iconSetLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iconSetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(renderingModeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(renderingModeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fontAntialiasingLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fontAntialiasingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guiScalingLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(guiScalingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiScalingSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainOptionsBasicPanel.add(generalPanel, java.awt.BorderLayout.CENTER);

        add(mainOptionsBasicPanel, java.awt.BorderLayout.NORTH);

        requireRestartLabel.setText(resourceBundle.getString("requireRestartLabel.text")); // NOI18N

        javax.swing.GroupLayout mainOptionsNotePanelLayout = new javax.swing.GroupLayout(mainOptionsNotePanel);
        mainOptionsNotePanel.setLayout(mainOptionsNotePanelLayout);
        mainOptionsNotePanelLayout.setHorizontalGroup(
            mainOptionsNotePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainOptionsNotePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(requireRestartLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainOptionsNotePanelLayout.setVerticalGroup(
            mainOptionsNotePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainOptionsNotePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(requireRestartLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(mainOptionsNotePanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void visualThemeComboBoxjComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_visualThemeComboBoxjComboBoxItemStateChanged
        String selectedTheme = (String) visualThemeComboBox.getSelectedItem();
        ConfigurableLafProvider lafProvider = themeOptions.get(selectedTheme);
        visualThemeOptionsButton.setEnabled(lafProvider != null);
        notifyModified();
    }//GEN-LAST:event_visualThemeComboBoxjComboBoxItemStateChanged

    private void renderingModeComboBoxjComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_renderingModeComboBoxjComboBoxItemStateChanged
        notifyModified();
    }//GEN-LAST:event_renderingModeComboBoxjComboBoxItemStateChanged

    private void fontAntialiasingComboBoxjComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fontAntialiasingComboBoxjComboBoxItemStateChanged
        notifyModified();
    }//GEN-LAST:event_fontAntialiasingComboBoxjComboBoxItemStateChanged

    private void guiScalingComboBoxjComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_guiScalingComboBoxjComboBoxItemStateChanged
        notifyModified();
    }//GEN-LAST:event_guiScalingComboBoxjComboBoxItemStateChanged

    private void guiScalingComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiScalingComboBoxActionPerformed
        notifyModified();
    }//GEN-LAST:event_guiScalingComboBoxActionPerformed

    private void macOsAppearanceComboBoxjComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_macOsAppearanceComboBoxjComboBoxItemStateChanged
        notifyModified();
    }//GEN-LAST:event_macOsAppearanceComboBoxjComboBoxItemStateChanged

    private void iconSetComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_iconSetComboBoxItemStateChanged
        notifyModified();
    }//GEN-LAST:event_iconSetComboBoxItemStateChanged

    private void visualThemeOptionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visualThemeOptionsButtonActionPerformed
        if (controller != null) {
            String selectedTheme = (String) visualThemeComboBox.getSelectedItem();
            ConfigurableLafProvider lafProvider = themeOptions.get(selectedTheme);
            controller.configureTheme(lafProvider, settingsOptionsProvider);
        }
    }//GEN-LAST:event_visualThemeOptionsButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> fontAntialiasingComboBox;
    private javax.swing.JLabel fontAntialiasingLabel;
    private javax.swing.JPanel generalPanel;
    private javax.swing.JComboBox<String> guiScalingComboBox;
    private javax.swing.JLabel guiScalingLabel;
    private javax.swing.JSpinner guiScalingSpinner;
    private javax.swing.JComboBox<String> iconSetComboBox;
    private javax.swing.JLabel iconSetLabel;
    private javax.swing.JComboBox<String> macOsAppearanceComboBox;
    private javax.swing.JLabel macOsAppearanceLabel;
    private javax.swing.JPanel macOsOptionsPanel;
    private javax.swing.JPanel mainOptionsBasicPanel;
    private javax.swing.JPanel mainOptionsNotePanel;
    private javax.swing.JComboBox<String> renderingModeComboBox;
    private javax.swing.JLabel renderingModeLabel;
    private javax.swing.JLabel requireRestartLabel;
    private javax.swing.JCheckBox useScreenMenuBarCheckBox;
    private javax.swing.JComboBox<String> visualThemeComboBox;
    private javax.swing.JLabel visualThemeLabel;
    private javax.swing.JButton visualThemeOptionsButton;
    // End of variables declaration//GEN-END:variables

    private void notifyModified() {
        if (settingsModifiedListener != null) {
            settingsModifiedListener.notifyModified();
        }
    }

    @Override
    public void setSettingsModifiedListener(SettingsModifiedListener listener) {
        settingsModifiedListener = listener;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @ParametersAreNonnullByDefault
    public interface Controller {

        /**
         * Invokes theme configuration action.
         *
         * @param lafProvider theme provider
         * @param settingsOptionsProvider settings options provider
         */
        void configureTheme(ConfigurableLafProvider lafProvider, SettingsOptionsProvider settingsOptionsProvider);
    }
}
