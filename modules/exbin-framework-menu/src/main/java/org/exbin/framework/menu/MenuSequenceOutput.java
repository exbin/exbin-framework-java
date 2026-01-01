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
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SubSequenceContribution;
import org.exbin.framework.contribution.api.TreeContributionSequenceOutput;
import org.exbin.framework.menu.api.ActionMenuContribution;
import org.exbin.framework.menu.api.ActionMenuCreation;
import org.exbin.framework.menu.api.DirectMenuContribution;
import org.exbin.framework.menu.api.SubMenuContribution;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.action.api.ActionContextRegistration;

/**
 * Menu sequence output.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MenuSequenceOutput implements TreeContributionSequenceOutput {

    protected final JMenu menu;
    protected final ActionContextRegistration actionContextRegistration;
    protected final Map<String, ButtonGroup> buttonGroups;
    protected final boolean isPopup;

    public MenuSequenceOutput(JMenu menu, ActionContextRegistration actionContextRegistration, Map<String, ButtonGroup> buttonGroups, boolean isPopup) {
        this.menu = menu;
        this.actionContextRegistration = actionContextRegistration;
        this.buttonGroups = buttonGroups;
        this.isPopup = isPopup;
    }

    public MenuSequenceOutput(JMenu menu, ActionContextRegistration actionContextRegistration, Map<String, ButtonGroup> buttonGroups) {
        this(menu, actionContextRegistration, buttonGroups, false);
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

            JMenu subMenu = UiUtils.createMenu();
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
        if (contribution instanceof ActionMenuContribution) {
            menuItem = null;
            action = ((ActionMenuContribution) contribution).getAction();
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

        if (contribution instanceof ActionMenuContribution) {
            menuItem = MenuSequenceOutput.createMenuItem(action, buttonGroups);
            ((ActionMenuContribution) contribution).setMenuItem(menuItem);
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
            MenuSequenceOutput.finishMenuItem(subMenu, actionContextRegistration);
            return;
        }

        if (contribution instanceof DirectMenuContribution) {
            menu.add(((DirectMenuContribution) contribution).getMenuItem());
            return;
        }

        if (contribution instanceof ActionMenuContribution) {
            JMenuItem menuItem = ((ActionMenuContribution) contribution).getMenuItem();
            menu.add(menuItem);
            MenuSequenceOutput.finishMenuItem(menuItem, actionContextRegistration);
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
        return new MenuSequenceOutput(((SubMenuContribution) subContribution).getSubMenu().get(), actionContextRegistration, buttonGroups, isPopup);
    }

    @Override
    public boolean isEmpty() {
        return menu.getItemCount() == 0;
    }

    @Nonnull
    public static JMenuItem createMenuItem(Action action, Map<String, ButtonGroup> buttonGroups) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        JMenuItem menuItem = actionModule.actionToMenuItem(action, buttonGroups);
        return menuItem;
    }

    private static void finishMenuAction(@Nullable Action action, ActionContextRegistration actionContextRegistration) {
        if (action == null) {
            return;
        }

        actionContextRegistration.registerActionContext(action);
    }

    public static void finishMenuItem(@Nullable JMenuItem menuItem, ActionContextRegistration actionContextRegistration) {
        if (menuItem == null) {
            return;
        }

        if (menuItem instanceof JMenu) {
            finishMenu((JMenu) menuItem, actionContextRegistration);
        } else {
            Action action = menuItem.getAction();
            if (action != null) {
                finishMenuAction(action, actionContextRegistration);
            }
        }
    }

    private static void finishMenu(JMenu menu, ActionContextRegistration actionContextRegistration) {
        int i = 0;
        while (i < menu.getItemCount()) {
            JMenuItem menuItem = menu.getItem(i);
            i++;
            if (menuItem == null) {
                continue;
            }
            Action action = menuItem.getAction();
            if (action != null) {
                finishMenuAction(action, actionContextRegistration);
            }
            if (menuItem instanceof JMenu) {
                finishMenu((JMenu) menuItem, actionContextRegistration);
            }
        }
    }
}
