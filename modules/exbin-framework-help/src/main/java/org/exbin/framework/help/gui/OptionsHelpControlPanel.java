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
package org.exbin.framework.help.gui;

import java.awt.event.ActionEvent;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JButton;
import org.exbin.framework.App;
import org.exbin.framework.help.HelpModule;
import org.exbin.framework.help.api.HelpLink;
import org.exbin.framework.help.api.HelpLinkable;
import org.exbin.framework.help.api.HelpModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.OkCancelListener;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.window.api.handler.OptionsControlHandler;

/**
 * Default control panel for options dialogs.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsHelpControlPanel extends javax.swing.JPanel implements OptionsControlHandler.OptionsControlService, HelpLinkable {

    private final java.util.ResourceBundle resourceBundle;
    private OptionsControlHandler handler;
    private OkCancelListener okCancelListener;
    private HelpLink helpLink;

    public OptionsHelpControlPanel() {
        this(App.getModule(LanguageModuleApi.class).getBundle(OptionsHelpControlPanel.class));
    }

    public OptionsHelpControlPanel(java.util.ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        initComponents();

        okCancelListener = new OkCancelListener() {
            @Override
            public void okEvent() {
                performClick(OptionsControlHandler.ControlActionType.SAVE);
            }

            @Override
            public void cancelEvent() {
                performClick(OptionsControlHandler.ControlActionType.CANCEL);
            }
        };
        helpButton.addActionListener((ActionEvent e) -> {
            if (helpLink != null) {
                HelpModuleApi helpModule = App.getModule(HelpModuleApi.class);
                helpModule.openHelp(helpLink);
            }
        });
    }

    public void setHandler(OptionsControlHandler handler) {
        this.handler = handler;
    }

    @Override
    public void setHelpLink(HelpLink helpLink) {
        this.helpLink = helpLink;
        helpButton.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        helpButton = App.getModule(HelpModule.class).createHelpButton();
        cancelButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        applyOnceButton = new javax.swing.JButton();

        cancelButton.setText(resourceBundle.getString("cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        saveButton.setText(resourceBundle.getString("saveButton.text")); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        applyOnceButton.setText(resourceBundle.getString("applyOnceButton.text")); // NOI18N
        applyOnceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyOnceButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(helpButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(applyOnceButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(saveButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(applyOnceButton)
                    .addComponent(saveButton)
                    .addComponent(helpButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        if (handler != null) {
            handler.controlActionPerformed(OptionsControlHandler.ControlActionType.CANCEL);
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void applyOnceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyOnceButtonActionPerformed
        if (handler != null) {
            handler.controlActionPerformed(OptionsControlHandler.ControlActionType.APPLY_ONCE);
        }
    }//GEN-LAST:event_applyOnceButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        if (handler != null) {
            handler.controlActionPerformed(OptionsControlHandler.ControlActionType.SAVE);
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyOnceButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton helpButton;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void performClick(OptionsControlHandler.ControlActionType actionType) {
        switch (actionType) {
            case SAVE: {
                UiUtils.doButtonClick(saveButton);
                break;
            }
            case APPLY_ONCE: {
                UiUtils.doButtonClick(applyOnceButton);
                break;
            }
            case CANCEL: {
                UiUtils.doButtonClick(cancelButton);
                break;
            }
            default:
                throw new IllegalStateException("Illegal action type " + actionType.name());
        }
    }

    @Nonnull
    @Override
    public Optional<JButton> getDefaultButton() {
        return Optional.of(saveButton);
    }

    @Nonnull
    @Override
    public OkCancelListener getOkCancelListener() {
        return okCancelListener;
    }

    @Nonnull
    @Override
    public OptionsControlHandler.OptionsControlEnablementListener createEnablementListener() {
        return (OptionsControlHandler.ControlActionType actionType, boolean enablement) -> {
            switch (actionType) {
                case APPLY_ONCE: {
                    applyOnceButton.setEnabled(enablement);
                    break;
                }
                case CANCEL: {
                    cancelButton.setEnabled(enablement);
                    break;
                }
                case SAVE: {
                    saveButton.setEnabled(enablement);
                    break;
                }
                default:
                    throw new IllegalStateException("Illegal action type " + actionType.name());
            }
        };
    }
}
