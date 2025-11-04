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
package org.exbin.framework.toolbar.api;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JToolBar;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.action.api.ActionContextRegistration;

/**
 * Interface for tool bar management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ToolBarManagement {

    /**
     * Builds toolbar from given definition id.
     *
     * @param targetToolBar output tool bar
     * @param toolBarId tool bar definition id
     * @param actionContextRegistration action context registration
     */
    void buildToolBar(JToolBar targetToolBar, String toolBarId, ActionContextRegistration actionContextRegistration);

    /**
     * Builds toolbar with icons only from given definition id.
     *
     * @param targetToolBar output tool bar
     * @param toolBarId tool bar definition id
     * @param actionContextRegistration action context registration
     */
    void buildIconToolBar(JToolBar targetToolBar, String toolBarId, ActionContextRegistration actionContextRegistration);

    /**
     * Registers toolbar.
     *
     * @param toolBarId tool bar id
     * @param pluginId plugin id
     */
    void registerToolBar(String toolBarId, String pluginId);

    /**
     * Registers tool bar item contribution.
     *
     * @param toolBarId tool bar id
     * @param pluginId plugin id
     * @param action item action
     * @return item contribution
     */
    @Nonnull
    ActionToolBarContribution registerToolBarItem(String toolBarId, String pluginId, Action action);

    /**
     * Registers tool bar group.
     *
     * @param toolBarId tool bar id
     * @param pluginId plugin id
     * @param groupId group id
     * @return group contribution
     */
    @Nonnull
    GroupSequenceContribution registerToolBarGroup(String toolBarId, String pluginId, String groupId);

    /**
     * Register tool bar contribution rule.
     *
     * @param contribution tool bar contribution
     * @param rule tool bar rule
     */
    void registerToolBarRule(SequenceContribution contribution, SequenceContributionRule rule);

    /**
     * Returns list of managed actions.
     *
     * @return list of actions
     */
    @Nonnull
    List<Action> getAllManagedActions();
}
