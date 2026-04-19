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
package org.exbin.jaguif.toolbar;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;
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
import org.exbin.jaguif.action.api.ActionConsts;
import org.exbin.jaguif.action.api.ActionContextChange;
import org.exbin.jaguif.contribution.api.ContributionSequenceOutput;
import org.exbin.jaguif.contribution.api.ItemSequenceContribution;
import org.exbin.jaguif.action.api.ActionType;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.contribution.api.ActionSequenceContribution;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.toolbar.api.ToolBarComponent;

/**
 * Toolbar sequence output.
 */
@ParametersAreNonnullByDefault
public class ToolBarSequenceOutput implements ContributionSequenceOutput {

    protected final JToolBar toolBar;
    protected final ContextRegistration contextRegistration;
    protected final Map<SequenceContribution, ToolBarComponent> toolBarItems = new HashMap<>();

    public ToolBarSequenceOutput(JToolBar toolBar, ContextRegistration contextRegistration) {
        this.toolBar = toolBar;
        this.contextRegistration = contextRegistration;
    }

    @Override
    public boolean initItem(ItemSequenceContribution itemContribution) {
        Action action = ((ActionSequenceContribution) itemContribution).createAction();
        toolBarItems.put(itemContribution, new DefaultToolBarComponent(ToolBarSequenceOutput.createToolBarComponent(action), action));
        return true;
    }

    @Override
    public void add(ItemSequenceContribution itemContribution) {
        ToolBarComponent component = toolBarItems.get(itemContribution);
        toolBar.add(component.getComponent());
        ToolBarSequenceOutput.finishToolBarAction(component.getAction(), contextRegistration);
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

    protected static void finishToolBarAction(Action action, ContextRegistration contextRegistration) {
        if (action == null) {
            return;
        }
        
        Object contextChange = action.getValue(ActionConsts.ACTION_CONTEXT_CHANGE);
        
        if (contextChange instanceof ActionContextChange) {
            contextRegistration.registerContextChange((ActionContextChange) contextChange);
        }
    }
}
