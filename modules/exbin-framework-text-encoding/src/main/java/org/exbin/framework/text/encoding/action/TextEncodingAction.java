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
package org.exbin.framework.text.encoding.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.text.encoding.gui.TextEncodingPanel;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.controller.DefaultControlController;
import org.exbin.framework.action.api.ActionContextChangeRegistration;
import org.exbin.framework.context.api.StateChangeMessage;
import org.exbin.framework.text.encoding.ContextEncoding;
import org.exbin.framework.text.encoding.CharsetEncodingState;

/**
 * Text encoding action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextEncodingAction extends AbstractAction {

    public static final String ACTION_ID = "textEncodingAction";

    private CharsetEncodingState textEncodingState;
    private Component component;

    public TextEncodingAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ActionContextChangeRegistration registrar) -> {
            registrar.registerUpdateListener(ContextEncoding.class, (instance) -> {
                updateByContext(instance);
            });
            registrar.registerContextMessageListener(ContextEncoding.class, (ContextEncoding instance, StateChangeMessage changeMessage) -> {
                if (ContextEncoding.ChangeMessage.ENCODING_CHANGE.equals(changeMessage)) {
                    updateByContext(instance);
                }
            });
        });
    }

    public void updateByContext(ContextEncoding instance) {
        textEncodingState = instance instanceof CharsetEncodingState ? (CharsetEncodingState) instance : null;
        setEnabled(textEncodingState != null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final TextEncodingPanel encodingPanel = new TextEncodingPanel();
        encodingPanel.setCurrentEncoding(textEncodingState.getEncoding());
        DefaultControlPanel controlPanel = new DefaultControlPanel();
        final WindowHandler dialog = windowModule.createDialog(encodingPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), encodingPanel.getClass(), encodingPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, encodingPanel.getResourceBundle());
        controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
            if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                Optional<String> encoding = encodingPanel.getCurrentEncoding();
                if (encoding.isPresent()) {
                    textEncodingState.setEncoding(encoding.get());
                }
            }

            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(component);
    }
}
