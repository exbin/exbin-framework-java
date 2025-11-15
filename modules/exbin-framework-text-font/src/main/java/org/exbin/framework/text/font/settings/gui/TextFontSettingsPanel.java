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
package org.exbin.framework.text.font.settings.gui;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.text.font.settings.TextFontOptions;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsModifiedListener;
import org.exbin.framework.options.settings.api.SettingsOptionsProvider;
import org.exbin.framework.context.api.ActiveContextProvider;
import org.exbin.framework.text.font.ContextFont;
import org.exbin.framework.text.font.TextFontState;
import org.exbin.framework.text.font.settings.TextFontSettingsApplier;

/**
 * Text font settings panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextFontSettingsPanel extends javax.swing.JPanel implements SettingsComponent {

    private SettingsModifiedListener settingsModifiedListener;
    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(TextFontSettingsPanel.class);
    private TextFontState textFontState = null;
    private Font codeFont;
    protected Controller controller;

    public TextFontSettingsPanel() {
        initComponents();
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setTextFontState(TextFontState textFontState) {
        this.textFontState = textFontState;
        updateForState();
    }

    @Override
    public void loadFromOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ActiveContextProvider contextProvider) {
        TextFontOptions options = settingsOptionsProvider.getSettingsOptions(TextFontOptions.class);
        boolean useDefaultFont = options.isUseDefaultFont();
        defaultFontCheckBox.setSelected(useDefaultFont);

        textFontState = null;
        if (contextProvider != null) {
            ContextFont contextFont = contextProvider.getActiveState(ContextFont.class);
            textFontState = contextFont instanceof TextFontState ? (TextFontState) contextFont : null;
        }

        codeFont = textFontState == null ? options.getFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)) : textFontState.getDefaultFont().deriveFont(options.getFontAttributes());

        if (contextProvider != null) {
            ContextFont contextFont = contextProvider.getActiveState(ContextFont.class);
            if (contextFont instanceof TextFontState) {
                TextFontState state = (TextFontState) contextFont;
                if (!codeFont.equals(state.getCurrentFont())) {
                    codeFont = state.getCurrentFont();
                    notifyModified();
                }
            }
        }

        setEnabled(!useDefaultFont);
        updateFontFields();
    }

    @Override
    public void saveToOptions(SettingsOptionsProvider settingsOptionsProvider, @Nullable ActiveContextProvider contextProvider) {
        TextFontOptions options = settingsOptionsProvider.getSettingsOptions(TextFontOptions.class);
        options.setUseDefaultFont(defaultFontCheckBox.isSelected());
        options.setFontAttributes(codeFont != null ? codeFont.getAttributes() : null);

        if (contextProvider != null) {
            ContextFont contextFont = contextProvider.getActiveState(ContextFont.class);
            if (contextFont instanceof TextFontState) {
                TextFontSettingsApplier applier = new TextFontSettingsApplier();
                applier.applySettings(contextFont, settingsOptionsProvider);
                contextProvider.notifyActiveStateChange(ContextFont.class, ContextFont.ChangeType.FONT_CHANGE);
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        fontPreviewLabel.setEnabled(enabled);
        updateForState();
        changeFontButton.setEnabled(enabled);
    }

    private void updateForState() {
        boolean enabled = isEnabled();
        fillDefaultFontButton.setEnabled(enabled && textFontState != null);
        fillCurrentFontButton.setEnabled(enabled && textFontState != null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        colorChooser = new javax.swing.JColorChooser();
        defaultFontCheckBox = new javax.swing.JCheckBox();
        fillDefaultFontButton = new javax.swing.JButton();
        changeFontButton = new javax.swing.JButton();
        fontPreviewLabel = new javax.swing.JLabel();
        fillCurrentFontButton = new javax.swing.JButton();
        fontTextField = new javax.swing.JTextField();

        colorChooser.setName("colorChooser"); // NOI18N

        setName("Form"); // NOI18N

        defaultFontCheckBox.setSelected(true);
        defaultFontCheckBox.setText(resourceBundle.getString("defaultFontCheckBox.text")); // NOI18N
        defaultFontCheckBox.setName("defaultFontCheckBox"); // NOI18N
        defaultFontCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                defaultFontCheckBoxItemStateChanged(evt);
            }
        });

        fillDefaultFontButton.setText(resourceBundle.getString("fillDefaultFontButton.text")); // NOI18N
        fillDefaultFontButton.setEnabled(false);
        fillDefaultFontButton.setName("fillDefaultFontButton"); // NOI18N
        fillDefaultFontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fillDefaultFontButtonActionPerformed(evt);
            }
        });

        changeFontButton.setText(resourceBundle.getString("changeFontButton.text")); // NOI18N
        changeFontButton.setEnabled(false);
        changeFontButton.setName("changeFontButton"); // NOI18N
        changeFontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeFontButtonActionPerformed(evt);
            }
        });

        fontPreviewLabel.setBackground(java.awt.Color.white);
        fontPreviewLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        fontPreviewLabel.setText(resourceBundle.getString("fontPreviewLabel.text")); // NOI18N
        fontPreviewLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        fontPreviewLabel.setEnabled(false);
        fontPreviewLabel.setName("fontPreviewLabel"); // NOI18N
        fontPreviewLabel.setOpaque(true);

        fillCurrentFontButton.setText(resourceBundle.getString("fillCurrentFontButton.text")); // NOI18N
        fillCurrentFontButton.setEnabled(false);
        fillCurrentFontButton.setName("fillCurrentFontButton"); // NOI18N
        fillCurrentFontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fillCurrentFontButtonActionPerformed(evt);
            }
        });

        fontTextField.setEditable(false);
        fontTextField.setName("fontTextField"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(fontPreviewLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fontTextField, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(defaultFontCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(changeFontButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fillDefaultFontButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fillCurrentFontButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(defaultFontCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fontTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fontPreviewLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(changeFontButton)
                    .addComponent(fillDefaultFontButton)
                    .addComponent(fillCurrentFontButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void defaultFontCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_defaultFontCheckBoxItemStateChanged
        boolean selected = evt.getStateChange() != ItemEvent.SELECTED;
        fontPreviewLabel.setEnabled(selected);
        fillDefaultFontButton.setEnabled(selected);
        fillCurrentFontButton.setEnabled(selected);
        changeFontButton.setEnabled(selected);
        notifyModified();
    }//GEN-LAST:event_defaultFontCheckBoxItemStateChanged

    private void fillDefaultFontButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fillDefaultFontButtonActionPerformed
        codeFont = textFontState.getDefaultFont();
        updateFontFields();
        notifyModified();
    }//GEN-LAST:event_fillDefaultFontButtonActionPerformed

    private void changeFontButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeFontButtonActionPerformed
        if (controller != null) {
            Font resultFont = controller.changeFont(fontPreviewLabel.getFont());
            if (resultFont != null) {
                codeFont = resultFont;
                updateFontFields();
                notifyModified();
            }
        }
    }//GEN-LAST:event_changeFontButtonActionPerformed

    private void fillCurrentFontButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fillCurrentFontButtonActionPerformed
        codeFont = textFontState.getCurrentFont();
        updateFontFields();
        notifyModified();
    }//GEN-LAST:event_fillCurrentFontButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton changeFontButton;
    private javax.swing.JColorChooser colorChooser;
    private javax.swing.JCheckBox defaultFontCheckBox;
    private javax.swing.JButton fillCurrentFontButton;
    private javax.swing.JButton fillDefaultFontButton;
    private javax.swing.JLabel fontPreviewLabel;
    private javax.swing.JTextField fontTextField;
    // End of variables declaration//GEN-END:variables

    private void updateFontFields() {
        int fontStyle = codeFont.getStyle();
        String fontStyleName;
        if ((fontStyle & (Font.BOLD + Font.ITALIC)) == Font.BOLD + Font.ITALIC) {
            fontStyleName = "Bold Italic";
        } else if ((fontStyle & Font.BOLD) > 0) {
            fontStyleName = "Bold";
        } else if ((fontStyle & Font.ITALIC) > 0) {
            fontStyleName = "Italic";
        } else {
            fontStyleName = "Plain";
        }
        fontTextField.setText(codeFont.getFamily() + " " + String.valueOf(codeFont.getSize()) + " " + fontStyleName);
        fontPreviewLabel.setFont(codeFont);
    }

    private void notifyModified() {
        if (settingsModifiedListener != null) {
            settingsModifiedListener.notifyModified();
        }
    }

    @Override
    public void setSettingsModifiedListener(SettingsModifiedListener listener) {
        settingsModifiedListener = listener;
    }

    @ParametersAreNonnullByDefault
    public interface Controller {

        @Nullable
        Font changeFont(Font currentFont);
    }
}
