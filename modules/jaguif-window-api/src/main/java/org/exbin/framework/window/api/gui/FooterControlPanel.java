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
package org.exbin.framework.window.api.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.exbin.framework.App;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Footer control panel.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FooterControlPanel extends JPanel {

    protected final java.util.ResourceBundle resourceBundle;

    protected GroupLayout.ParallelGroup verticalGroup;
    protected List<JComponent> leftComponents = new ArrayList<>();
    protected List<JComponent> rightComponents = new ArrayList<>();

    public FooterControlPanel() {
        this(App.getModule(LanguageModuleApi.class).getBundle(FooterControlPanel.class));
    }

    public FooterControlPanel(java.util.ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        initComponents();
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    private void initComponents() {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        rebuildHorizontalGroup();

        verticalGroup = layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE);
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(verticalGroup)
                                .addContainerGap())
        );
    }

    public void addButton(JButton button, ButtonPosition buttonPosition) {
        switch (buttonPosition) {
            case FIRST_LEFT:
                leftComponents.add(0, button);
                break;
            case LAST_LEFT:
                leftComponents.add(button);
                break;
            case FIRST_RIGHT:
                rightComponents.add(0, button);
                break;
            case LAST_RIGHT:
                rightComponents.add(button);
                break;
        }

        rebuildHorizontalGroup();
        verticalGroup.addComponent(button);
    }

    protected void rebuildHorizontalGroup() {
        javax.swing.GroupLayout layout = (javax.swing.GroupLayout) getLayout();
        GroupLayout.SequentialGroup horizontalGroup = layout.createSequentialGroup();
        if (!leftComponents.isEmpty()) {
            boolean first = true;
            for (JComponent component : leftComponents) {
                if (first) {
                    horizontalGroup.addContainerGap();
                    first = false;
                } else {
                    horizontalGroup.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
                }
                horizontalGroup.addComponent(component);
            }
        }
        horizontalGroup.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        if (!rightComponents.isEmpty()) {
            boolean first = true;
            for (JComponent component : rightComponents) {
                if (first) {
                    first = false;
                } else {
                    horizontalGroup.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
                }
                horizontalGroup.addComponent(component);
            }
            horizontalGroup.addContainerGap();
        }
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, horizontalGroup)
        );
    }

    public enum ButtonPosition {
        FIRST_LEFT,
        LAST_LEFT,
        FIRST_RIGHT,
        LAST_RIGHT
    }
}
