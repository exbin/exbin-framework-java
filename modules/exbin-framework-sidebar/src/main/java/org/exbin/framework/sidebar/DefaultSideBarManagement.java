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
package org.exbin.framework.sidebar;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.sidebar.api.SideBarContribution;
import org.exbin.framework.sidebar.api.SideBarContributionRule;
import org.exbin.framework.sidebar.api.SideBarManagement;

/**
 * Default side bar management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultSideBarManagement implements SideBarManagement {

    private final SideBarManager sideBarManager;
    private final String sideBarId;
    private final String moduleId;

    public DefaultSideBarManagement(SideBarManager sideBarManager, String sideBarId, String moduleId) {
        this.sideBarManager = sideBarManager;
        this.sideBarId = sideBarId;
        this.moduleId = moduleId;
    }

    @Nonnull
    @Override
    public SideBarContribution registerSideBarItem(String sideBarId, Action action) {
        return sideBarManager.registerSideBarItem(sideBarId, moduleId, action);
    }

    @Nonnull
    @Override
    public SideBarContribution registerSideBarGroup(String sideBarId, String groupId) {
        return sideBarManager.registerSideBarGroup(sideBarId, moduleId, groupId);
    }

    @Override
    public void registerSideBarRule(SideBarContribution sideBarContribution, SideBarContributionRule rule) {
        sideBarManager.registerSideBarRule(sideBarContribution, rule);
    }
}
