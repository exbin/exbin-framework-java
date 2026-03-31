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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
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
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionContextChange;
import org.exbin.framework.sidebar.api.ActionSideBarContribution;
import org.exbin.framework.contribution.api.ContributionSequenceOutput;
import org.exbin.framework.contribution.api.ItemSequenceContribution;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.ActionContextRegistration;
import org.exbin.framework.context.api.ContextChange;
import org.exbin.framework.context.api.ContextChangeRegistration;
import org.exbin.framework.sidebar.api.ComponentSideBarContribution;
import org.exbin.framework.sidebar.api.SideBar;
import org.exbin.framework.sidebar.api.SideBarComponent;

/**
 * Sidebar toolbar sequence output.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SideToolBarSequenceOutput implements ContributionSequenceOutput {

    protected final SideBar sideBar;
    protected final ActionContextRegistration actionContextRegistration;

    public SideToolBarSequenceOutput(SideBar sideBar, ActionContextRegistration actionContextRegistration) {
        this.sideBar = sideBar;
        this.actionContextRegistration = actionContextRegistration;
    }

    @Override
    public boolean initItem(ItemSequenceContribution itemContribution) {
        if (itemContribution instanceof ActionSideBarContribution) {
            Action action = ((ActionSideBarContribution) itemContribution).getAction();
            ((ActionSideBarContribution) itemContribution).setComponent(SideToolBarSequenceOutput.createSideBarComponent(action));
            return true;
        } else if (itemContribution instanceof ComponentSideBarContribution) {
            return true;
        }
        
        throw new IllegalStateException("Unsupported contribution type");
    }

    @Override
    public void add(ItemSequenceContribution itemContribution) {
        if (itemContribution instanceof ActionSideBarContribution) {
            sideBar.getToolBar().add(((ActionSideBarContribution) itemContribution).getComponent());
            SideToolBarSequenceOutput.finishSideBarAction(((ActionSideBarContribution) itemContribution).getAction(), actionContextRegistration);
        } else if (itemContribution instanceof ComponentSideBarContribution) {
            SideBarComponent sideBarComponent = ((ComponentSideBarContribution) itemContribution).getComponent();
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
            SideToolBarSequenceOutput.finishSideBarAction(buttonAction, actionContextRegistration);
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

    protected static void finishSideBarAction(Action action, ActionContextRegistration actionContextRegistration) {
        if (action == null) {
            return;
        }

        actionContextRegistration.registerActionContext(action);
    }
}
