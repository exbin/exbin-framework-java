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
package org.exbin.jaguif.toolbar;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import org.exbin.jaguif.toolbar.api.ActionToolBarContribution;
import org.exbin.jaguif.contribution.api.ContributionSequenceOutput;
import org.exbin.jaguif.contribution.api.ItemSequenceContribution;
import org.exbin.jaguif.action.api.ActionContextRegistration;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.toolbar.api.ToolBarComponent;

/**
 * Icon toolbar sequence output.
 */
@ParametersAreNonnullByDefault
public class IconToolBarSequenceOutput implements ContributionSequenceOutput {

    protected final JToolBar toolBar;
    protected final ActionContextRegistration actionContextRegistration;
    protected final Map<SequenceContribution, ToolBarComponent> toolBarItems = new HashMap<>();

    public IconToolBarSequenceOutput(JToolBar menuBar, ActionContextRegistration actionContextRegistration) {
        this.toolBar = menuBar;
        this.actionContextRegistration = actionContextRegistration;
    }

    @Override
    public boolean initItem(ItemSequenceContribution itemContribution) {
        Action action = ((ActionToolBarContribution) itemContribution).createAction();
        toolBarItems.put(itemContribution, new DefaultToolBarComponent(ToolBarSequenceOutput.createToolBarComponent(action), action));
        return true;
    }

    @Override
    public void add(ItemSequenceContribution itemContribution) {
        ToolBarComponent component = toolBarItems.get(itemContribution);
        if (component instanceof AbstractButton) {
            ((AbstractButton) component).setText("");
        }
        toolBar.add(component.getComponent());
        ToolBarSequenceOutput.finishToolBarAction(component.getAction(), actionContextRegistration);
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
