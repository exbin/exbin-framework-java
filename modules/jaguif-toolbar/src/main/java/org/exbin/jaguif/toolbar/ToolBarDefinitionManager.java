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
package org.exbin.jaguif.toolbar;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContributionRule;
import org.exbin.jaguif.toolbar.api.ToolBarDefinitionManagement;
import org.exbin.jaguif.toolbar.api.ToolBarManagement;

/**
 * Default toolbar definition manager.
 */
@ParametersAreNonnullByDefault
public class ToolBarDefinitionManager implements ToolBarDefinitionManagement {

    protected final ToolBarManagement toolBarManager;
    protected final String toolBarId;
    protected final String moduleId;

    public ToolBarDefinitionManager(ToolBarManagement toolBarManager, String toolBarId, String moduleId) {
        this.toolBarManager = toolBarManager;
        this.toolBarId = toolBarId;
        this.moduleId = moduleId;
    }

    @Override
    public void registerToolBarContribution(SequenceContribution contribution) {
        toolBarManager.registerToolBarContribution(toolBarId, moduleId, contribution);
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

    @Nonnull
    @Override
    public List<SequenceContribution> getContributions() {
        return toolBarManager.getContributions();
    }
}
