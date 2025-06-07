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
package org.exbin.framework.addon.manager.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.exbin.framework.App;
import org.exbin.framework.menu.popup.api.MenuPopupModuleApi;
import org.exbin.framework.addon.manager.api.AddonManagerModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.DesktopUtils;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.window.api.handler.CloseControlHandler;
import org.exbin.framework.window.api.handler.CloseControlHandler.CloseControlEnablementListener;

/**
 * Control panel for addons manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonsControlPanel extends javax.swing.JPanel implements CloseControlHandler.CloseControlService {

    private final java.util.ResourceBundle resourceBundle;
    private CloseControlHandler handler;
    private Controller controller;
    private int availableUpdates = 0;
    private int selectedForOperation = 0;

    public AddonsControlPanel() {
        this(App.getModule(LanguageModuleApi.class).getBundle(AddonsControlPanel.class));
    }

    public AddonsControlPanel(java.util.ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        initComponents();
    }

    public void showLegacyWarning() {
        add(legacyModePanel, BorderLayout.CENTER);
    }

    public void showManualOnlyWarning() {
        AddonManagerModuleApi addonManagerModule = App.getModule(AddonManagerModuleApi.class);
        String link = addonManagerModule.getManualLegacyUrl();
        manualOnlyModeLabel.setText(String.format(resourceBundle.getString("manualOnlyModeLabel.text"), link));
        manualOnlyModeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1 && !evt.isPopupTrigger()) {
                    DesktopUtils.openDesktopURL(link);
                }
            }
        });
        manualOnlyModeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        manualOnlyModeLabel.setComponentPopupMenu(new JPopupMenu() {

            @Override
            public void show(Component invoker, int x, int y) {
                MenuPopupModuleApi actionPopupModule = App.getModule(MenuPopupModuleApi.class);
                actionPopupModule.createLinkPopupMenu(link).show(invoker, x, y);
            }
        });
        add(manualOnlyModePanel, BorderLayout.CENTER);
    }

    public void setOperationState(OperationVariant variant, int selected) {
        this.selectedForOperation = selected;
        switch (variant) {
            case INSTALL:
                operationButton.setText(resourceBundle.getString("installButton.text"));
                if (selected > 0) {
                    operationLabel.setText(String.format(resourceBundle.getString("installLabel.text"), selected));
                    operationButton.setEnabled(true);
                } else {
                    updateAllState();
                }
                break;
            case UPDATE:
                operationButton.setText(resourceBundle.getString("updateButton.text"));
                if (selected > 0) {
                    operationLabel.setText(String.format(resourceBundle.getString("updateLabel.text"), selected));
                    operationButton.setEnabled(true);
                } else {
                    updateAllState();
                }
                break;
            case REMOVE:
                throw new UnsupportedOperationException("Not supported yet.");
            default:
                throw new AssertionError();
        }
    }

    public void setAvailableUpdates(int availableUpdates) {
        this.availableUpdates = availableUpdates;
        updateAllState();
    }

    public void updateAllState() {
        if (selectedForOperation == 0) {
            operationButton.setText(resourceBundle.getString("updateAllButton.text"));
            if (availableUpdates > 0) {
                operationButton.setEnabled(availableUpdates > 0);
                operationLabel.setText(String.format(resourceBundle.getString("updateAllLabel.text"), availableUpdates));
            } else {
                operationButton.setEnabled(false);
                operationLabel.setText(null);
            }
        }
    }

    public void setHandler(CloseControlHandler handler) {
        this.handler = handler;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        legacyModePanel = new javax.swing.JPanel();
        legacyModeLabel = new javax.swing.JLabel();
        manualOnlyModePanel = new javax.swing.JPanel();
        manualOnlyModeLabel = new javax.swing.JLabel();
        buttonsPanel = new javax.swing.JPanel();
        operationButton = new javax.swing.JButton();
        operationLabel = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();

        legacyModePanel.setBackground(new java.awt.Color(255, 153, 0));

        legacyModeLabel.setFont(new java.awt.Font("sansserif", 1, 13)); // NOI18N
        legacyModeLabel.setForeground(new java.awt.Color(0, 0, 0));
        legacyModeLabel.setText(resourceBundle.getString("legacyModeLabel.text")); // NOI18N

        javax.swing.GroupLayout legacyModePanelLayout = new javax.swing.GroupLayout(legacyModePanel);
        legacyModePanel.setLayout(legacyModePanelLayout);
        legacyModePanelLayout.setHorizontalGroup(
            legacyModePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(legacyModePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(legacyModeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 814, Short.MAX_VALUE)
                .addContainerGap())
        );
        legacyModePanelLayout.setVerticalGroup(
            legacyModePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(legacyModePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(legacyModeLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        manualOnlyModePanel.setBackground(new java.awt.Color(255, 102, 102));

        manualOnlyModeLabel.setFont(new java.awt.Font("sansserif", 1, 13)); // NOI18N
        manualOnlyModeLabel.setForeground(new java.awt.Color(255, 255, 255));
        manualOnlyModeLabel.setText(resourceBundle.getString("manualOnlyModeLabel.text")); // NOI18N

        javax.swing.GroupLayout manualOnlyModePanelLayout = new javax.swing.GroupLayout(manualOnlyModePanel);
        manualOnlyModePanel.setLayout(manualOnlyModePanelLayout);
        manualOnlyModePanelLayout.setHorizontalGroup(
            manualOnlyModePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(manualOnlyModePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(manualOnlyModeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 814, Short.MAX_VALUE)
                .addContainerGap())
        );
        manualOnlyModePanelLayout.setVerticalGroup(
            manualOnlyModePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(manualOnlyModePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(manualOnlyModeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setLayout(new java.awt.BorderLayout());

        operationButton.setText(resourceBundle.getString("operationButton.text")); // NOI18N
        operationButton.setEnabled(false);
        operationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operationButtonActionPerformed(evt);
            }
        });

        closeButton.setText(resourceBundle.getString("closeButton.text")); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonsPanelLayout = new javax.swing.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(operationButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(operationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(closeButton)
                .addContainerGap())
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(closeButton)
                    .addComponent(operationButton)
                    .addComponent(operationLabel))
                .addContainerGap())
        );

        add(buttonsPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        if (handler != null) {
            handler.controlActionPerformed();
        }
    }//GEN-LAST:event_closeButtonActionPerformed

    private void operationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_operationButtonActionPerformed
        if (controller != null) {
            controller.performOperation();
        }
    }//GEN-LAST:event_operationButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel legacyModeLabel;
    private javax.swing.JPanel legacyModePanel;
    private javax.swing.JLabel manualOnlyModeLabel;
    private javax.swing.JPanel manualOnlyModePanel;
    private javax.swing.JButton operationButton;
    private javax.swing.JLabel operationLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void performCloseClick() {
        UiUtils.doButtonClick(closeButton);
    }

    @Nonnull
    @Override
    public Optional<JButton> getDefaultButton() {
        return Optional.of(closeButton);
    }

    @Override
    public void invokeOkEvent() {
        performCloseClick();
    }

    @Override
    public void invokeCancelEvent() {
        performCloseClick();
    }

    @Nonnull
    @Override
    public CloseControlEnablementListener createEnablementListener() {
        return (boolean enablement) -> {
            closeButton.setEnabled(enablement);
        };
    }

    public interface Controller {

        void performOperation();
    }

    public enum OperationVariant {
        INSTALL,
        UPDATE,
        REMOVE
    }
}
