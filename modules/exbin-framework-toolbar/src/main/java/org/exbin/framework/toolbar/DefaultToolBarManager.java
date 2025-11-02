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
package org.exbin.framework.toolbar;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.exbin.auxiliary.dropdownbutton.DropDownButton;
import org.exbin.auxiliary.dropdownbutton.DropDownButtonVariant;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.toolbar.api.ActionToolBarContribution;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.contribution.ContributionDefinition;
import org.exbin.framework.contribution.ContributionsManager;
import org.exbin.framework.contribution.api.ContributionSequenceOutput;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.ItemSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.toolbar.api.ToolBarManager;
import org.exbin.framework.action.api.ActionContextManager;

/**
 * Toolbar manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DefaultToolBarManager extends ContributionsManager implements ToolBarManager {

    public DefaultToolBarManager() {
    }

    @Override
    public void buildToolBar(JToolBar targetToolBar, String toolBarId, ActionContextManager actionContextManager) {
        ContributionDefinition contributionDef = definitions.get(toolBarId);
        buildSequence(new ToolBarWrapper(targetToolBar, actionContextManager), contributionDef);
    }

    @Override
    public void buildIconToolBar(JToolBar targetToolBar, String toolBarId, ActionContextManager actionContextManager) {
        ContributionDefinition contributionDef = definitions.get(toolBarId);
        buildSequence(new IconToolBarWrapper(targetToolBar, actionContextManager), contributionDef);
    }

    @Override
    public void registerToolBar(String toolBarId, String moduleId) {
        registerDefinition(toolBarId, moduleId);
    }

    @Nonnull
    @Override
    public ActionToolBarContribution registerToolBarItem(String toolBarId, String moduleId, Action action) {
        ContributionDefinition definition = definitions.get(toolBarId);
        if (definition == null) {
            throw new IllegalStateException("Definition with Id " + toolBarId + " doesn't exist");
        }

        ActionToolBarContribution toolBarContribution = new ActionToolBarContribution(action);
        definition.addContribution(toolBarContribution);
        return toolBarContribution;
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerToolBarGroup(String toolBarId, String moduleId, String groupId) {
        return registerContributionGroup(toolBarId, moduleId, groupId);
    }

    @Override
    public void registerToolBarRule(SequenceContribution contribution, SequenceContributionRule rule) {
        registerContributionRule(contribution, rule);
    }

    @Nonnull
    @Override
    public List<Action> getAllManagedActions() {
        List<Action> actions = new ArrayList<>();
        for (ContributionDefinition definition : definitions.values()) {
            for (SequenceContribution contribution : definition.getContributions()) {
                if (contribution instanceof ActionToolBarContribution) {
                    actions.add(((ActionToolBarContribution) contribution).getAction());
                }
            }
        }
        return actions;
    }

    @Nonnull
    protected static JComponent createToolBarComponent(Action action) {
        ActionType actionType = (ActionType) action.getValue(ActionConsts.ACTION_TYPE);
        JComponent toolBarItem;
        if (actionType != null) {
            switch (actionType) {
                case CHECK: {
                    if (action.getValue(Action.SMALL_ICON) != null) {
                        JToggleButton newItem = new JToggleButton(action);
                        newItem.setFocusable(false);
                        newItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                        newItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                        toolBarItem = newItem;
                    } else {
                        JCheckBox newItem = new JCheckBox(action);
                        newItem.setFocusable(false);
                        newItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                        newItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                        toolBarItem = newItem;
                    }

                    break;
                }
                case RADIO: {
                    JRadioButton newItem = new JRadioButton(action);
                    newItem.setFocusable(false);
                    newItem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                    newItem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
                    toolBarItem = newItem;
                    break;
                }
                case CYCLE: {
                    JPopupMenu popupMenu = (JPopupMenu) action.getValue(ActionConsts.CYCLE_POPUP_MENU);
                    DropDownButton dropDown = new DropDownButton(DropDownButtonVariant.TOOL, action, popupMenu);
                    dropDown.setActionTooltip((String) action.getValue(Action.SHORT_DESCRIPTION));
                    action.addPropertyChangeListener((PropertyChangeEvent evt) -> {
                        dropDown.setActionText((String) action.getValue(Action.NAME));
                    });
                    // createDefaultToolBarItem(action);
                    toolBarItem = dropDown;
                    break;
                }
                default: {
                    toolBarItem = createDefaultToolBarItem(action);
                }
            }
        } else {
            toolBarItem = createDefaultToolBarItem(action);
        }
        return toolBarItem;
    }

    @Nonnull
    protected static JComponent createDefaultToolBarItem(Action action) {
        JButton button = new JButton(action);
        button.setFocusable(false);
        button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        return button;
    }

    protected static void finishToolBarAction(Action action, ActionContextManager actionContextManager) {
        if (action == null) {
            return;
        }

        actionContextManager.registerActionContext(action);
    }

    @ParametersAreNonnullByDefault
    private static class ToolBarWrapper implements ContributionSequenceOutput {

        private final JToolBar toolBar;
        private final ActionContextManager actionContextManager;

        public ToolBarWrapper(JToolBar menuBar, ActionContextManager actionContextManager) {
            this.toolBar = menuBar;
            this.actionContextManager = actionContextManager;
        }

        @Override
        public boolean initItem(ItemSequenceContribution itemContribution) {
            Action action = ((ActionToolBarContribution) itemContribution).getAction();
            ((ActionToolBarContribution) itemContribution).setComponent(DefaultToolBarManager.createToolBarComponent(action));
            return true;
        }

        @Override
        public void add(ItemSequenceContribution itemContribution) {
            toolBar.add(((ActionToolBarContribution) itemContribution).getComponent());
            DefaultToolBarManager.finishToolBarAction(((ActionToolBarContribution) itemContribution).getAction(), actionContextManager);
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

    @ParametersAreNonnullByDefault
    private static class IconToolBarWrapper implements ContributionSequenceOutput {

        private final JToolBar toolBar;
        private final ActionContextManager actionContextManager;

        public IconToolBarWrapper(JToolBar menuBar, ActionContextManager actionContextManager) {
            this.toolBar = menuBar;
            this.actionContextManager = actionContextManager;
        }

        @Override
        public boolean initItem(ItemSequenceContribution itemContribution) {
            Action action = ((ActionToolBarContribution) itemContribution).getAction();
            ((ActionToolBarContribution) itemContribution).setComponent(DefaultToolBarManager.createToolBarComponent(action));
            return true;
        }

        @Override
        public void add(ItemSequenceContribution itemContribution) {
            JComponent component = ((ActionToolBarContribution) itemContribution).getComponent();
            if (component instanceof AbstractButton) {
                ((AbstractButton) component).setText("");
            }
            toolBar.add(component);
            DefaultToolBarManager.finishToolBarAction(((ActionToolBarContribution) itemContribution).getAction(), actionContextManager);
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
