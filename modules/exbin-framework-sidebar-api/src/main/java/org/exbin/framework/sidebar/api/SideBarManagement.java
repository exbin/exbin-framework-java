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

/**
 * Interface for registered side bars management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface SideBarManagement {

    /**
     * Registers item as a child item for given side bar.
     *
     * @param sideBarId sidebar id
     * @param action action
     * @return sidebar contribution
     */
    @Nonnull
    SideBarContribution registerSideBarItem(String sideBarId, Action action);

    /**
     * Registers group as a child item for given side bar.
     *
     * @param sideBarId sidebar id
     * @param groupId group id
     * @return sidebar contribution
     */
    @Nonnull
    SideBarContribution registerSideBarGroup(String sideBarId, String groupId);

    /**
     * Registers side bar contribution rule.
     *
     * @param sideBarContribution side bar contribution
     * @param rule side bar contribution rule
     */
    void registerSideBarRule(SideBarContribution sideBarContribution, SideBarContributionRule rule);
}
