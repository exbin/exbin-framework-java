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
package org.exbin.framework.action.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JToolBar;

/**
 * Interface for registered tool bars management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ToolBarManagement {

    /**
     * Returns tool bar using given identificator.
     *
     * @param targetToolBar target toolbar
     * @param toolBarId toolbar id
     * @param activationUpdateService activation update service
     */
    void buildToolBar(JToolBar targetToolBar, String toolBarId, ActionContextService activationUpdateService);

    /**
     * Registers tool bar associating it with given identificator.
     *
     * @param toolBarId toolbar id
     */
    void registerToolBar(String toolBarId);

    /**
     * Registers item as a child item for given tool bar.
     *
     * @param toolBarId toolbar id
     * @param action action
     * @return toolbar contribution
     */
    @Nonnull
    ToolBarContribution registerToolBarItem(String toolBarId, Action action);

    /**
     * Registers group as a child item for given tool bar.
     *
     * @param toolBarId toolbar id
     * @param groupId group id
     * @return toolbar contribution
     */
    @Nonnull
    ToolBarContribution registerToolBarGroup(String toolBarId, String groupId);

    /**
     * Registers tool bar contribution rule.
     *
     * @param toolBarContribution tool bar contribution
     * @param rule tool bar contribution rule
     */
    void registerToolBarRule(ToolBarContribution toolBarContribution, ToolBarContributionRule rule);
}
