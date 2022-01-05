/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.component;

import org.exbin.framework.component.action.DefaultEditItemActions;
import org.exbin.framework.component.action.DefaultMoveItemActions;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.component.api.DialogControlPanelHandler;
import org.exbin.framework.component.api.toolbar.EditItemActions;
import org.exbin.framework.component.api.toolbar.EditItemActionsHandler;
import org.exbin.framework.component.api.ComponentModuleApi;
import org.exbin.framework.component.api.toolbar.MoveItemActions;
import org.exbin.framework.component.api.toolbar.MoveItemActionsHandler;
import org.exbin.framework.component.gui.DialogControlPanel;
import org.exbin.xbup.plugin.XBModuleHandler;

/**
 * Implementation of framework component module.
 *
 * @version 0.2.1 2019/07/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ComponentModule implements ComponentModuleApi {

    private XBApplication application;

    public ComponentModule() {
    }

    @Override
    public void init(XBModuleHandler application) {
        this.application = (XBApplication) application;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    @Override
    public JPanel getTableEditPanel() {
        // return new TableEditPanel();
        return null;
    }

    @Nonnull
    @Override
    public EditItemActions createEditItemActions(EditItemActionsHandler editItemActionsHandler) {
        DefaultEditItemActions editActions = new DefaultEditItemActions();
        editActions.setEditItemActionsHandler(editItemActionsHandler);
        return editActions;
    }

    @Nonnull
    @Override
    public MoveItemActions createMoveItemActions(MoveItemActionsHandler moveItemActionsHandler) {
        DefaultMoveItemActions moveActions = new DefaultMoveItemActions();
        moveActions.setMoveItemActionsHandler(moveItemActionsHandler);
        return moveActions;
    }

    @Nonnull
    @Override
    public JPanel createDialogControlPanel(DialogControlPanelHandler handler) {
        DialogControlPanel controlPanel =  new DialogControlPanel();
        controlPanel.setControl(handler);
        return controlPanel;
    }
}
