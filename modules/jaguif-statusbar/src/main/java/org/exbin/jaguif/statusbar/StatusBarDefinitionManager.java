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
package org.exbin.jaguif.statusbar;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContributionRule;
import org.exbin.jaguif.statusbar.api.StatusBarDefinitionManagement;
import org.exbin.jaguif.statusbar.api.StatusBarManagement;

/**
 * Default status bar definition manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class StatusBarDefinitionManager implements StatusBarDefinitionManagement {

    protected final StatusBarManagement statusBarManager;
    protected final String statusBarId;
    protected final String moduleId;

    public StatusBarDefinitionManager(StatusBarManagement statusBarManager, String statusBarId, String moduleId) {
        this.statusBarManager = statusBarManager;
        this.statusBarId = statusBarId;
        this.moduleId = moduleId;
    }

    @Override
    public void registerStatusBarContribution(SequenceContribution contribution) {
        statusBarManager.registerStatusBarContribution(statusBarId, moduleId, contribution);
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerStatusBarGroup(String groupId) {
        return statusBarManager.registerStatusBarGroup(statusBarId, moduleId, groupId);
    }

    @Override
    public void registerStatusBarRule(SequenceContribution statusBarContribution, SequenceContributionRule rule) {
        statusBarManager.registerStatusBarRule(statusBarContribution, rule);
    }
}
