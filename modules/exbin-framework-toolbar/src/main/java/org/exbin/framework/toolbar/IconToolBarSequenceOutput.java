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

import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import org.exbin.framework.toolbar.api.ActionToolBarContribution;
import org.exbin.framework.contribution.api.ContributionSequenceOutput;
import org.exbin.framework.contribution.api.ItemSequenceContribution;
import org.exbin.framework.action.api.ActionContextRegistration;

/**
 * Icon toolbar sequence output.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class IconToolBarSequenceOutput implements ContributionSequenceOutput {

    protected final JToolBar toolBar;
    protected final ActionContextRegistration actionContextRegistration;

    public IconToolBarSequenceOutput(JToolBar menuBar, ActionContextRegistration actionContextRegistration) {
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
        JComponent component = ((ActionToolBarContribution) itemContribution).getComponent();
        if (component instanceof AbstractButton) {
            ((AbstractButton) component).setText("");
        }
        toolBar.add(component);
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
}
