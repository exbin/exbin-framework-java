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
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.sidebar.api.ActionSideBarContribution;
import org.exbin.framework.sidebar.api.ComponentSideBarContribution;
import org.exbin.framework.sidebar.api.SideBarComponent;
import org.exbin.framework.sidebar.api.SideBarDefinitionManagement;

/**
 * Default side bar definition manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SideBarDefinitionManager implements SideBarDefinitionManagement {

    protected final SideBarManager sideBarManager;
    protected final String sideBarId;
    protected final String moduleId;

    public SideBarDefinitionManager(SideBarManager sideBarManager, String sideBarId, String moduleId) {
        this.sideBarManager = sideBarManager;
        this.sideBarId = sideBarId;
        this.moduleId = moduleId;
    }

    @Nonnull
    @Override
    public ActionSideBarContribution registerSideBarAction(Action action) {
        return sideBarManager.registerSideBarAction(sideBarId, moduleId, action);
    }

    @Nonnull
    @Override
    public ComponentSideBarContribution registerSideBarComponent(SideBarComponent sideBarComponent) {
        return sideBarManager.registerSideBarComponent(sideBarId, moduleId, sideBarComponent);
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerSideBarGroup(String groupId) {
        return sideBarManager.registerSideBarGroup(sideBarId, moduleId, groupId);
    }

    @Override
    public void registerSideBarRule(SequenceContribution sideBarContribution, SequenceContributionRule rule) {
        sideBarManager.registerSideBarRule(sideBarContribution, rule);
    }
}
