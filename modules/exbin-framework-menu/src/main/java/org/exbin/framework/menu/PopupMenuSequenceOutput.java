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
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.exbin.framework.action.api.ActionConsts;
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
 * Popup menu sequence output.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class PopupMenuSequenceOutput implements TreeContributionSequenceOutput {

    protected final JPopupMenu menu;
    protected final ActionContextRegistration actionContextRegistration;
    protected final Map<String, ButtonGroup> buttonGroups;

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

            JMenu subMenu = UiUtils.createMenu();
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
        if (contribution instanceof ActionMenuContribution) {
            menuItem = null;
            action = ((ActionMenuContribution) contribution).getAction();
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

        if (contribution instanceof ActionMenuContribution) {
            menuItem = MenuSequenceOutput.createMenuItem(action, buttonGroups);
            ((ActionMenuContribution) contribution).setMenuItem(menuItem);
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

        JMenuItem menuItem = ((ActionMenuContribution) contribution).getMenuItem();
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
