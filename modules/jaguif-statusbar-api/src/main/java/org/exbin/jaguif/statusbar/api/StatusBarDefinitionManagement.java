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
package org.exbin.jaguif.statusbar.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.contribution.api.GroupSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContributionRule;

/**
 * Interface for registered status bars definition management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface StatusBarDefinitionManagement {

    /**
     * Registers contribution to the given status bar.
     *
     * @param contribution status bar contribution
     */
    void registerStatusBarContribution(SequenceContribution contribution);

    /**
     * Registers group as a child item for given status bar.
     *
     * @param groupId group id
     * @return status bar contribution
     */
    @Nonnull
    GroupSequenceContribution registerStatusBarGroup(String groupId);

    /**
     * Registers status bar contribution rule.
     *
     * @param contribution status bar contribution
     * @param rule status bar contribution rule
     */
    void registerStatusBarRule(SequenceContribution contribution, SequenceContributionRule rule);

    /**
     * Creates status bar for given id.
     *
     * @param statusBarId status bar id
     * @return status bar
     */
    @Nonnull
    StatusBar createStatusBar(String statusBarId);
}
