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
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.exbin.framework.App;
import org.exbin.framework.menu.popup.api.MenuPopupModuleApi;
import org.exbin.framework.addon.manager.api.AddonManagerModuleApi;
import org.exbin.framework.help.api.HelpLink;
import org.exbin.framework.help.api.HelpModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.DesktopUtils;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.window.api.controller.CloseControlController;

/**
 * Control panel for addons manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonsControlPanel extends javax.swing.JPanel implements CloseControlController.CloseControlComponent {

    protected final java.util.ResourceBundle resourceBundle;
    protected Controller controller;
    protected Component activeStatusComponent = null;

    public AddonsControlPanel() {
        this(App.getModule(LanguageModuleApi.class).getBundle(AddonsControlPanel.class));
    }

    public AddonsControlPanel(java.util.ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        initComponents();
    }

    public void addHelpButton(HelpLink helpLink) {
        HelpModuleApi helpModule = App.getModule(HelpModuleApi.class);
        JButton helpButton = helpModule.createHelpButton();
        helpButton.addActionListener((ActionEvent e) -> {
            if (helpLink != null) {
                helpModule.openHelp(helpLink);
            }
        });
        buttonsPanel.add(helpButton);
        javax.swing.GroupLayout buttonsPanelLayout = new javax.swing.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
                buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(buttonsPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(helpButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(refreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(closeButton)
                                .addContainerGap())
        );
        buttonsPanelLayout.setVerticalGroup(
                buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonsPanelLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(helpButton)
                                        .addComponent(closeButton)
                                        .addComponent(refreshButton)
                                        .addComponent(statusPanel))
                                .addContainerGap())
        );
    }

    public void showLegacyWarning() {
        add(legacyModePanel, BorderLayout.CENTER);
    }

    public void showManualOnlyWarning() {
        AddonManagerModuleApi addonManagerModule = App.getModule(AddonManagerModuleApi.class);
        String link = ""; // TODO addonManagerModule.getManualLegacyUrl();
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
        operationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        buttonsPanel = new javax.swing.JPanel();
        statusPanel = new javax.swing.JPanel();
        refreshButton = new javax.swing.JButton();
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

        progressBar.setIndeterminate(true);

        setLayout(new java.awt.BorderLayout());

        statusPanel.setLayout(new java.awt.BorderLayout());

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exbin/framework/addon/manager/resources/icons/open_icon_library/icons/png/16x16/view-refresh-4.png")));
        refreshButton.setText(resourceBundle.getString("refreshButton.text")); // NOI18N
        refreshButton.setEnabled(false);
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
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
                .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refreshButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(closeButton)
                .addContainerGap())
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(statusPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(closeButton)
                        .addComponent(refreshButton)))
                .addContainerGap())
        );

        add(buttonsPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        controller.controlActionPerformed();
    }//GEN-LAST:event_closeButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        controller.performRefresh();
    }//GEN-LAST:event_refreshButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel legacyModeLabel;
    private javax.swing.JPanel legacyModePanel;
    private javax.swing.JLabel manualOnlyModeLabel;
    private javax.swing.JPanel manualOnlyModePanel;
    private javax.swing.JLabel operationLabel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton refreshButton;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void performCloseClick() {
        UiUtils.doButtonClick(closeButton);
    }

    @Nonnull
    @Override
    public Optional<JButton> getDefaultButton() {
        return Optional.of(refreshButton);
    }

    @Override
    public void invokeOkEvent() {
        performCloseClick();
    }

    @Override
    public void invokeCancelEvent() {
        performCloseClick();
    }

    @Override
    public void setCloseActionEnabled(boolean enablement) {
        closeButton.setEnabled(enablement);
    }

    public void setProgressStatus(String status) {
        if (activeStatusComponent != null) {
            statusPanel.remove(activeStatusComponent);
        }

        if (status.isEmpty()) {
            activeStatusComponent = null;
        } else {
            progressBar.setString(status);
            progressBar.setStringPainted(!status.isEmpty());
            activeStatusComponent = progressBar;
            statusPanel.add(activeStatusComponent, BorderLayout.CENTER);
        }
        statusPanel.revalidate();
        statusPanel.repaint();
    }

    public void setOperationLabel(String text) {
        if (activeStatusComponent != null) {
            statusPanel.remove(activeStatusComponent);
        }

        if (text.isEmpty()) {
            activeStatusComponent = null;
        } else {
            operationLabel.setText(text);
            activeStatusComponent = operationLabel;
            statusPanel.add(activeStatusComponent, BorderLayout.CENTER);
        }
        statusPanel.revalidate();
        statusPanel.repaint();
    }

    public interface Controller extends CloseControlController {

        void performRefresh();
    }
}
