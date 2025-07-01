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
import org.exbin.framework.window.api.controller.OptionsControlController;

/**
 * Default control panel for options dialogs.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsControlPanel extends FooterControlPanel implements OptionsControlController.OptionsControlService {

    private OptionsControlController controller;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton applyOnceButton;

    public OptionsControlPanel() {
        super();
        init();
    }

    public OptionsControlPanel(ResourceBundle resourceBundle) {
        super(resourceBundle);
        init();
    }

    private void init() {
        saveButton = new javax.swing.JButton();

        saveButton.setText(resourceBundle.getString("saveButton.text"));
        saveButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (controller != null) {
                controller.controlActionPerformed(OptionsControlController.ControlActionType.SAVE);
            }
        });
        addButton(saveButton, ButtonPosition.LAST_RIGHT);

        cancelButton = new javax.swing.JButton();
        cancelButton.setText(resourceBundle.getString("cancelButton.text"));
        cancelButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (controller != null) {
                controller.controlActionPerformed(OptionsControlController.ControlActionType.CANCEL);
            }
        });
        addButton(cancelButton, ButtonPosition.LAST_RIGHT);

        applyOnceButton = new javax.swing.JButton();
        applyOnceButton.setText(resourceBundle.getString("applyOnceButton.text"));
        applyOnceButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (controller != null) {
                controller.controlActionPerformed(OptionsControlController.ControlActionType.APPLY_ONCE);
            }
        });
        addButton(applyOnceButton, ButtonPosition.LAST_LEFT);
    }

    public void setController(OptionsControlController controller) {
        this.controller = controller;
    }

    @Override
    public void performClick(OptionsControlController.ControlActionType actionType) {
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

    @Override
    public void invokeOkEvent() {
        performClick(OptionsControlController.ControlActionType.SAVE);
    }

    @Override
    public void invokeCancelEvent() {
        performClick(OptionsControlController.ControlActionType.CANCEL);
    }

    @Nonnull
    @Override
    public Optional<JButton> getDefaultButton() {
        return Optional.of(saveButton);
    }

    @Override
    public void setActionEnabled(OptionsControlController.ControlActionType actionType, boolean enablement) {
        switch (actionType) {
            case SAVE: {
                saveButton.setEnabled(enablement);
                break;
            }
            case CANCEL: {
                cancelButton.setEnabled(enablement);
                break;
            }
            case APPLY_ONCE: {
                applyOnceButton.setEnabled(enablement);
                break;
            }
            default:
                throw new IllegalStateException("Illegal action type " + actionType.name());
        }
    }
}
