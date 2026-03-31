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
import org.exbin.framework.window.api.controller.MultiStepControlController;

/**
 * Multi-step control panel for options dialogs.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MultiStepControlPanel extends FooterControlPanel implements MultiStepControlController.MultiStepControlComponent {

    private MultiStepControlController controller;
    private javax.swing.JButton finishButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton previousButton;

    public MultiStepControlPanel() {
        super();
        init();
    }

    public MultiStepControlPanel(ResourceBundle resourceBundle) {
        super(resourceBundle);
        init();
    }

    private void init() {
        finishButton = new javax.swing.JButton();
        finishButton.setEnabled(false);
        finishButton.setText(resourceBundle.getString("finishButton.text"));
        finishButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (controller != null) {
                controller.controlActionPerformed(MultiStepControlController.ControlActionType.FINISH);
            }
        });
        addButton(finishButton, ButtonPosition.LAST_RIGHT);

        cancelButton = new javax.swing.JButton();
        cancelButton.setText(resourceBundle.getString("cancelButton.text"));
        cancelButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (controller != null) {
                controller.controlActionPerformed(MultiStepControlController.ControlActionType.CANCEL);
            }
        });
        addButton(cancelButton, ButtonPosition.LAST_RIGHT);

        nextButton = new javax.swing.JButton();
        nextButton.setEnabled(false);
        nextButton.setText(resourceBundle.getString("nextButton.text"));
        nextButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (controller != null) {
                controller.controlActionPerformed(MultiStepControlController.ControlActionType.NEXT);
            }
        });
        addButton(nextButton, ButtonPosition.FIRST_RIGHT);

        previousButton = new javax.swing.JButton();
        previousButton.setEnabled(false);
        previousButton.setText(resourceBundle.getString("previousButton.text"));
        previousButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (controller != null) {
                controller.controlActionPerformed(MultiStepControlController.ControlActionType.PREVIOUS);
            }
        });
        addButton(previousButton, ButtonPosition.FIRST_RIGHT);
    }

    public void setController(MultiStepControlController controller) {
        this.controller = controller;
    }

    @Override
    public void performClick(MultiStepControlController.ControlActionType actionType) {
        switch (actionType) {
            case FINISH: {
                UiUtils.doButtonClick(finishButton);
                break;
            }
            case CANCEL: {
                UiUtils.doButtonClick(cancelButton);
                break;
            }
            case NEXT: {
                UiUtils.doButtonClick(nextButton);
                break;
            }
            case PREVIOUS: {
                UiUtils.doButtonClick(previousButton);
                break;
            }
            default:
                throw new IllegalStateException("Illegal action type " + actionType.name());
        }
    }

    @Override
    public void invokeOkEvent() {
        performClick(MultiStepControlController.ControlActionType.FINISH);
    }

    @Override
    public void invokeCancelEvent() {
        performClick(MultiStepControlController.ControlActionType.CANCEL);
    }

    @Nonnull
    @Override
    public Optional<JButton> getDefaultButton() {
        return Optional.of(finishButton);
    }

    @Override
    public void setActionEnabled(MultiStepControlController.ControlActionType actionType, boolean enablement) {
        switch (actionType) {
            case FINISH: {
                finishButton.setEnabled(enablement);
                break;
            }
            case CANCEL: {
                cancelButton.setEnabled(enablement);
                break;
            }
            case NEXT: {
                nextButton.setEnabled(enablement);
                break;
            }
            case PREVIOUS: {
                previousButton.setEnabled(enablement);
                break;
            }
            default:
                throw new IllegalStateException("Illegal action type " + actionType.name());
        }
    }
}
