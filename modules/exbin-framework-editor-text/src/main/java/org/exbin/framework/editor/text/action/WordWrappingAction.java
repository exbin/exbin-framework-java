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
package org.exbin.framework.editor.text.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.ActiveComponent;
import org.exbin.framework.editor.text.EditorTextPanelComponent;
import org.exbin.framework.editor.text.gui.TextPanel;
import org.exbin.framework.action.api.ActionContextChangeRegistration;

/**
 * Word wrapping action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WordWrappingAction extends AbstractAction {

    public static final String ACTION_ID = "viewWordWrappingAction";

    private TextPanel textPanel;

    public WordWrappingAction() {
    }

    public void setup(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, new ActionContextChange() {
            @Override
            public void register(ActionContextChangeRegistration registrar) {
                registrar.registerUpdateListener(ActiveComponent.class, (instance) -> {
                    textPanel = instance instanceof EditorTextPanelComponent ? ((EditorTextPanelComponent) instance).getTextPanel() : null;
                    setEnabled(textPanel != null);
                });
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!(textPanel instanceof TextPanel)) {
            return;
        }

        boolean lineWraping = textPanel.changeLineWrap();
        putValue(Action.SELECTED_KEY, lineWraping);
    }
}
