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
import org.exbin.framework.frame.api.ApplicationFrameHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.WindowPosition;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.menu.api.GroupMenuContributionRule;
import org.exbin.framework.menu.api.MenuContribution;
import org.exbin.framework.menu.api.MenuManagement;
import org.exbin.framework.menu.api.PositionMenuContributionRule;
import org.exbin.framework.menu.api.SeparationMenuContributionRule;
import org.exbin.framework.frame.action.FrameActions;
import org.exbin.framework.menu.api.MenuModuleApi;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.toolbar.api.ToolBarModuleApi;
import org.exbin.framework.utils.DesktopUtils;

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

    public static final String PREFERENCES_SCREEN_INDEX = "screenIndex";
    public static final String PREFERENCES_SCREEN_WIDTH = "screenWidth";
    public static final String PREFERENCES_SCREEN_HEIGHT = "screenHeight";
    public static final String PREFERENCES_POSITION_X = "positionX";
    public static final String PREFERENCES_POSITION_Y = "positionY";
    public static final String PREFERENCES_WIDTH = "width";
    public static final String PREFERENCES_HEIGHT = "height";
    public static final String PREFERENCES_MAXIMIZED = "maximized";

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
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID);
        MenuContribution contribution = mgmt.registerMenuItem(MenuModuleApi.FILE_SUBMENU_ID, resourceBundle.getString("fileMenu.text"));
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(MenuModuleApi.EDIT_SUBMENU_ID, resourceBundle.getString("editMenu.text"));
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(MenuModuleApi.VIEW_SUBMENU_ID, resourceBundle.getString("viewMenu.text"));
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(MenuModuleApi.TOOLS_SUBMENU_ID, resourceBundle.getString("toolsMenu.text"));
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(MenuModuleApi.OPTIONS_SUBMENU_ID, resourceBundle.getString("optionsMenu.text"));
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.TOP));
        contribution = mgmt.registerMenuItem(MenuModuleApi.HELP_SUBMENU_ID, resourceBundle.getString("helpMenu.text"));
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.TOP));
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
    public void notifyFrameUpdated() {
        if (frameActions != null) {
            frameActions.notifyFrameUpdated();
        }
    }

    @Override
    public void loadFramePosition() {
        getFrameHandler();
        WindowPosition framePosition = new WindowPosition();
        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
        if (preferencesFramePositionExists(preferencesModule.getAppPreferences(), PREFERENCES_FRAME_PREFIX)) {
            loadFramePositionFromPreferences(framePosition, preferencesModule.getAppPreferences(), PREFERENCES_FRAME_PREFIX);
            WindowUtils.setWindowPosition(applicationFrame, framePosition);
        }
    }

    @Override
    public void saveFramePosition() {
        WindowPosition windowPosition = WindowUtils.getWindowPosition(applicationFrame);
        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
        saveFramePositionToPreferences(windowPosition, preferencesModule.getAppPreferences(), PREFERENCES_FRAME_PREFIX);
    }

    static private void saveFramePositionToPreferences(WindowPosition windowPosition, OptionsStorage pref, String prefix) {
        pref.putInt(prefix + PREFERENCES_SCREEN_INDEX, windowPosition.getScreenIndex());
        pref.putInt(prefix + PREFERENCES_SCREEN_WIDTH, windowPosition.getScreenWidth());
        pref.putInt(prefix + PREFERENCES_SCREEN_HEIGHT, windowPosition.getScreenHeight());
        pref.putInt(prefix + PREFERENCES_POSITION_X, windowPosition.getRelativeX());
        pref.putInt(prefix + PREFERENCES_POSITION_Y, windowPosition.getRelativeY());
        pref.putInt(prefix + PREFERENCES_WIDTH, windowPosition.getWidth());
        pref.putInt(prefix + PREFERENCES_HEIGHT, windowPosition.getHeight());
        pref.putBoolean(prefix + PREFERENCES_MAXIMIZED, windowPosition.isMaximized());
    }

    static private void loadFramePositionFromPreferences(WindowPosition windowPosition, OptionsStorage pref, String prefix) {
        windowPosition.setScreenIndex(pref.getInt(prefix + PREFERENCES_SCREEN_INDEX, 0));
        windowPosition.setScreenWidth(pref.getInt(prefix + PREFERENCES_SCREEN_WIDTH, 0));
        windowPosition.setScreenHeight(pref.getInt(prefix + PREFERENCES_SCREEN_HEIGHT, 0));
        windowPosition.setRelativeX(pref.getInt(prefix + PREFERENCES_POSITION_X, 0));
        windowPosition.setRelativeY(pref.getInt(prefix + PREFERENCES_POSITION_Y, 0));
        windowPosition.setWidth(pref.getInt(prefix + PREFERENCES_WIDTH, 0));
        windowPosition.setHeight(pref.getInt(prefix + PREFERENCES_HEIGHT, 0));
        windowPosition.setMaximized(pref.getBoolean(prefix + PREFERENCES_MAXIMIZED, false));
    }

    static private boolean preferencesFramePositionExists(OptionsStorage pref, String prefix) {
        return pref.exists(prefix + PREFERENCES_SCREEN_INDEX);
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

        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.FILE_SUBMENU_ID);
        MenuContribution contribution = mgmt.registerMenuGroup(appClosingActionsGroup);
        mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.BOTTOM_LAST));
        mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(exitActionRegistered ? SeparationMenuContributionRule.SeparationMode.NONE : SeparationMenuContributionRule.SeparationMode.ABOVE));
        if (!exitActionRegistered) {
            contribution = mgmt.registerMenuItem(getExitAction());
            mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(appClosingActionsGroup));
        }
    }

    @Nonnull
    @Override
    public ApplicationFrameHandler getFrameHandler() {
        if (applicationFrame == null) {
            applicationFrame = new ApplicationFrame(undecorated);
            applicationFrame.initApplication();
            applicationFrame.setApplicationExitHandler(exitHandler);
            appIcon = applicationFrame.getIconImage();

            if (frameActions != null) {
                frameActions.setApplicationFrame(applicationFrame);
            }
        }

        return applicationFrame;
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
            if (applicationFrame != null) {
                frameActions.setApplicationFrame(applicationFrame);
            }
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
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        MenuContribution contribution = mgmt.registerMenuItem(frameActions.getViewToolBarAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(VIEW_BARS_GROUP_ID));
        contribution = mgmt.registerMenuItem(frameActions.getViewToolBarCaptionsAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(VIEW_BARS_GROUP_ID));
    }

    @Override
    public void registerStatusBarVisibilityActions() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        getFrameActions();
        createViewBarsMenuGroup();
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        MenuContribution contribution = mgmt.registerMenuItem(frameActions.getViewStatusBarAction());
        mgmt.registerMenuRule(contribution, new GroupMenuContributionRule(VIEW_BARS_GROUP_ID));
    }

    private void createViewBarsMenuGroup() {
        MenuModuleApi menuModule = App.getModule(MenuModuleApi.class);
        MenuManagement mgmt = menuModule.getMainMenuManagement(MODULE_ID).getSubMenu(MenuModuleApi.VIEW_SUBMENU_ID);
        if (!mgmt.menuGroupExists(VIEW_BARS_GROUP_ID)) {
            MenuContribution contribution = mgmt.registerMenuGroup(VIEW_BARS_GROUP_ID);
            mgmt.registerMenuRule(contribution, new PositionMenuContributionRule(PositionMenuContributionRule.PositionMode.TOP));
            mgmt.registerMenuRule(contribution, new SeparationMenuContributionRule(SeparationMenuContributionRule.SeparationMode.BELOW));
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
}
