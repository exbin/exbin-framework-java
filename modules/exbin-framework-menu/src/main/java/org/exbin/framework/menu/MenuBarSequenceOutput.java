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
package org.exbin.framework.menu;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.exbin.framework.action.api.ActionContextManager;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SubSequenceContribution;
import org.exbin.framework.contribution.api.TreeContributionSequenceOutput;
import org.exbin.framework.menu.api.ActionMenuContribution;
import org.exbin.framework.menu.api.DirectMenuContribution;
import org.exbin.framework.menu.api.SubMenuContribution;
import org.exbin.framework.utils.UiUtils;

/**
 * Menu bar sequence output.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MenuBarSequenceOutput implements TreeContributionSequenceOutput {

    protected final JMenuBar menuBar;
    protected final ActionContextManager actionContextManager;
    protected final Map<String, ButtonGroup> buttonGroups;

    public MenuBarSequenceOutput(JMenuBar menuBar, ActionContextManager actionContextManager, Map<String, ButtonGroup> buttonGroups) {
        this.menuBar = menuBar;
        this.actionContextManager = actionContextManager;
        this.buttonGroups = buttonGroups;
    }

    @Override
    public boolean initItem(SequenceContribution contribution, String definitionId, String subId) {
        if (contribution instanceof SubMenuContribution) {
            Action action = ((SubMenuContribution) contribution).getAction();

            JMenu subMenu = UiUtils.createMenu();
            subMenu.setAction(action);
            ((SubMenuContribution) contribution).setSubMenu(subMenu);
            return true;
        }

        if (contribution instanceof ActionMenuContribution) {
            Action action = ((ActionMenuContribution) contribution).getAction();
            JMenuItem menuItem = MenuSequenceOutput.createMenuItem(action, buttonGroups);
            ((ActionMenuContribution) contribution).setMenuItem(menuItem);
        }

        return true;
    }

    @Override
    public void add(SequenceContribution contribution) {
        if (contribution instanceof SubSequenceContribution) {
            JMenu subMenu = ((SubMenuContribution) contribution).getSubMenu().get();
            menuBar.add(subMenu);
            MenuSequenceOutput.finishMenuItem(subMenu, actionContextManager);
            return;
        }

        if (contribution instanceof DirectMenuContribution) {
            menuBar.add(((DirectMenuContribution) contribution).getMenuItem());
            return;
        }

        JMenuItem menuItem = ((ActionMenuContribution) contribution).getMenuItem();
        menuBar.add(menuItem);
        MenuSequenceOutput.finishMenuItem(menuItem, actionContextManager);
    }

    @Override
    public void addSeparator() {
    }

    @Nonnull
    @Override
    public TreeContributionSequenceOutput createSubOutput(SubSequenceContribution subContribution) {
        return new MenuSequenceOutput(((SubMenuContribution) subContribution).getSubMenu().get(), actionContextManager, buttonGroups);
    }

    @Override
    public boolean isEmpty() {
        return menuBar.getMenuCount() == 0;
    }
}
