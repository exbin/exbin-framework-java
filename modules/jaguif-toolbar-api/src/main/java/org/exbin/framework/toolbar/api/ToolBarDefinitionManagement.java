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
package org.exbin.framework.toolbar.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;

/**
 * Interface for tool bar definition management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface ToolBarDefinitionManagement {

    /**
     * Registers item as a child item for given tool bar.
     *
     * @param action action
     * @return toolbar contribution
     */
    @Nonnull
    ActionToolBarContribution registerToolBarItem(Action action);

    /**
     * Registers group as a child item for given tool bar.
     *
     * @param groupId group id
     * @return toolbar contribution
     */
    @Nonnull
    GroupSequenceContribution registerToolBarGroup(String groupId);

    /**
     * Registers tool bar contribution rule.
     *
     * @param toolBarContribution tool bar contribution
     * @param rule tool bar contribution rule
     */
    void registerToolBarRule(SequenceContribution toolBarContribution, SequenceContributionRule rule);
}
