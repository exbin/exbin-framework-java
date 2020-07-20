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
package org.exbin.framework.gui.component;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.gui.component.api.toolbar.MoveItemActions;
import org.exbin.framework.gui.component.api.toolbar.MoveItemActionsHandler;
import org.exbin.framework.gui.component.api.toolbar.SideToolBar;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Item movement default action set.
 *
 * @version 0.2.0 2016/03/21
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultMoveItemActions implements MoveItemActions {

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(GuiComponentModule.class);

    private MoveItemActionsHandler actionsHandler = null;
    private Action moveUpAction = null;
    private Action moveDownAction = null;
    private Action moveTopAction = null;
    private Action moveBottomAction = null;

    public DefaultMoveItemActions() {
    }

    @Override
    public void setMoveItemActionsHandler(MoveItemActionsHandler actionsHandler) {
        this.actionsHandler = actionsHandler;
    }

    @Nonnull
    @Override
    public Action getMoveUpAction() {
        if (moveUpAction == null) {
            moveUpAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionsHandler.performMoveUp();
                }
            };
            ActionUtils.setupAction(moveUpAction, resourceBundle, "moveItemUpAction");
            moveUpAction.setEnabled(false);
        }
        return moveUpAction;
    }

    @Nonnull
    @Override
    public Action getMoveDownAction() {
        if (moveDownAction == null) {
            moveDownAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionsHandler.performMoveDown();
                }
            };
            ActionUtils.setupAction(moveDownAction, resourceBundle, "moveItemDownAction");
            moveDownAction.setEnabled(false);
        }
        return moveDownAction;
    }

    @Nonnull
    @Override
    public Action getMoveTopAction() {
        if (moveTopAction == null) {
            moveTopAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionsHandler.performMoveTop();
                }
            };
            ActionUtils.setupAction(moveTopAction, resourceBundle, "moveItemTopAction");
            moveTopAction.setEnabled(false);
        }
        return moveTopAction;
    }

    @Nonnull
    @Override
    public Action getMoveBottomAction() {
        if (moveBottomAction == null) {
            moveBottomAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    actionsHandler.performMoveBottom();
                }
            };
            ActionUtils.setupAction(moveBottomAction, resourceBundle, "moveItemBottomAction");
            moveBottomAction.setEnabled(false);
        }
        return moveBottomAction;
    }

    @Override
    public void updateMoveItemActions() {
        boolean enabled = actionsHandler.isEditable() && actionsHandler.isSelection();
        if (moveUpAction != null) {
            moveUpAction.setEnabled(enabled);
        }
        if (moveDownAction != null) {
            moveDownAction.setEnabled(enabled);
        }
        if (moveTopAction != null) {
            moveTopAction.setEnabled(enabled);
        }
        if (moveBottomAction != null) {
            moveBottomAction.setEnabled(enabled);
        }
    }

    @Override
    public void registerActions(SideToolBar sideToolBar) {
        sideToolBar.addAction(getMoveTopAction());
        sideToolBar.addAction(getMoveUpAction());
        sideToolBar.addAction(getMoveDownAction());
        sideToolBar.addAction(getMoveBottomAction());
        updateMoveItemActions();
    }
}
