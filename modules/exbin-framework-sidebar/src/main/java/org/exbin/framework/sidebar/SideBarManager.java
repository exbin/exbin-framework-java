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

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.exbin.framework.contribution.ContributionDefinition;
import org.exbin.framework.sidebar.api.ActionSideBarContribution;
import org.exbin.framework.contribution.ContributionManager;
import org.exbin.framework.contribution.api.GroupSequenceContribution;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.contribution.api.SequenceContributionRule;
import org.exbin.framework.contribution.ContributionSequenceBuilder;
import org.exbin.framework.sidebar.api.SideBarManagement;
import org.exbin.framework.action.api.ActionContextRegistration;

/**
 * Default sidebar manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SideBarManager extends ContributionManager implements SideBarManagement {

    protected final ContributionSequenceBuilder builder = new ContributionSequenceBuilder();
    protected JPanel sideBarPanel;
    protected JComponent activeComponent = null;

    public SideBarManager() {
        sideBarPanel = new JPanel(new BorderLayout());
    }

    @Override
    public void buildSideBar(JToolBar targetSideBar, String sideBarId, ActionContextRegistration actionContextRegistration) {
        ContributionDefinition definition = definitions.get(sideBarId);
        builder.buildSequence(new SideToolBarSequenceOutput(targetSideBar, actionContextRegistration), definition);
        actionContextRegistration.finish();
    }

    @Override
    public void registerSideBar(String sideBarId, String moduleId) {
        registerDefinition(sideBarId, moduleId);
    }

    @Nonnull
    @Override
    public ActionSideBarContribution registerSideBarItem(String sideBarId, String moduleId, Action action) {
        ContributionDefinition definition = definitions.get(sideBarId);
        if (definition == null) {
            throw new IllegalStateException("Definition with Id " + sideBarId + " doesn't exist");
        }

        ActionSideBarContribution sideBarContribution = new ActionSideBarContribution(action);
        definition.addContribution(sideBarContribution);
        return sideBarContribution;
    }

    @Nonnull
    @Override
    public GroupSequenceContribution registerSideBarGroup(String sideBarId, String moduleId, String groupId) {
        return registerContributionGroup(sideBarId, moduleId, groupId);
    }

    @Override
    public void registerSideBarRule(SequenceContribution contribution, SequenceContributionRule rule) {
        registerContributionRule(contribution, rule);
    }

    @Nonnull
    public List<Action> getAllManagedActions() {
        List<Action> actions = new ArrayList<>();
        for (ContributionDefinition definition : definitions.values()) {
            for (SequenceContribution contribution : definition.getContributions()) {
                if (contribution instanceof ActionSideBarContribution) {
                    actions.add(((ActionSideBarContribution) contribution).getAction());
                }
            }
        }
        return actions;
    }

    @Nonnull
    public JPanel getSideBarPanel() {
        return sideBarPanel;
    }

    @Nonnull
    public Optional<JComponent> getActiveComponent() {
        return Optional.ofNullable(activeComponent);
    }

    public void setActiveComponent(@Nullable JComponent component) {
        if (component == activeComponent) {
            return;
        }

        if (activeComponent != null) {
            sideBarPanel.remove(activeComponent);
        }
        if (component != null) {
            sideBarPanel.add(component, BorderLayout.CENTER);
        }
        sideBarPanel.revalidate();
        sideBarPanel.repaint();
        activeComponent = component;
    }
}
