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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.exbin.jaguif.App;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.contribution.api.SubSequenceContribution;
import org.exbin.jaguif.contribution.api.TreeContributionSequenceOutput;
import org.exbin.jaguif.menu.api.ActionMenuCreation;
import org.exbin.jaguif.menu.api.DirectMenuContribution;
import org.exbin.jaguif.menu.api.SubMenuContribution;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;
import org.exbin.jaguif.menu.api.MenuModuleApi;

/**
 * Menu sequence output.
 */
@ParametersAreNonnullByDefault
public class MenuSequenceOutput implements TreeContributionSequenceOutput {

    protected final JMenu menu;
    protected final ContextRegistration contextRegistration;
    protected final Map<String, ButtonGroup> buttonGroups;
    protected final boolean isPopup;
    protected final Map<SequenceContribution, JMenuItem> menuItems = new HashMap<>();

    public MenuSequenceOutput(JMenu menu, ContextRegistration contextRegistration, Map<String, ButtonGroup> buttonGroups, boolean isPopup) {
        this.menu = menu;
        this.contextRegistration = contextRegistration;
        this.buttonGroups = buttonGroups;
        this.isPopup = isPopup;
    }

    public MenuSequenceOutput(JMenu menu, ContextRegistration contextRegistration, Map<String, ButtonGroup> buttonGroups) {
        this(menu, contextRegistration, buttonGroups, false);
    }

    @Override
    public boolean initItem(SequenceContribution contribution, String definitionId, String subId) {
        if (contribution instanceof SubSequenceContribution) {
            Action action = ((SubMenuContribution) contribution).getAction();
            if (isPopup) {
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

            if (isPopup) {
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
        } else if (contribution instanceof DirectMenuContribution) {
            menuItem = ((DirectMenuContribution) contribution).getMenuItem();
            action = menuItem.getAction();
        } else {
            throw new IllegalStateException();
        }
        if (isPopup && action != null) {
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

        if (isPopup && action != null) {
            ActionMenuCreation menuCreation = (ActionMenuCreation) action.getValue(ActionConsts.ACTION_MENU_CREATION);
            if (menuCreation != null) {
                menuCreation.onCreate(menuItem, definitionId, subId);
            }
        }

        return true;
    }

    @Override
    public void add(SequenceContribution contribution) {
        if (contribution instanceof SubSequenceContribution) {
            JMenu subMenu = ((SubMenuContribution) contribution).getSubMenu().get();
            menu.add(subMenu);
            MenuSequenceOutput.finishMenuItem(subMenu, contextRegistration);
            return;
        }

        if (contribution instanceof DirectMenuContribution) {
            menu.add(((DirectMenuContribution) contribution).getMenuItem());
            return;
        }

        if (contribution instanceof ActionSequenceContribution) {
            JMenuItem menuItem = menuItems.get(contribution);
            menu.add(menuItem);
            MenuSequenceOutput.finishMenuItem(menuItem, contextRegistration);
            return;
        }

        throw new IllegalStateException();
    }

    @Override
    public void addSeparator() {
        menu.addSeparator();
    }

    @Nonnull
    public JMenu getMenu() {
        return menu;
    }

    @Nonnull
    @Override
    public TreeContributionSequenceOutput createSubOutput(SubSequenceContribution subContribution) {
        return new MenuSequenceOutput(((SubMenuContribution) subContribution).getSubMenu().get(), contextRegistration, buttonGroups, isPopup);
    }

    @Override
    public boolean isEmpty() {
        return menu.getItemCount() == 0;
    }

    @Nonnull
    public static JMenuItem createMenuItem(Action action, Map<String, ButtonGroup> buttonGroups) {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        JMenuItem menuItem = menuModule.actionToMenuItem(action, buttonGroups);
        return menuItem;
    }

    private static void finishMenuAction(@Nullable Action action, ContextRegistration contextRegistration) {
        if (action == null) {
            return;
        }

        Object contextChange = action.getValue(ActionConsts.ACTION_CONTEXT_CHANGE);

        if (contextChange instanceof ActionContextChange) {
            contextRegistration.registerContextChange((ActionContextChange) contextChange);
        }
    }

    public static void finishMenuItem(@Nullable JMenuItem menuItem, ContextRegistration contextRegistration) {
        if (menuItem == null) {
            return;
        }

        if (menuItem instanceof JMenu) {
            finishMenu((JMenu) menuItem, contextRegistration);
        } else {
            Action action = menuItem.getAction();
            if (action != null) {
                finishMenuAction(action, contextRegistration);
            }
        }
    }

    private static void finishMenu(JMenu menu, ContextRegistration contextRegistration) {
        int i = 0;
        int itemCount = menu.getItemCount();
        while (i < itemCount) {
            JMenuItem menuItem = menu.getItem(i);
            if (menuItem == null) {
                i++;
                continue;
            }
            Action action = menuItem.getAction();
            if (action != null) {
                finishMenuAction(action, contextRegistration);
            }
            if (menuItem instanceof JMenu) {
                finishMenu((JMenu) menuItem, contextRegistration);
            }
            itemCount = menu.getItemCount();
            if (i < itemCount && menu.getItem(i) == menuItem) {
                // menuItem can be removed when finishing
                i++;
            }
        }
    }
}
