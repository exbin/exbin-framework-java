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
import java.nio.charset.Charset;
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
import org.exbin.framework.action.api.ActionContextChangeManager;
import org.exbin.framework.action.api.ActiveComponent;
import org.exbin.framework.text.encoding.gui.TextEncodingPanel;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.window.api.controller.DefaultControlController;
import org.exbin.framework.text.encoding.TextEncodingController;

/**
 * Text encoding action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextEncodingAction extends AbstractAction {

    public static final String ACTION_ID = "textEncodingAction";

    private TextEncodingController textEncodingSupported;
    private Component component;

    public TextEncodingAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ActionContextChangeManager manager) -> {
            manager.registerUpdateListener(TextEncodingController.class, (instance) -> {
                textEncodingSupported = instance;
                setEnabled(textEncodingSupported != null);
            });
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        final TextEncodingPanel encodingPanel = new TextEncodingPanel();
        encodingPanel.setCurrentEncoding(textEncodingSupported.getCharset().name());
        DefaultControlPanel controlPanel = new DefaultControlPanel();
        final WindowHandler dialog = windowModule.createDialog(encodingPanel, controlPanel);
        windowModule.addHeaderPanel(dialog.getWindow(), encodingPanel.getClass(), encodingPanel.getResourceBundle());
        windowModule.setWindowTitle(dialog, encodingPanel.getResourceBundle());
        controlPanel.setController((DefaultControlController.ControlActionType actionType) -> {
            if (actionType != DefaultControlController.ControlActionType.CANCEL) {
                Optional<String> encoding = encodingPanel.getCurrentEncoding();
                if (encoding.isPresent()) {
                    textEncodingSupported.setCharset(Charset.forName(encoding.get()));
                }
            }

            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(component);
    }
}
