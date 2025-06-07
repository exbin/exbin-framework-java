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
import org.exbin.framework.window.api.handler.CloseControlHandler;

/**
 * Basic close control panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CloseControlPanel extends FooterControlPanel implements CloseControlHandler.CloseControlService {

    private CloseControlHandler handler;
    private javax.swing.JButton closeButton;

    public CloseControlPanel() {
        super();
        init();
    }

    public CloseControlPanel(ResourceBundle resourceBundle) {
        super(resourceBundle);
        init();
    }

    private void init() {
        closeButton = new javax.swing.JButton();

        closeButton.setText(resourceBundle.getString("closeButton.text"));
        closeButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            if (handler != null) {
                handler.controlActionPerformed();
            }
        });
        addButton(closeButton, ButtonPosition.LAST_RIGHT);
    }

    public void setHandler(CloseControlHandler handler) {
        this.handler = handler;
    }

    @Override
    public void performCloseClick() {
        UiUtils.doButtonClick(closeButton);
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
    public Optional<JButton> getDefaultButton() {
        return Optional.of(closeButton);
    }

    @Nonnull
    @Override
    public CloseControlHandler.CloseControlEnablementListener createEnablementListener() {
        return closeButton::setEnabled;
    }
}
