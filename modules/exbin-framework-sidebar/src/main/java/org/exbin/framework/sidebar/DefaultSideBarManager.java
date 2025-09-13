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
package org.exbin.framework.sidebar;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.contribution.ContributionDefinition;
import org.exbin.framework.contribution.ContributionManager;
import org.exbin.framework.sidebar.api.ActionSideBarContribution;
import org.exbin.framework.action.api.ActionContextService;
import org.exbin.framework.action.api.ActionType;
import static org.exbin.framework.action.api.ActionType.CHECK;
import static org.exbin.framework.action.api.ActionType.CYCLE;
import static org.exbin.framework.action.api.ActionType.RADIO;
import org.exbin.framework.contribution.api.ContributionSequenceOutput;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.ItemSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.sidebar.api.SideBarManager;

/**
 * Sidebar manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultSideBarManager extends ContributionManager implements SideBarManager {

    public DefaultSideBarManager() {
    }

    @Override
    public void buildSideBar(JToolBar targetSideBar, String sideBarId, ActionContextService activationUpdateService) {
        buildSequence(new ToolBarWrapper(targetSideBar, activationUpdateService), sideBarId);
        activationUpdateService.requestUpdate();
    }

    @Override
    public void registerSideBar(String sideBarId, String pluginId) {
        registerDefinition(sideBarId, pluginId);
    }

    @Nonnull
    @Override
    public ActionSideBarContribution registerSideBarItem(String sideBarId, String pluginId, Action action) {
        ContributionDefinition definition = definitions.get(sideBarId);
        if (definition == null) {
            throw new IllegalStateException("Definition with Id " + sideBarId + " doesn't exist");
        }

        ActionSideBarContribution sideBarContribution = new ActionSideBarContribution(action);
        definition.getContributions().add(sideBarContribution);
        return sideBarContribution;
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerSideBarGroup(String sideBarId, String pluginId, String groupId) {
        return registerContributionGroup(sideBarId, pluginId, groupId);
    }

    @Override
    public void registerSideBarRule(SequenceContribution contribution, SequenceContributionRule rule) {
        registerContributionRule(contribution, rule);
    }

    @Nonnull
    public List<Action> getAllManagedActions() {
        List<Action> actions = new ArrayList<>();
        for (ContributionDefinition definition : definitions.values()) {
            for (SequenceContribution contribution : definition.getContributions()) {
                if (contribution instanceof ActionSideBarContribution) {
                    actions.add(((ActionSideBarContribution) contribution).getAction());
                }
            }
        }
        return actions;
    }

    @Nonnull
    protected static JComponent createSideBarComponent(Action action) {
        ActionType actionType = (ActionType) action.getValue(ActionConsts.ACTION_TYPE);
        JComponent sideBarItem;
        if (actionType != null) {
            switch (actionType) {
                case CHECK: {
                    if (action.getValue(Action.SMALL_ICON) != null) {
                        JToggleButton newItem = new JToggleButton(action);
                        newItem.setFocusable(false);
                        newItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                        newItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                        sideBarItem = newItem;
                    } else {
                        JCheckBox newItem = new JCheckBox(action);
                        newItem.setFocusable(false);
                        newItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                        newItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                        sideBarItem = newItem;
                    }

                    break;
                }
                case RADIO: {
                    JRadioButton newItem = new JRadioButton(action);
                    newItem.setFocusable(false);
                    newItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                    newItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                    sideBarItem = newItem;
                    break;
                }
                case CYCLE: {
                    JPopupMenu popupMenu = (JPopupMenu) action.getValue(ActionConsts.CYCLE_POPUP_MENU);
//                    DropDownButton dropDown = new DropDownButton(action, popupMenu);
//                    dropDown.setActionTooltip((String) action.getValue(Action.SHORT_DESCRIPTION));
//                    action.addPropertyChangeListener((PropertyChangeEvent evt) -> {
//                        dropDown.setActionText((String) action.getValue(Action.NAME));
//                    });
//                    // createDefaultSideBarItem(action);
//                    sideBarItem = dropDown;
                    sideBarItem = null;
                    break;
                }
                default: {
                    sideBarItem = createDefaultSideBarItem(action);
                }
            }
        } else {
            sideBarItem = createDefaultSideBarItem(action);
        }
        return sideBarItem;
    }

    protected static void addSideBarSeparator(JToolBar targetSideBar) {
        targetSideBar.addSeparator();
    }

    @Nonnull
    protected static JComponent createDefaultSideBarItem(Action action) {
        JButton newItem = new JButton(action);
        newItem.setFocusable(false);
        newItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        return newItem;
    }

    protected static void finishSideBarAction(Action action, ActionContextService activationUpdateService) {
        if (action == null) {
            return;
        }

        activationUpdateService.requestUpdate(action);
    }

    @ParametersAreNonnullByDefault
    protected static class ToolBarWrapper implements ContributionSequenceOutput {

        private final JToolBar toolBar;
        private final ActionContextService activationUpdateService;

        public ToolBarWrapper(JToolBar menuBar, ActionContextService activationUpdateService) {
            this.toolBar = menuBar;
            this.activationUpdateService = activationUpdateService;
        }

        @Override
        public void initItem(ItemSequenceContribution itemContribution) {
            Action action = ((ActionSideBarContribution) itemContribution).getAction();
            ((ActionSideBarContribution) itemContribution).setComponent(DefaultSideBarManager.createSideBarComponent(action));
        }

        @Override
        public void add(ItemSequenceContribution itemContribution) {
            toolBar.add(((ActionSideBarContribution) itemContribution).getComponent());
            DefaultSideBarManager.finishSideBarAction(((ActionSideBarContribution) itemContribution).getAction(), activationUpdateService);
        }

        @Override
        public void addSeparator() {
            toolBar.addSeparator();
        }

        @Override
        public boolean isEmpty() {
            return toolBar.getComponentCount() == 0;
        }
    }
}
