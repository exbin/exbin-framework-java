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
package org.exbin.framework.window.api.gui;

import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JButton;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.window.api.controller.DefaultControlController;

/**
 * Basic default control panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultControlPanel extends FooterControlPanel implements DefaultControlController.DefaultControlComponent {

    private DefaultControlController controller;
    private javax.swing.JButton okButton;
    private javax.swing.JButton cancelButton;

    public DefaultControlPanel() {
        super();
        init();
    }

    public DefaultControlPanel(ResourceBundle resourceBundle) {
        super(resourceBundle);
        init();
    }

    private void init() {
        okButton = new javax.swing.JButton();

        okButton.setText(resourceBundle.getString("okButton.text"));
        okButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (controller != null) {
                controller.controlActionPerformed(DefaultControlController.ControlActionType.OK);
            }
        });
        addButton(okButton, ButtonPosition.LAST_RIGHT);

        cancelButton = new javax.swing.JButton();
        cancelButton.setText(resourceBundle.getString("cancelButton.text"));
        cancelButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (controller != null) {
                controller.controlActionPerformed(DefaultControlController.ControlActionType.CANCEL);
            }
        });
        addButton(cancelButton, ButtonPosition.LAST_RIGHT);
    }

    public void setController(DefaultControlController controller) {
        this.controller = controller;
    }

    @Override
    public void performClick(DefaultControlController.ControlActionType actionType) {
        UiUtils.doButtonClick(actionType == DefaultControlController.ControlActionType.OK ? okButton : cancelButton);
    }

    @Override
    public void invokeOkEvent() {
        performClick(DefaultControlController.ControlActionType.OK);
    }

    @Override
    public void invokeCancelEvent() {
        performClick(DefaultControlController.ControlActionType.CANCEL);
    }

    @Nonnull
    @Override
    public Optional<JButton> getDefaultButton() {
        return Optional.of(okButton);
    }

    @Override
    public void setActionEnabled(DefaultControlController.ControlActionType actionType, boolean enablement) {
        switch (actionType) {
            case OK: {
                okButton.setEnabled(enablement);
                break;
            }
            case CANCEL: {
                cancelButton.setEnabled(enablement);
                break;
            }
            default:
                throw new IllegalStateException("Illegal action type " + actionType.name());
        }
    }
}
