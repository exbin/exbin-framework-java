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
package org.exbin.jaguif.statusbar;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.contribution.api.ContributionSequenceOutput;
import org.exbin.jaguif.contribution.api.ItemSequenceContribution;
import org.exbin.jaguif.context.api.ContextRegistration;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.statusbar.api.ComponentStatusBarContribution;
import org.exbin.jaguif.statusbar.api.StatusBar;
import org.exbin.jaguif.statusbar.api.StatusBarComponent;

/**
 * Status bar sequence output.
 */
@ParametersAreNonnullByDefault
public class StatusBarSequenceOutput implements ContributionSequenceOutput {

    protected final StatusBar statusBar;
    protected final ContextRegistration contextRegistration;
    protected final Map<SequenceContribution, StatusBarComponent> statusBarItems = new HashMap<>();

    public StatusBarSequenceOutput(StatusBar statusBar, ContextRegistration contextRegistration) {
        this.statusBar = statusBar;
        this.contextRegistration = contextRegistration;
    }

    @Override
    public boolean initItem(ItemSequenceContribution itemContribution) {
        if (itemContribution instanceof ComponentStatusBarContribution) {
            StatusBarComponent statusBarComponent = ((ComponentStatusBarContribution) itemContribution).createComponent();
            statusBarItems.put(itemContribution, statusBarComponent);
        }
        return true;
    }

    @Override
    public void add(ItemSequenceContribution itemContribution) {
//        if (itemContribution instanceof ComponentStatusBarContribution) {
//            
//        }

        StatusBarComponent statusBarComponent = statusBarItems.get(itemContribution);
        statusBar.addItem(statusBarComponent.getComponent());
        StatusBarSequenceOutput.finishStatusBarItem(statusBarComponent, contextRegistration);
    }

    @Override
    public void addSeparator() {
        statusBar.addSeparator();
    }

    @Override
    public boolean isEmpty() {
        return statusBar.getItemsCount() == 0;
    }

    protected static void finishStatusBarItem(StatusBarComponent statusBarComponent, ContextRegistration contextRegistration) {
        contextRegistration.registerItemContext(statusBarComponent);
    }
}
