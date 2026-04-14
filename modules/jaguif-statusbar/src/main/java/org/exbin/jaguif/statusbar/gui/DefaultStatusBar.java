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
package org.exbin.jaguif.statusbar.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.exbin.jaguif.statusbar.api.StatusBar;

/**
 * Status bar component.
 */
@ParametersAreNonnullByDefault
public class DefaultStatusBar implements StatusBar {

    protected final List<JComponent> statusBarComponents = new ArrayList<>();
    protected JComponent statusBarComponent;

    @Nonnull
    @Override
    public JComponent getComponent() {
        if (statusBarComponent == null) {
            statusBarComponent = new JPanel();

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(statusBarComponent);
            statusBarComponent.setLayout(layout);
            GroupLayout.SequentialGroup horizontalGroup = layout.createSequentialGroup();
            horizontalGroup.addContainerGap(0, Short.MAX_VALUE);
            GroupLayout.ParallelGroup verticalGroup = layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
            for (JComponent component : statusBarComponents) {
                if (component instanceof JLabel) {
                    component.setBorder(javax.swing.BorderFactory.createEtchedBorder());
                }
                // horizontalGroup.addGap(0,0,0);
                horizontalGroup.addComponent(component, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
                verticalGroup.addComponent(component, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
            }
            layout.setHorizontalGroup(horizontalGroup);
            layout.setVerticalGroup(verticalGroup);
        }

        return statusBarComponent;
    }

    @Override
    public void addItem(JComponent component) {
        statusBarComponents.add(component);
    }

    @Override
    public void addSeparator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getItemsCount() {
        return statusBarComponents.size();
    }
}
