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
import org.exbin.framework.toolbar.api.ToolBarContribution;
import org.exbin.framework.toolbar.api.ToolBarContributionRule;
import org.exbin.framework.toolbar.api.ToolBarManagement;

/**
 * Default toolbar management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultToolBarManagement implements ToolBarManagement {

    private final ToolBarManager toolBarManager;
    private final String toolBarId;
    private final String moduleId;

    public DefaultToolBarManagement(ToolBarManager toolBarManager, String toolBarId, String moduleId) {
        this.toolBarManager = toolBarManager;
        this.toolBarId = toolBarId;
        this.moduleId = moduleId;
    }

    @Nonnull
    @Override
    public ToolBarContribution registerToolBarItem(Action action) {
        return toolBarManager.registerToolBarItem(toolBarId, moduleId, action);
    }

    @Nonnull
    @Override
    public ToolBarContribution registerToolBarGroup(String groupId) {
        return toolBarManager.registerToolBarGroup(toolBarId, moduleId, groupId);
    }

    @Override
    public void registerToolBarRule(ToolBarContribution toolBarContribution, ToolBarContributionRule rule) {
        toolBarManager.registerToolBarRule(toolBarContribution, rule);
    }
}
