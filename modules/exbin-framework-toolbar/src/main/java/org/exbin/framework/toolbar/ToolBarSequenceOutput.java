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
import org.exbin.auxiliary.dropdownbutton.DropDownButton;
import org.exbin.auxiliary.dropdownbutton.DropDownButtonVariant;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.toolbar.api.ActionToolBarContribution;
import org.exbin.framework.contribution.api.ContributionSequenceOutput;
import org.exbin.framework.contribution.api.ItemSequenceContribution;
import org.exbin.framework.action.api.ActionType;
import org.exbin.framework.action.api.ActionContextRegistration;

/**
 * Toolbar sequence output.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ToolBarSequenceOutput implements ContributionSequenceOutput {

    protected final JToolBar toolBar;
    protected final ActionContextRegistration actionContextRegistration;

    public ToolBarSequenceOutput(JToolBar menuBar, ActionContextRegistration actionContextRegistration) {
        this.toolBar = menuBar;
        this.actionContextRegistration = actionContextRegistration;
    }

    @Override
    public boolean initItem(ItemSequenceContribution itemContribution) {
        Action action = ((ActionToolBarContribution) itemContribution).getAction();
        ((ActionToolBarContribution) itemContribution).setComponent(ToolBarSequenceOutput.createToolBarComponent(action));
        return true;
    }

    @Override
    public void add(ItemSequenceContribution itemContribution) {
        toolBar.add(((ActionToolBarContribution) itemContribution).getComponent());
        ToolBarSequenceOutput.finishToolBarAction(((ActionToolBarContribution) itemContribution).getAction(), actionContextRegistration);
    }

    @Override
    public void addSeparator() {
        toolBar.addSeparator();
    }

    @Override
    public boolean isEmpty() {
        return toolBar.getComponentCount() == 0;
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

    protected static void finishToolBarAction(Action action, ActionContextRegistration actionContextRegistration) {
        if (action == null) {
            return;
        }

        actionContextRegistration.registerActionContext(action);
    }
}
