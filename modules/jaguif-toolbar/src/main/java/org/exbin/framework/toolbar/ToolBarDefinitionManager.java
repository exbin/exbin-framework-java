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
package org.exbin.framework.toolbar;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.toolbar.api.ActionToolBarContribution;
import org.exbin.framework.toolbar.api.ToolBarDefinitionManagement;

/**
 * Default toolbar definition manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ToolBarDefinitionManager implements ToolBarDefinitionManagement {

    protected final ToolBarManager toolBarManager;
    protected final String toolBarId;
    protected final String moduleId;

    public ToolBarDefinitionManager(ToolBarManager toolBarManager, String toolBarId, String moduleId) {
        this.toolBarManager = toolBarManager;
        this.toolBarId = toolBarId;
        this.moduleId = moduleId;
    }

    @Nonnull
    @Override
    public ActionToolBarContribution registerToolBarItem(Action action) {
        return toolBarManager.registerToolBarItem(toolBarId, moduleId, action);
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerToolBarGroup(String groupId) {
        return toolBarManager.registerToolBarGroup(toolBarId, moduleId, groupId);
    }

    @Override
    public void registerToolBarRule(SequenceContribution toolBarContribution, SequenceContributionRule rule) {
        toolBarManager.registerToolBarRule(toolBarContribution, rule);
    }
}
