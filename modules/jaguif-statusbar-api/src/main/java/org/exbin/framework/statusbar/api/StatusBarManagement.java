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
package org.exbin.framework.statusbar.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JToolBar;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.action.api.ActionContextRegistration;

/**
 * Interface for status bar management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface StatusBarManagement {

    /**
     * Builds status bar from given síde bar id.
     *
     * @param targetStatusBar output status bar
     * @param statusBarId status bar definition id
     * @param actionContextRegistration action context registration
     */
    void buildStatusBar(JToolBar targetStatusBar, String statusBarId, ActionContextRegistration actionContextRegistration);

    /**
     * Registers status bar.
     *
     * @param statusBarId status bar id
     * @param pluginId plugin id
     */
    void registerStatusBar(String statusBarId, String pluginId);

    /**
     * Registers status bar item contribution.
     *
     * @param statusBarId status bar id
     * @param pluginId plugin id
     * @param action item action
     * @return item contribution
     */
    @Nonnull
    ComponentStatusBarContribution registerStatusBarItem(String statusBarId, String pluginId, Action action);

    /**
     * Registers status bar group.
     *
     * @param statusBarId status bar id
     * @param pluginId plugin id
     * @param groupId group id
     * @return group contribution
     */
    @Nonnull
    GroupSequenceContribution registerStatusBarGroup(String statusBarId, String pluginId, String groupId);

    /**
     * Register contribution rule.
     *
     * @param contribution contribution
     * @param rule rule
     */
    void registerStatusBarRule(SequenceContribution contribution, SequenceContributionRule rule);
}
