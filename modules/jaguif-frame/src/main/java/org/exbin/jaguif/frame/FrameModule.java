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
package org.exbin.jaguif.frame;

import com.formdev.flatlaf.extras.FlatDesktop;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.jaguif.App;
import org.exbin.jaguif.frame.api.FrameModuleApi;
import org.exbin.jaguif.language.api.LanguageModuleApi;
import org.exbin.jaguif.utils.WindowPosition;
import org.exbin.jaguif.utils.WindowUtils;
import org.exbin.jaguif.action.api.ActionModuleApi;
import org.exbin.jaguif.action.api.DialogParentComponent;
import org.exbin.jaguif.contribution.api.GroupSequenceContributionRule;
import org.exbin.jaguif.contribution.api.PositionSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SeparationSequenceContributionRule;
import org.exbin.jaguif.contribution.api.SequenceContribution;
import org.exbin.jaguif.frame.action.FrameActions;
import org.exbin.jaguif.menu.api.MenuModuleApi;
import org.exbin.jaguif.toolbar.api.ToolBarModuleApi;
import org.exbin.jaguif.utils.DesktopUtils;
import org.exbin.jaguif.options.api.OptionsModuleApi;
import org.exbin.jaguif.menu.api.MenuDefinitionManagement;
import org.exbin.jaguif.context.api.ActiveContextManagement;
import org.exbin.jaguif.frame.settings.FrameAppearanceOptions;
import org.exbin.jaguif.frame.settings.FrameAppearanceSettingsApplier;
import org.exbin.jaguif.frame.settings.FrameAppearanceSettingsComponent;
import org.exbin.jaguif.options.settings.api.ApplySettingsContribution;
import org.exbin.jaguif.options.settings.api.OptionsSettingsManagement;
import org.exbin.jaguif.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.jaguif.options.settings.api.SettingsComponentContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContribution;
import org.exbin.jaguif.options.settings.api.SettingsPageContributionRule;
import org.exbin.jaguif.frame.api.ContextFrame;
import org.exbin.jaguif.options.api.PrefixOptionsStorage;
import org.exbin.jaguif.window.settings.WindowPositionOptions;
import org.exbin.jaguif.frame.api.ComponentFrame;
import org.exbin.jaguif.context.api.ContextActivable;
import org.exbin.jaguif.frame.contribution.ExitContribution;
import org.exbin.jaguif.frame.contribution.ViewStatusBarContribution;
import org.exbin.jaguif.frame.contribution.ViewToolBarCaptionsContribution;
import org.exbin.jaguif.frame.contribution.ViewToolBarContribution;
import org.exbin.jaguif.utils.ComponentProvider;
import org.exbin.jaguif.utils.WindowClosingListener;

