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
import org.exbin.framework.window.api.controller.RemovalControlController;

/**
 * Basic control panel with support for removal.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class RemovalControlPanel extends FooterControlPanel implements RemovalControlController.RemovalControlService {

    private RemovalControlController controller;
    private javax.swing.JButton okButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton removeButton;

    public RemovalControlPanel() {
        super();
        init();
    }

    public RemovalControlPanel(ResourceBundle resourceBundle) {
        super(resourceBundle);
        init();
    }

    private void init() {
        okButton = new javax.swing.JButton();

        okButton.setText(resourceBundle.getString("okButton.text"));
        okButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (controller != null) {
                controller.controlActionPerformed(RemovalControlController.ControlActionType.OK);
            }
        });
        addButton(okButton, ButtonPosition.LAST_RIGHT);

        cancelButton = new javax.swing.JButton();
        cancelButton.setText(resourceBundle.getString("cancelButton.text"));
        cancelButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (controller != null) {
                controller.controlActionPerformed(RemovalControlController.ControlActionType.CANCEL);
            }
        });
        addButton(cancelButton, ButtonPosition.LAST_RIGHT);

        removeButton = new javax.swing.JButton();
        removeButton.setText(resourceBundle.getString("removeButton.text"));
        removeButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (controller != null) {
                controller.controlActionPerformed(RemovalControlController.ControlActionType.REMOVE);
            }
        });
        addButton(removeButton, ButtonPosition.LAST_LEFT);
    }

    public void setController(RemovalControlController controller) {
        this.controller = controller;
    }

    @Override
    public void performClick(RemovalControlController.ControlActionType actionType) {
        switch (actionType) {
            case OK: {
                UiUtils.doButtonClick(okButton);
                break;
            }
            case CANCEL: {
                UiUtils.doButtonClick(cancelButton);
                break;
            }
            case REMOVE: {
                UiUtils.doButtonClick(removeButton);
                break;
            }
            default:
                throw new IllegalStateException("Illegal action type " + actionType.name());
        }
    }

    @Override
    public void invokeOkEvent() {
        performClick(RemovalControlController.ControlActionType.OK);
    }

    @Override
    public void invokeCancelEvent() {
        performClick(RemovalControlController.ControlActionType.CANCEL);
    }

    @Nonnull
    @Override
    public Optional<JButton> getDefaultButton() {
        return Optional.of(okButton);
    }

    @Override
    public void setActionEnabled(RemovalControlController.ControlActionType actionType, boolean enablement) {
        switch (actionType) {
            case OK: {
                okButton.setEnabled(enablement);
                break;
            }
            case CANCEL: {
                cancelButton.setEnabled(enablement);
                break;
            }
            case REMOVE: {
                removeButton.setEnabled(enablement);
                break;
            }
            default:
                throw new IllegalStateException("Illegal action type " + actionType.name());
        }
    }
}
