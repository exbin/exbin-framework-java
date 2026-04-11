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
package org.exbin.jaguif.frame.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.ActionType;
import org.exbin.jaguif.context.api.ContextChangeRegistration;
import org.exbin.jaguif.frame.ApplicationFrame;
import org.exbin.jaguif.frame.api.ComponentFrame;
import org.exbin.jaguif.frame.api.ContextFrame;

/**
 * View tool bar action.
 */
@ParametersAreNonnullByDefault
public class ViewToolBarAction extends AbstractAction {

    public static final String ACTION_ID = "viewToolBar";
    protected ComponentFrame frame;

    public void init(ResourceBundle resourceBundle) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        setEnabled(false);
        putValue(Action.SELECTED_KEY, true);
        putValue(ActionConsts.ACTION_TYPE, ActionType.CHECK);
        putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) (ContextChangeRegistration registrar) -> {
            registrar.registerChangeListener(ContextFrame.class, instance -> {
                updateByContext(instance);
            });
            registrar.registerStateUpdateListener(ContextFrame.class, (instance, message) -> {
                if (ContextFrame.UpdateType.BARS_LAYOUT_CHANGE.equals(message)) {
                    updateByContext(instance);
                }
            });
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof JMenuItem) {
            frame.setToolBarVisible(((JMenuItem) source).isSelected());
        }
    }

    public void updateByContext(ContextFrame context) {
        this.frame = context instanceof ComponentFrame ? (ApplicationFrame) context : null;
        setEnabled(frame != null);
        if (frame != null) {
            putValue(Action.SELECTED_KEY, frame.isToolBarVisible());
        }
    }
}
