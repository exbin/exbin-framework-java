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
import org.exbin.framework.window.api.handler.OptionsControlHandler;

/**
 * Default control panel for options dialogs.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsControlPanel extends FooterControlPanel implements OptionsControlHandler.OptionsControlService {

    private OptionsControlHandler handler;
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
            if (handler != null) {
                handler.controlActionPerformed(OptionsControlHandler.ControlActionType.SAVE);
            }
        });
        addButton(saveButton, ButtonPosition.LAST_RIGHT);

        cancelButton = new javax.swing.JButton();
        cancelButton.setText(resourceBundle.getString("cancelButton.text"));
        cancelButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (handler != null) {
                handler.controlActionPerformed(OptionsControlHandler.ControlActionType.CANCEL);
            }
        });
        addButton(cancelButton, ButtonPosition.LAST_RIGHT);

        applyOnceButton = new javax.swing.JButton();
        applyOnceButton.setText(resourceBundle.getString("applyOnceButton.text"));
        applyOnceButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (handler != null) {
                handler.controlActionPerformed(OptionsControlHandler.ControlActionType.APPLY_ONCE);
            }
        });
        addButton(applyOnceButton, ButtonPosition.LAST_LEFT);
    }

    public void setHandler(OptionsControlHandler handler) {
        this.handler = handler;
    }

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

    @Override
    public void invokeOkEvent() {
        performClick(OptionsControlHandler.ControlActionType.SAVE);
    }

    @Override
    public void invokeCancelEvent() {
        performClick(OptionsControlHandler.ControlActionType.CANCEL);
    }

    @Nonnull
    @Override
    public Optional<JButton> getDefaultButton() {
        return Optional.of(saveButton);
    }

    @Nonnull
    @Override
    public OptionsControlHandler.OptionsControlEnablementListener createEnablementListener() {
        return (OptionsControlHandler.ControlActionType actionType, boolean enablement) -> {
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
        };
    }
}
