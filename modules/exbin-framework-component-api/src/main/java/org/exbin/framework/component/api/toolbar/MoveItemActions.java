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
package org.exbin.framework.component.api.toolbar;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.component.api.ActionsProvider;

/**
 * Interface for item movement action set.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface MoveItemActions extends ActionsProvider {

    /**
     * Sets move action handler.
     *
     * @param actionsHandler actions handler
     */
    void setMoveItemActionsHandler(MoveItemActionsHandler actionsHandler);

    /**
     * Returns move up action.
     *
     * @return move up action
     */
    @Nonnull
    Action getMoveUpAction();

    /**
     * Returns move down action.
     *
     * @return move down action
     */
    @Nonnull
    Action getMoveDownAction();

    /**
     * Returns move top action.
     *
     * @return move top action
     */
    @Nonnull
    Action getMoveTopAction();

    /**
     * Returns move bottom action.
     *
     * @return move bottom action
     */
    @Nonnull
    Action getMoveBottomAction();

    /**
     * Updates state of these actions according to handler.
     */
    void updateMoveItemActions();
}
