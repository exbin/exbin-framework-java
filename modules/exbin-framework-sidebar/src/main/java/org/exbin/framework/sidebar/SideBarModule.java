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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import org.exbin.framework.App;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.sidebar.api.SideBarModuleApi;
import org.exbin.framework.sidebar.api.SideBarDefinitionManagement;
import org.exbin.framework.sidebar.api.SideBarManagement;
import org.exbin.framework.action.api.ActionContextRegistration;
import org.exbin.framework.action.api.ActionManagement;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.docking.api.SidePanelDocking;
import org.exbin.framework.frame.api.ComponentFrame;

/**
 * Implementation of side bar module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class SideBarModule implements SideBarModuleApi {

    private SideBarManager sideBarManager = null;
    private ResourceBundle resourceBundle;

    public SideBarModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(SideBarModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    @Override
    public List<Action> getSideBarManagedActions() {
        List<Action> actions = new ArrayList<>();
        SideBarModule.this.getSideBarManager();
        actions.addAll(sideBarManager.getAllManagedActions());

        return actions;
    }

    @Nonnull
    private SideBarManager getSideBarManager() {
        if (sideBarManager == null) {
            sideBarManager = new SideBarManager();
        }

        return sideBarManager;
    }

    @Nonnull
    @Override
    public SideBarManagement createSideBarManager() {
        return new SideBarManager();
    }

    @Override
    public void buildSideBar(JToolBar targetSideBar, String sideBarId, ActionContextRegistration actionContextRegistration) {
        SideBarModule.this.getSideBarManager().buildSideBar(targetSideBar, sideBarId, actionContextRegistration);
    }

    @Override
    public void registerSideBar(String sideBarId, String moduleId) {
        SideBarModule.this.getSideBarManager().registerSideBar(sideBarId, moduleId);
    }

    @Nonnull
    @Override
    public SideBarDefinitionManagement getMainSideBarManager(String moduleId) {
        return getSideBarManager(MAIN_SIDE_BAR_ID, moduleId);
    }

    @Nonnull
    @Override
    public SideBarDefinitionManagement getSideBarManager(String sideBarId, String moduleId) {
        return new SideBarDefinitionManager(SideBarModule.this.getSideBarManager(), sideBarId, moduleId);
    }

    @Nonnull
    @Override
    public void registerDockingSideBar(SidePanelDocking docking) {
        registerDockingSideBar(getSideBarManager(), docking);
    }

    @Nonnull
    public void registerDockingSideBar(SideBarManager sideBarManager, SidePanelDocking docking) {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        ComponentFrame frameHandler = frameModule.getFrameHandler();
        ActionManagement actionManager = frameHandler.getActionManager();
        JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
        toolBar.setFloatable(false);
        toolBar.setFocusable(false);
        sideBarManager.buildSideBar(toolBar, MODULE_ID, actionModule.createActionContextRegistrar(actionManager));
        JButton test1Button = new JButton("TEST1");
        JLabel label1 = new JLabel("TEST1");
        test1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComponent currentComponent = sideBarManager.getActiveComponent().orElse(null);
                if (currentComponent == label1) {
                    docking.setSidePanelVisible(false);
                    sideBarManager.setActiveComponent(null);
                } else {
                    if (!docking.isSidePanelVisible()) {
                        docking.setSidePanelVisible(true);
                    }
                    sideBarManager.setActiveComponent(label1);
                }
            }
        });
        test1Button.setFocusable(false);
        toolBar.add(test1Button);
        JButton test2Button = new JButton("TEST2");
        JLabel label2 = new JLabel("TEST2");
        test2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComponent currentComponent = sideBarManager.getActiveComponent().orElse(null);
                if (currentComponent == label2) {
                    docking.setSidePanelVisible(false);
                    sideBarManager.setActiveComponent(null);
                } else {
                    if (!docking.isSidePanelVisible()) {
                        docking.setSidePanelVisible(true);
                    }
                    sideBarManager.setActiveComponent(label2);
                }
            }
        });
        test2Button.setFocusable(false);
        toolBar.add(test2Button);

        toolBar.invalidate();
        docking.setSideToolBar(toolBar);
        docking.setSideComponent(sideBarManager.getSideBarPanel());
    }
}
