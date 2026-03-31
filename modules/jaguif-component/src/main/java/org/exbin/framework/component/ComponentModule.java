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
package org.exbin.framework.component;

import org.exbin.framework.component.action.DefaultEditItemActions;
import org.exbin.framework.component.action.DefaultMoveItemActions;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.component.api.action.EditItemActions;
import org.exbin.framework.component.api.ComponentModuleApi;
import org.exbin.framework.component.api.action.MoveItemActions;
import org.exbin.framework.component.api.ContextEditItem;
import org.exbin.framework.component.api.ContextMoveItem;

/**
 * Implementation of framework component module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ComponentModule implements ComponentModuleApi {

    public ComponentModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    @Override
    public EditItemActions createEditItemActions(ContextEditItem editItemActionsHandler) {
        DefaultEditItemActions editActions = new DefaultEditItemActions();
        return editActions;
    }

    @Nonnull
    @Override
    public MoveItemActions createMoveItemActions(ContextMoveItem moveItemActionsHandler) {
        DefaultMoveItemActions moveActions = new DefaultMoveItemActions();
        return moveActions;
    }
}
