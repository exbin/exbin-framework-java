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
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SubSequenceContribution;
import org.exbin.jaguif.contribution.api.TreeContributionSequenceOutput;
import org.exbin.jaguif.menu.api.ActionMenuCreation;
import org.exbin.jaguif.menu.api.DirectMenuContribution;
import org.exbin.jaguif.menu.api.SubMenuContribution;
import org.exbin.jaguif.action.api.ActionContextRegistration;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;
import org.exbin.jaguif.menu.api.MenuModuleApi;

/**
 * Popup menu sequence output.
 */
@ParametersAreNonnullByDefault
public class PopupMenuSequenceOutput implements TreeContributionSequenceOutput {

    protected final JPopupMenu menu;
    protected final ActionContextRegistration actionContextRegistration;
    protected final Map<String, ButtonGroup> buttonGroups;
    protected final Map<SequenceContribution, JMenuItem> menuItems = new HashMap<>();

    public PopupMenuSequenceOutput(JPopupMenu menu, ActionContextRegistration actionContextRegistration, Map<String, ButtonGroup> buttonGroups) {
        this.menu = menu;
        this.actionContextRegistration = actionContextRegistration;
        this.buttonGroups = buttonGroups;
    }

    @Override
    public boolean initItem(SequenceContribution contribution, String definitionId, String subId) {
        if (contribution instanceof SubMenuContribution) {
            Action action = ((SubMenuContribution) contribution).getAction();

            if (action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    if (!menuCreation.shouldCreate(definitionId, subId)) {
                        return false;
                    }
                }
            }

            MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
            JMenu subMenu = menuModule.getMenuBuilder().createMenu();
            subMenu.setAction(action);
            ((SubMenuContribution) contribution).setSubMenu(subMenu);

            if (action != null) {
                ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
                if (menuCreation != null) {
                    menuCreation.onCreate(subMenu, definitionId, subId);
                }
            }

            return true;
        }

        Action action;
        JMenuItem menuItem;
        if (contribution instanceof ActionSequenceContribution) {
            menuItem = null;
            action = ((ActionSequenceContribution) contribution).createAction();
        } else {
            menuItem = ((DirectMenuContribution) contribution).getMenuItem();
            action = menuItem.getAction();
        }
        if (action != null) {
            ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
            if (menuCreation != null) {
                if (!menuCreation.shouldCreate(definitionId, subId)) {
                    return false;
                }
            }
        }

        if (contribution instanceof ActionSequenceContribution) {
            menuItem = MenuSequenceOutput.createMenuItem(action, buttonGroups);
            menuItems.put(contribution, menuItem);
        }

        if (action != null) {
            ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
            if (menuCreation != null) {
                menuCreation.onCreate(menuItem, definitionId, subId);
            }
        }

        return true;
    }

    @Override
    public void add(SequenceContribution contribution) {
        if (contribution instanceof SubMenuContribution) {
            JMenu subMenu = ((SubMenuContribution) contribution).getSubMenu().get();
            menu.add(subMenu);
            MenuSequenceOutput.finishMenuItem(subMenu, actionContextRegistration);
            return;
        }

        if (contribution instanceof DirectMenuContribution) {
            menu.add(((DirectMenuContribution) contribution).getMenuItem());
            return;
        }

        JMenuItem menuItem = menuItems.get(contribution);
        menu.add(menuItem);
        MenuSequenceOutput.finishMenuItem(menuItem, actionContextRegistration);
    }

    @Override
    public void addSeparator() {
        menu.addSeparator();
    }

    @Nonnull
    @Override
    public TreeContributionSequenceOutput createSubOutput(SubSequenceContribution subContribution) {
        return new MenuSequenceOutput(((SubMenuContribution) subContribution).getSubMenu().get(), actionContextRegistration, buttonGroups, true);
    }

    @Override
    public boolean isEmpty() {
        return menu.getComponentCount() == 0;
    }
}
