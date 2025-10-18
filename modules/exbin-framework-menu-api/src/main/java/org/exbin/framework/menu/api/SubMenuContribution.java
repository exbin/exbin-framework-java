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
package org.exbin.framework.menu.api;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JMenu;
import org.exbin.framework.contribution.api.SubSequenceContribution;
import org.exbin.framework.contribution.api.TreeContributionSequenceOutput;

/**
 * Record of sub/child menu contribution.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SubMenuContribution implements SubSequenceContribution {

    protected final String subMenuId;
    protected final Action action;
    protected JMenu subMenu;
    protected TreeContributionSequenceOutput subOutput;

    public SubMenuContribution(String subMenuId, Action action) {
        this.subMenuId = subMenuId;
        this.action = action;
    }

    @Nonnull
    @Override
    public String getContributionId() {
        return subMenuId;
    }

    @Nonnull
    public String getSubMenuId() {
        return subMenuId;
    }

    @Nonnull
    public Action getAction() {
        return action;
    }

    @Nullable
    public JMenu getSubMenu() {
        return subMenu;
    }

    public void setSubMenu(JMenu subMenu) {
        this.subMenu = subMenu;
    }

    @Nullable
    @Override
    public Optional<TreeContributionSequenceOutput> getSubOutput() {
        return Optional.ofNullable(subOutput);
    }

    public void setSubOutput(@Nullable TreeContributionSequenceOutput subOutput) {
        this.subOutput = subOutput;
    }
}
