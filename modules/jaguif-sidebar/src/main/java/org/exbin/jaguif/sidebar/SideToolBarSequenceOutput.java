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
package org.exbin.jaguif.sidebar;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.sidebar.api.ActionSideBarContribution;
import org.exbin.jaguif.contribution.api.ContributionSequenceOutput;
import org.exbin.jaguif.contribution.api.ItemSequenceContribution;
import org.exbin.jaguif.action.api.ActionType;
import org.exbin.jaguif.context.api.ContextChange;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.sidebar.api.ComponentSideBarContribution;
import org.exbin.jaguif.sidebar.api.SideBar;
import org.exbin.jaguif.sidebar.api.SideBarComponent;

/**
 * Sidebar toolbar sequence output.
 */
@ParametersAreNonnullByDefault
public class SideToolBarSequenceOutput implements ContributionSequenceOutput {

    protected final SideBar sideBar;
    protected final ContextRegistration contextRegistration;
    protected final Map<SequenceContribution, SideToolBarComponent> sideBarItems = new HashMap<>();

    public SideToolBarSequenceOutput(SideBar sideBar, ContextRegistration contextRegistration) {
        this.sideBar = sideBar;
        this.contextRegistration = contextRegistration;
    }

    @Override
    public boolean initItem(ItemSequenceContribution itemContribution) {
        if (itemContribution instanceof ActionSideBarContribution) {
            Action action = ((ActionSideBarContribution) itemContribution).createAction();
            sideBarItems.put(itemContribution, new SideToolBarComponent(SideToolBarSequenceOutput.createSideBarComponent(action), action));
            return true;
        } else if (itemContribution instanceof ComponentSideBarContribution) {
            return true;
        }

        throw new IllegalStateException("Unsupported contribution type");
    }

    @Override
    public void add(ItemSequenceContribution itemContribution) {
        if (itemContribution instanceof ActionSideBarContribution) {
            SideToolBarComponent component = sideBarItems.get(itemContribution);
            sideBar.getToolBar().add(component.getComponent());
            SideToolBarSequenceOutput.finishSideBarAction(component.getAction(), contextRegistration);
        } else if (itemContribution instanceof ComponentSideBarContribution) {
            SideBarComponent sideBarComponent = ((ComponentSideBarContribution) itemContribution).createComponent();
            Action buttonAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    sideBar.switchComponent(sideBarComponent);
                }
            };
            JButton button = new JButton(buttonAction);
            String name = (String) sideBarComponent.getValue(SideBarComponent.KEY_NAME);
            if (name != null) {
                button.setText(name);
            }
            String toolTip = (String) sideBarComponent.getValue(SideBarComponent.KEY_TOOLTIP);
            if (toolTip != null) {
                button.setToolTipText(toolTip);
            }
            Icon icon = (Icon) sideBarComponent.getValue(SideBarComponent.KEY_ICON);
            if (icon != null) {
                button.setIcon(icon);
            }
            ContextChange contextChange = (ContextChange) sideBarComponent.getValue(SideBarComponent.KEY_CONTEXT_CHANGE);
            if (contextChange != null) {
                buttonAction.putValue(ActionConsts.ACTION_CONTEXT_CHANGE, (ActionContextChange) contextChange::register);
            }
            button.setFocusable(false);
            sideBar.getToolBar().add(button);
            SideToolBarSequenceOutput.finishSideBarAction(buttonAction, contextRegistration);
        }
    }

    @Override
    public void addSeparator() {
        sideBar.getToolBar().addSeparator();
    }

    @Override
    public boolean isEmpty() {
        return sideBar.getToolBar().getComponentCount() == 0;
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

    protected static void finishSideBarAction(Action action, ContextRegistration contextRegistration) {
        if (action == null) {
            return;
        }

        Object contextChange = action.getValue(ActionConsts.ACTION_CONTEXT_CHANGE);

        if (contextChange instanceof ActionContextChange) {
            contextRegistration.registerContextChange((ActionContextChange) contextChange);
        }
    }

    @ParametersAreNonnullByDefault
    @Immutable
    public static class SideToolBarComponent {

        private final JComponent component;
        private final Action action;

        public SideToolBarComponent(JComponent component, Action action) {
            this.component = component;
            this.action = action;
        }

        @Nonnull
        public JComponent getComponent() {
            return component;
        }

        @Nonnull
        public Action getAction() {
            return action;
        }
    }
}
