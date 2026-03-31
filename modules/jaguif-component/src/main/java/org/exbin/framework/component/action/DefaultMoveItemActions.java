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
package org.exbin.framework.component.action;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.component.ComponentModule;
import org.exbin.framework.component.api.action.MoveItemActions;
import org.exbin.framework.component.api.toolbar.SideToolBar;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Item movement default action set.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultMoveItemActions implements MoveItemActions {

    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(ComponentModule.class);

    public DefaultMoveItemActions() {
    }

    @Nonnull
    @Override
    public MoveUpAction createMoveUpAction() {
        MoveUpAction moveUpAction = new MoveUpAction();
        moveUpAction.setup(resourceBundle);
        return moveUpAction;
    }

    @Nonnull
    @Override
    public MoveDownAction createMoveDownAction() {
        MoveDownAction moveDownAction = new MoveDownAction();
        moveDownAction.setup(resourceBundle);
        return moveDownAction;
    }

    @Nonnull
    @Override
    public MoveTopAction createMoveTopAction() {
        MoveTopAction moveTopAction = new MoveTopAction();
        moveTopAction.setup(resourceBundle);
        return moveTopAction;
    }

    @Nonnull
    @Override
    public MoveBottomAction createMoveBottomAction() {
        MoveBottomAction moveBottomAction = new MoveBottomAction();
        moveBottomAction.setup(resourceBundle);
        return moveBottomAction;
    }

    @Override
    public void registerActions(SideToolBar sideToolBar) {
        sideToolBar.addAction(createMoveTopAction());
        sideToolBar.addAction(createMoveUpAction());
        sideToolBar.addAction(createMoveDownAction());
        sideToolBar.addAction(createMoveBottomAction());
    }
}
