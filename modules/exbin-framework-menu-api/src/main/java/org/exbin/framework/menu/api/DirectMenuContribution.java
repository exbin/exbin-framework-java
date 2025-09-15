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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.contribution.api.ItemSequenceContribution;

/**
 * Record for direct menu item contribution.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DirectMenuContribution implements ItemSequenceContribution {

    private MenuItemProvider menuItemProvider;
    private String contributionId;
    private JMenuItem menuItem;

    public DirectMenuContribution(MenuItemProvider menuItemProvider) {
        this.menuItemProvider = menuItemProvider;
    }

    @Nonnull
    @Override
    public String getContributionId() {
        if (menuItem == null) {
            createItem();
        }

        return contributionId;
    }

    @Nonnull
    public JMenuItem getMenuItem() {
        if (menuItem == null) {
            createItem();
        }

        return menuItem;
    }

    private void createItem() {
        menuItem = menuItemProvider.createMenuItem();
        contributionId = "";
        Action action = menuItem.getAction();
        if (action != null) {
            contributionId = (String) action.getValue(ActionConsts.ACTION_ID);
            if (contributionId == null) {
                contributionId = "";
            }
        }
    }
}
