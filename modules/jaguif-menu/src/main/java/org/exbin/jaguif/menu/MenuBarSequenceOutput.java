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
package org.exbin.jaguif.menu;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.exbin.jaguif.App;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SubSequenceContribution;
import org.exbin.jaguif.contribution.api.TreeContributionSequenceOutput;
import org.exbin.jaguif.menu.api.ActionMenuContribution;
import org.exbin.jaguif.menu.api.DirectMenuContribution;
import org.exbin.jaguif.menu.api.SubMenuContribution;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.menu.api.MenuModuleApi;

/**
 * Menu bar sequence output.
 */
@ParametersAreNonnullByDefault
public class MenuBarSequenceOutput implements TreeContributionSequenceOutput {

    protected final JMenuBar menuBar;
    protected final ContextRegistration contextRegistration;
    protected final Map<String, ButtonGroup> buttonGroups;
    protected final Map<SequenceContribution, JMenuItem> menuItems = new HashMap<>();

    public MenuBarSequenceOutput(JMenuBar menuBar, ContextRegistration contextRegistration, Map<String, ButtonGroup> buttonGroups) {
        this.menuBar = menuBar;
        this.contextRegistration = contextRegistration;
        this.buttonGroups = buttonGroups;
    }

    @Override
    public boolean initItem(SequenceContribution contribution, String definitionId, String subId) {
        if (contribution instanceof SubMenuContribution) {
            Action action = ((SubMenuContribution) contribution).getAction();

            MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
            JMenu subMenu = menuModule.getMenuBuilder().createMenu();
            subMenu.setAction(action);
            ((SubMenuContribution) contribution).setSubMenu(subMenu);
            return true;
        }

        if (contribution instanceof ActionMenuContribution) {
            Action action = ((ActionMenuContribution) contribution).createAction();
            JMenuItem menuItem = MenuSequenceOutput.createMenuItem(action, buttonGroups);
            menuItems.put(contribution, menuItem);
        }

        return true;
    }

    @Override
    public void add(SequenceContribution contribution) {
        if (contribution instanceof SubSequenceContribution) {
            JMenu subMenu = ((SubMenuContribution) contribution).getSubMenu().get();
            menuBar.add(subMenu);
            MenuSequenceOutput.finishMenuItem(subMenu, contextRegistration);
            return;
        }

        if (contribution instanceof DirectMenuContribution) {
            menuBar.add(((DirectMenuContribution) contribution).getMenuItem());
            return;
        }

        JMenuItem menuItem = menuItems.get(contribution);
        menuBar.add(menuItem);
        MenuSequenceOutput.finishMenuItem(menuItem, contextRegistration);
    }

    @Override
    public void addSeparator() {
    }

    @Nonnull
    @Override
    public TreeContributionSequenceOutput createSubOutput(SubSequenceContribution subContribution) {
        return new MenuSequenceOutput(((SubMenuContribution) subContribution).getSubMenu().get(), contextRegistration, buttonGroups);
    }

    @Override
    public boolean isEmpty() {
        return menuBar.getMenuCount() == 0;
    }
}