/**
 * Module for window frame support.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FrameModule implements FrameModuleApi {

    public static final String FILE_EXIT_GROUP_ID = MODULE_ID + ".exit";
    public static final String VIEW_BARS_GROUP_ID = MODULE_ID + ".view";
    public static final String PREFERENCES_FRAME_PREFIX = "mainFrame.";
    public static final String RESOURCES_DIALOG_TITLE = "dialog.title";

    private ResourceBundle resourceBundle;
    private ApplicationFrame applicationFrame;
    private boolean undecorated = false;
    private FrameClosingHandler exitHandler = null;
    private StatusBarHandler statusBarHandler = null;
    private FrameActions frameActions;
    private Image appIcon = null;

    public FrameModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(FrameModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Override
    public void init() {
        ensureSetup();
        initMainMenu();
        initMainToolBar();
    }

    private void initMainMenu() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(MenuModuleApi.FILE_SUBMENU_ID, resourceBundle.getString("fileMenu.text"));
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(MenuModuleApi.EDIT_SUBMENU_ID, resourceBundle.getString("editMenu.text"));
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(MenuModuleApi.VIEW_SUBMENU_ID, resourceBundle.getString("viewMenu.text"));
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(MenuModuleApi.TOOLS_SUBMENU_ID, resourceBundle.getString("toolsMenu.text"));
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(MenuModuleApi.OPTIONS_SUBMENU_ID, resourceBundle.getString("optionsMenu.text"));
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(MenuModuleApi.HELP_SUBMENU_ID, resourceBundle.getString("helpMenu.text"));
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
    }

    private void initMainToolBar() {
        ToolBarModuleApi toolBarModule = App.getModule(ToolBarModuleApi.class);
        toolBarModule.registerToolBar(ToolBarModuleApi.MAIN_TOOL_BAR_ID, MODULE_ID);
    }

    @Nonnull
    @Override
    public Frame getFrame() {
        return getFrameHandler().getFrame();
    }

    @Override
    public void loadFramePosition() {
        getFrameHandler();
        WindowPosition framePosition = new WindowPosition();
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        PrefixOptionsStorage frameOptionsStorage = new PrefixOptionsStorage(optionsModule.getAppOptions(), PREFERENCES_FRAME_PREFIX);
        WindowPositionOptions windowPositionOptions = new WindowPositionOptions(frameOptionsStorage);
        if (windowPositionOptions.preferencesFramePositionExists()) {
            windowPositionOptions.getWindowPosition(framePosition);
            WindowUtils.setWindowPosition(applicationFrame, framePosition);
        }
    }

    @Override
    public void saveFramePosition() {
        WindowPosition windowPosition = WindowUtils.getWindowPosition(applicationFrame);
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        PrefixOptionsStorage frameOptionsStorage = new PrefixOptionsStorage(optionsModule.getAppOptions(), PREFERENCES_FRAME_PREFIX);
        WindowPositionOptions windowPositionOptions = new WindowPositionOptions(frameOptionsStorage);
        windowPositionOptions.setWindowPosition(windowPosition);
    }

    @Nonnull
    @Override
    public Action getExitAction() {
        Action exitAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (exitHandler != null) {
                    exitHandler.executeExit(getFrameHandler());
                } else {
                    System.exit(0);
                }
            }
        };
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(exitAction, resourceBundle, "exitAction");
        exitAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.KeyEvent.ALT_DOWN_MASK));

        return exitAction;
    }

    @Override
    public void registerExitAction() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        String appClosingActionsGroup = "ApplicationClosingActionsGroup";
        boolean exitActionRegistered = false;

        if (DesktopUtils.detectBasicOs() == DesktopUtils.OsType.MACOSX) {
            FlatDesktop.setQuitHandler(response -> {
                if (exitHandler != null) {
                    boolean canExit = exitHandler.canExit(getFrameHandler());
                    if (canExit) {
                        response.performQuit();
                    } else {
                        response.cancelQuit();
                    }
                } else {
                    response.performQuit();
                }
            });
            /* // TODO: Replace after migration to Java 9+
            Desktop desktop = Desktop.getDesktop();
            desktop.setQuitHandler((e, response) -> {
                if (exitHandler != null) {
                    boolean canExit = exitHandler.canExit(getFrameHandler());
                    if (canExit) {
                        response.performQuit();
                    } else {
                        response.cancelQuit();
                    }
                } else {
                    response.performQuit();
                }
            });
            desktop.setQuitStrategy(QuitStrategy.NORMAL_EXIT); */
            exitActionRegistered = true;
        }

        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuGroup(appClosingActionsGroup);
        mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.BOTTOM_LAST));
        mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(exitActionRegistered ? SeparationSequenceContributionRule.SeparationMode.NONE : SeparationSequenceContributionRule.SeparationMode.ABOVE));
        if (!exitActionRegistered) {
            contribution = new ExitContribution();
            mgmt.registerMenuContribution(contribution);
            mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(appClosingActionsGroup));
        }
    }

    @Nonnull
    @Override
    public ComponentFrame getFrameHandler() {
        if (applicationFrame == null) {
            applicationFrame = new ApplicationFrame(undecorated);
            applicationFrame.initApplication();
            applicationFrame.setApplicationExitHandler(exitHandler);
            appIcon = applicationFrame.getIconImage();

            FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
            ActiveContextManagement contextManager = frameModule.getFrameHandler().getContextManager();
            contextManager.changeActiveState(ContextFrame.class, applicationFrame);
            contextManager.changeActiveState(DialogParentComponent.class, new DialogParentComponent() {
                @Nonnull
                @Override
                public Component getComponent() {
                    return applicationFrame;
                }
            });

            OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
            OptionsSettingsManagement mainSettingsManager = optionsSettingsModule.getMainSettingsManager();
            mainSettingsManager.applyContextOptions(ContextFrame.class, applicationFrame, mainSettingsManager.getSettingsOptionsProvider());
        }

        return applicationFrame;
    }
    
    @Override
    public void attachFrameContentComponent(ComponentProvider componentProvider) {
        ComponentFrame frameHandler = getFrameHandler();
        frameHandler.setMainPanel(componentProvider.getComponent());
        if (componentProvider instanceof ContextActivable) {
            ActiveContextManagement contextManager = frameHandler.getContextManager();
            ((ContextActivable) componentProvider).notifyActivated(contextManager);
        }
        if (componentProvider instanceof WindowClosingListener) {
            addClosingListener((WindowClosingListener) componentProvider);
        }
    }

    @Override
    public void addClosingListener(WindowClosingListener listener) {
        getExitHandler().addClosingListener(listener);
    }

    @Override
    public void removeClosingListener(WindowClosingListener listener) {
        getExitHandler().removeClosingListener(listener);
    }

    @Nonnull
    private FrameClosingHandler getExitHandler() {
        if (exitHandler == null) {
            exitHandler = new FrameClosingHandler();
            if (applicationFrame != null) {
                applicationFrame.setApplicationExitHandler(exitHandler);
            }
        }

        return exitHandler;
    }

    @Nonnull
    private StatusBarHandler getStatusBarHandler() {
        getFrameHandler();
        if (statusBarHandler == null) {
            statusBarHandler = new StatusBarHandler(applicationFrame);
        }

        return statusBarHandler;
    }

    @Override
    public void registerStatusBar(String moduleId, String statusBarId, JPanel panel) {
        getStatusBarHandler().registerStatusBar(moduleId, statusBarId, panel);
    }

    @Override
    public void switchStatusBar(String statusBarId) {
        getStatusBarHandler().switchStatusBar(statusBarId);
    }

    @Nonnull
    public FrameActions getFrameActions() {
        if (frameActions == null) {
            frameActions = new FrameActions();
            ensureSetup();
            frameActions.init(resourceBundle);
        }

        return frameActions;
    }

    @Override
    public void registerBarsVisibilityActions() {
        registerToolBarVisibilityActions();
        registerStatusBarVisibilityActions();
    }

    @Override
    public void registerToolBarVisibilityActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        getFrameActions();
        createViewBarsMenuGroup();
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = new ViewToolBarContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(VIEW_BARS_GROUP_ID));
        contribution = new ViewToolBarCaptionsContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(VIEW_BARS_GROUP_ID));
    }

    @Override
    public void registerStatusBarVisibilityActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        getFrameActions();
        createViewBarsMenuGroup();
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = new ViewStatusBarContribution();
        mgmt.registerMenuContribution(contribution);
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(VIEW_BARS_GROUP_ID));
    }

    private void createViewBarsMenuGroup() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        if (!mgmt.menuGroupExists(VIEW_BARS_GROUP_ID)) {
            SequenceContribution contribution = mgmt.registerMenuGroup(VIEW_BARS_GROUP_ID);
            mgmt.registerMenuRule(contribution, new PositionSequenceContributionRule(PositionSequenceContributionRule.PositionMode.TOP));
            mgmt.registerMenuRule(contribution, new SeparationSequenceContributionRule(SeparationSequenceContributionRule.SeparationMode.BELOW));
        }
    }

    @Override
    public void switchFrameToFullscreen() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        device.setFullScreenWindow(getFrame());
    }

    @Override
    public void switchFrameToUndecorated() {
        this.undecorated = true;
    }

    @Nonnull
    @Override
    public Optional<Image> getApplicationIcon() {
        return Optional.ofNullable(appIcon);
    }

    @Override
    public void registerSettings() {
        OptionsSettingsModuleApi settingsModule = App.getModule(OptionsSettingsModuleApi.class);
        OptionsSettingsManagement settingsManagement = settingsModule.getMainSettingsManager();

        settingsManagement.registerSettingsOptions(FrameAppearanceOptions.class, (optionsStorage) -> new FrameAppearanceOptions(optionsStorage));

        settingsManagement.registerApplySetting(FrameAppearanceOptions.class, new ApplySettingsContribution(FrameAppearanceSettingsApplier.APPLIER_ID, new FrameAppearanceSettingsApplier()));
        settingsManagement.registerApplyContextSetting(ContextFrame.class, new ApplySettingsContribution(FrameAppearanceSettingsApplier.APPLIER_ID, new FrameAppearanceSettingsApplier()));

        SettingsPageContribution pageContribution = new SettingsPageContribution(FrameModuleApi.SETTINGS_PAGE_ID, getResourceBundle());
        settingsManagement.registerPage(pageContribution);
        SettingsComponentContribution settingsComponent = settingsManagement.registerComponent(FrameAppearanceSettingsComponent.COMPONENT_ID, new FrameAppearanceSettingsComponent());
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(pageContribution));
    }
}
