/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.component.api.action;

import javax.annotation.Nonnull;
import javax.swing.Action;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.toolbar.api.ToolBarManagement;

/**
 * Interface for item edit action set.
 */
public interface EditItemActions {

    /**
     * Returns add item action.
     *
     * @return add item action
     */
    @Nonnull
    Action createAddItemAction();

    /**
     * Returns edit item action.
     *
     * @return edit item action
     */
    @Nonnull
    Action createEditItemAction();

    /**
     * Returns delete item action.
     *
     * @return delete item action
     */
    @Nonnull
    Action createDeleteItemAction();

    /**
     * Returns add item contribution.
     *
     * @return add item contribution
     */
    @Nonnull
    SequenceContribution createAddItemContribution();

    /**
     * Returns edit item contribution.
     *
     * @return edit item contribution
     */
    @Nonnull
    SequenceContribution createEditItemContribution();

    /**
     * Returns delete item contribution.
     *
     * @return delete item contribution
     */
    @Nonnull
    SequenceContribution createDeleteItemContribution();

    /**
     * Registers contributions to tool bar.
     *
     * @param toolBarManager tool bar manager
     */
    void registerToolBarContributions(ToolBarManagement toolBarManager);
}
