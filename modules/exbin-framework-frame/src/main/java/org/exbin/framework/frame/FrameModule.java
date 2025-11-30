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
package org.exbin.framework.frame;

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
import org.exbin.framework.App;
import org.exbin.framework.frame.api.ApplicationExitListener;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.WindowPosition;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.action.api.DialogParentComponent;
import org.exbin.framework.contribution.api.GroupSequenceContributionRule;
import org.exbin.framework.contribution.api.PositionSequenceContributionRule;
import org.exbin.framework.contribution.api.SeparationSequenceContributionRule;
import org.exbin.framework.contribution.api.SequenceContribution;
import org.exbin.framework.frame.action.FrameActions;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;
import org.exbin.framework.utils.DesktopUtils;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.menu.api.MenuDefinitionManagement;
import org.exbin.framework.context.api.ActiveContextManagement;
import org.exbin.framework.frame.settings.FrameAppearanceOptions;
import org.exbin.framework.frame.settings.FrameAppearanceSettingsApplier;
import org.exbin.framework.frame.settings.FrameAppearanceSettingsComponent;
import org.exbin.framework.options.settings.api.ApplySettingsContribution;
import org.exbin.framework.options.settings.api.OptionsSettingsManagement;
import org.exbin.framework.options.settings.api.OptionsSettingsModuleApi;
import org.exbin.framework.options.settings.api.SettingsComponentContribution;
import org.exbin.framework.options.settings.api.SettingsPageContribution;
import org.exbin.framework.options.settings.api.SettingsPageContributionRule;
import org.exbin.framework.frame.api.ContextFrame;
import org.exbin.framework.options.api.PrefixOptionsStorage;
import org.exbin.framework.window.settings.WindowPositionOptions;
import org.exbin.framework.frame.api.ComponentFrame;
import org.exbin.framework.context.api.ContextActivable;
import org.exbin.framework.utils.ComponentProvider;

/**
 * Module frame handling.
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
    private ApplicationExitHandler exitHandler = null;
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
            contribution = mgmt.registerMenuItem(getExitAction());
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

            OptionsSettingsModuleApi optionsSettingsModule = App.getModule(OptionsSettingsModuleApi.class);
            OptionsSettingsManagement mainSettingsManager = optionsSettingsModule.getMainSettingsManager();
            mainSettingsManager.applyOptions(ContextFrame.class, applicationFrame, mainSettingsManager.getSettingsOptionsProvider());
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
    }

    @Override
    public void addExitListener(ApplicationExitListener listener) {
        getExitHandler().addListener(listener);
    }

    @Override
    public void removeExitListener(ApplicationExitListener listener) {
        getExitHandler().removeListener(listener);
    }

    @Nonnull
    private ApplicationExitHandler getExitHandler() {
        if (exitHandler == null) {
            exitHandler = new ApplicationExitHandler();
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
            frameActions.setup(resourceBundle);
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
        SequenceContribution contribution = mgmt.registerMenuItem(frameActions.createViewToolBarAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(VIEW_BARS_GROUP_ID));
        contribution = mgmt.registerMenuItem(frameActions.createViewToolBarCaptionsAction());
        mgmt.registerMenuRule(contribution, new GroupSequenceContributionRule(VIEW_BARS_GROUP_ID));
    }

    @Override
    public void registerStatusBarVisibilityActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        getFrameActions();
        createViewBarsMenuGroup();
        MenuDefinitionManagement mgmt = menuModule.getMainMenuManager(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        SequenceContribution contribution = mgmt.registerMenuItem(frameActions.createViewStatusBarAction());
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

        settingsManagement.registerOptionsSettings(FrameAppearanceOptions.class, (optionsStorage) -> new FrameAppearanceOptions(optionsStorage));

        settingsManagement.registerApplySetting(ContextFrame.class, new ApplySettingsContribution(FrameAppearanceSettingsApplier.APPLIER_ID, new FrameAppearanceSettingsApplier()));

        SettingsPageContribution pageContribution = new SettingsPageContribution(FrameModuleApi.SETTINGS_PAGE_ID, getResourceBundle());
        settingsManagement.registerPage(pageContribution);
        SettingsComponentContribution settingsComponent = settingsManagement.registerComponent(FrameAppearanceSettingsComponent.COMPONENT_ID, new FrameAppearanceSettingsComponent());
        settingsManagement.registerSettingsRule(settingsComponent, new SettingsPageContributionRule(pageContribution));
    }
}
