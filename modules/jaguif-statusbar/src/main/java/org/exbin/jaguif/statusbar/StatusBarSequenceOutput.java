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
package org.exbin.jaguif.statusbar;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.exbin.jaguif.contribution.api.ContributionSequenceOutput;
import org.exbin.jaguif.contribution.api.ItemSequenceContribution;
import org.exbin.jaguif.context.api.ContextRegistration;
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

    public StatusBarSequenceOutput(StatusBar statusBar, ContextRegistration contextRegistration) {
        this.statusBar = statusBar;
        this.contextRegistration = contextRegistration;
    }

    @Override
    public boolean initItem(ItemSequenceContribution itemContribution) {
        // StatusBarComponent statusBarComponent = ((ComponentStatusBarContribution) itemContribution).createComponent();
        // TODO ((ComponentStatusBarContribution) itemContribution).setComponent(StatusBarSequenceOutput.createStatusBarComponent(statusBarComponent));
        return true;
    }

    @Override
    public void add(ItemSequenceContribution itemContribution) {
        StatusBarComponent statusBarComponent = ((ComponentStatusBarContribution) itemContribution).createComponent();
        statusBar.addItem(statusBarComponent.getComponent());
        StatusBarSequenceOutput.finishStatusBarItem(statusBarComponent, contextRegistration);
    }

    @Override
    public void addSeparator() {
        // TODO statusBar.addSeparator();
    }

    @Override
    public boolean isEmpty() {
        return statusBar.getItemsCount() == 0;
    }

    @Nonnull
    protected static JComponent createStatusBarComponent(StatusBarComponent statusBarComponent) {
        JComponent statusBarItem = createDefaultStatusBarItem(statusBarComponent);
        return statusBarItem;
    }

    @Nonnull
    protected static JComponent createDefaultStatusBarItem(StatusBarComponent statusBarComponent) {
        /* JButton button = new JButton(statusBarComponent);
        button.setFocusable(false);
        button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        return button; */
        return new JLabel("TEST");
    }

    protected static void finishStatusBarItem(StatusBarComponent statusBarComponent, ContextRegistration contextRegistration) {
        if (statusBarComponent == null) {
            return;
        }

        // TODO contextRegistration.registerActionContext(statusBarComponent);
    }
}
