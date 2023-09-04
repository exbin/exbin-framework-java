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
package org.exbin.framework.options.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.options.api.OptionsModifiedListener;
import org.exbin.framework.options.options.impl.FrameworkOptionsImpl;
import org.exbin.framework.options.api.OptionsComponent;

/**
 * Main options panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MainOptionsPanel extends javax.swing.JPanel implements OptionsComponent<FrameworkOptionsImpl> {

    private java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(MainOptionsPanel.class);
    private OptionsModifiedListener optionsModifiedListener;
    private OptionsComponent extendedOptionsPanel = null;
    private String defaultLocaleName = "";

    private final DefaultListCellRenderer languageComboBoxCellRenderer;

    public MainOptionsPanel() {
        languageComboBoxCellRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                DefaultListCellRenderer retValue = (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Locale) {
                    if (value.equals(Locale.ROOT)) {
                        retValue.setText(defaultLocaleName);
                    } else {
                        retValue.setText(((Locale) value).getDisplayName((Locale) value));
                    }
                }
                return retValue;
            }
        };

        initComponents();
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Override
    public void loadFromOptions(FrameworkOptionsImpl options) {
        Locale languageLocale = options.getLanguageLocale();
        languageComboBox.setSelectedItem(languageLocale);

        String laf = options.getLookAndFeel();
        ComboBoxModel<String> model = visualThemeComboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (laf.equals(model.getElementAt(i))) {
                visualThemeComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    @Override
    public void saveToOptions(FrameworkOptionsImpl options) {
        options.setLookAndFeel((String) visualThemeComboBox.getSelectedItem());
        Locale languageLocale = (Locale) languageComboBox.getSelectedItem();
        options.setLanguageLocale(languageLocale);
    }

    public void setLanguageLocales(List<Locale> languageLocales) {
        DefaultComboBoxModel<Locale> languageComboBoxModel = new DefaultComboBoxModel<>();
        languageLocales.forEach((language) -> {
            languageComboBoxModel.addElement(language);
        });
        languageComboBox.setModel(languageComboBoxModel);
    }

    public void setDefaultLocaleName(String defaultLocaleName) {
        this.defaultLocaleName = defaultLocaleName;
    }

    public void setThemes(List<String> themeKeys, List<String> themeNames) {
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
        visualThemeLabel = new javax.swing.JLabel();
        visualThemeComboBox = new javax.swing.JComboBox<>();
        renderingModeLabel = new javax.swing.JLabel();
        renderingModeComboBox = new javax.swing.JComboBox<>();
        fontAntialiasingLabel = new javax.swing.JLabel();
        fontAntialiasingComboBox = new javax.swing.JComboBox<>();
        guiScalingLabel = new javax.swing.JLabel();
        guiScalingComboBox = new javax.swing.JComboBox<>();
        guiScalingSpinner = new javax.swing.JSpinner();
        mainOptionsNotePanel = new javax.swing.JPanel();
        requireRestartLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        languageLabel.setText(resourceBundle.getString("languageLabel.text") + " *"); // NOI18N

        languageComboBox.setRenderer(languageComboBoxCellRenderer);
        languageComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                languageComboBoxItemStateChanged(evt);
            }
        });

        visualThemeLabel.setText(resourceBundle.getString("visualThemeLabel.text")); // NOI18N

        visualThemeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                visualThemeComboBoxjComboBoxItemStateChanged(evt);
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

        javax.swing.GroupLayout mainOptionsBasicPanelLayout = new javax.swing.GroupLayout(mainOptionsBasicPanel);
        mainOptionsBasicPanel.setLayout(mainOptionsBasicPanelLayout);
        mainOptionsBasicPanelLayout.setHorizontalGroup(
            mainOptionsBasicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainOptionsBasicPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainOptionsBasicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(visualThemeComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 388, Short.MAX_VALUE)
                    .addComponent(languageComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 388, Short.MAX_VALUE)
                    .addComponent(renderingModeComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 388, Short.MAX_VALUE)
                    .addComponent(fontAntialiasingComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 388, Short.MAX_VALUE)
                    .addGroup(mainOptionsBasicPanelLayout.createSequentialGroup()
                        .addGroup(mainOptionsBasicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(visualThemeLabel)
                            .addComponent(languageLabel)
                            .addComponent(renderingModeLabel)
                            .addComponent(fontAntialiasingLabel)
                            .addComponent(guiScalingLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(mainOptionsBasicPanelLayout.createSequentialGroup()
                        .addComponent(guiScalingComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiScalingSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        mainOptionsBasicPanelLayout.setVerticalGroup(
            mainOptionsBasicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainOptionsBasicPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(languageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(languageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(visualThemeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(visualThemeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addGroup(mainOptionsBasicPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(guiScalingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiScalingSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(mainOptionsBasicPanel, java.awt.BorderLayout.NORTH);

        requireRestartLabel.setText(resourceBundle.getString("requireRestartLabel.text")); // NOI18N

        javax.swing.GroupLayout mainOptionsNotePanelLayout = new javax.swing.GroupLayout(mainOptionsNotePanel);
        mainOptionsNotePanel.setLayout(mainOptionsNotePanelLayout);
        mainOptionsNotePanelLayout.setHorizontalGroup(
            mainOptionsNotePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainOptionsNotePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(requireRestartLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
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

    private void languageComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_languageComboBoxItemStateChanged
        notifyModified();
    }//GEN-LAST:event_languageComboBoxItemStateChanged

    /**
     * Test method for this panel.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WindowUtils.invokeDialog(new MainOptionsPanel());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> fontAntialiasingComboBox;
    private javax.swing.JLabel fontAntialiasingLabel;
    private javax.swing.JComboBox<String> guiScalingComboBox;
    private javax.swing.JLabel guiScalingLabel;
    private javax.swing.JSpinner guiScalingSpinner;
    private javax.swing.JComboBox<Locale> languageComboBox;
    private javax.swing.JLabel languageLabel;
    private javax.swing.JPanel mainOptionsBasicPanel;
    private javax.swing.JPanel mainOptionsNotePanel;
    private javax.swing.JComboBox<String> renderingModeComboBox;
    private javax.swing.JLabel renderingModeLabel;
    private javax.swing.JLabel requireRestartLabel;
    private javax.swing.JComboBox<String> visualThemeComboBox;
    private javax.swing.JLabel visualThemeLabel;
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

    public void addExtendedPanel(OptionsComponent<?> optionsPanel) {
        if (extendedOptionsPanel != null) {
            remove((Component) extendedOptionsPanel);
        }
        extendedOptionsPanel = optionsPanel;
        add((Component) extendedOptionsPanel, BorderLayout.CENTER);
        extendedOptionsPanel.setOptionsModifiedListener(optionsModifiedListener);
    }

    @Immutable
    public static class KeyText {

        private final String key;
        private final String text;

        public KeyText(String key, String text) {
            this.key = key;
            this.text = text;
        }
    }
}
