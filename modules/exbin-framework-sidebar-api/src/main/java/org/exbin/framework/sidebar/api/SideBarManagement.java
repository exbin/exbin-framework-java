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
package org.exbin.framework.sidebar.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.action.api.ActionContextRegistration;

/**
 * Interface for side bar management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface SideBarManagement {

    /**
     * Builds side bar from given síde bar id.
     *
     * @param targetSideBar output side bar
     * @param sideBarId side bar definition id
     * @param actionContextRegistration action context registration
     */
    void buildSideBar(SideBar targetSideBar, String sideBarId, ActionContextRegistration actionContextRegistration);

    /**
     * Registers side bar.
     *
     * @param sideBarId side bar id
     * @param pluginId plugin id
     */
    void registerSideBar(String sideBarId, String pluginId);

    /**
     * Registers side bar item contribution.
     *
     * @param sideBarId side bar id
     * @param moduleId module id
     * @param action item action
     * @return item contribution
     */
    @Nonnull
    ActionSideBarContribution registerSideBarItem(String sideBarId, String moduleId, Action action);

    /**
     * Registers side bar item contribution.
     *
     * @param sideBarId side bar id
     * @param moduleId module id
     * @param component side bar component
     * @return item contribution
     */
    @Nonnull
    ComponentSideBarContribution registerSideBarItem(String sideBarId, String moduleId, SideBarComponent component);

    /**
     * Registers side bar group.
     *
     * @param sideBarId side bar id
     * @param moduleId module id
     * @param groupId group id
     * @return group contribution
     */
    @Nonnull
    GroupSequenceContribution registerSideBarGroup(String sideBarId, String moduleId, String groupId);

    /**
     * Register contribution rule.
     *
     * @param contribution contribution
     * @param rule rule
     */
    void registerSideBarRule(SequenceContribution contribution, SequenceContributionRule rule);
}
