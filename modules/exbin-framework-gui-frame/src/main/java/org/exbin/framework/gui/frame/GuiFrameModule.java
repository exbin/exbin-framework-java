/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.gui.frame;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.ApplicationExitListener;
import org.exbin.framework.gui.frame.api.ApplicationFrameHandler;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.action.api.MenuGroup;
import org.exbin.framework.gui.action.api.MenuPosition;
import org.exbin.framework.gui.action.api.PositionMode;
import org.exbin.framework.gui.action.api.SeparationMode;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowPosition;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.gui.action.api.GuiActionModuleApi;
import org.exbin.framework.gui.frame.action.FrameActions;

/**
 * Implementation of XBUP framework frame module.
 *
 * @version 0.2.1 2021/10/26
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GuiFrameModule implements GuiFrameModuleApi {

    public static final String FILE_EXIT_GROUP_ID = MODULE_ID + ".exit";
    public static final String VIEW_BARS_GROUP_ID = MODULE_ID + ".view";
    public static final String PREFERENCES_FRAME_PREFIX = "mainFrame.";
    public static final String RESOURCES_DIALOG_TITLE = "dialog.title";

    private XBApplication application;
    private ResourceBundle resourceBundle;
    private XBApplicationFrame frame;
    private ApplicationExitHandler exitHandler = null;
    private StatusBarHandler statusBarHandler = null;
    private FrameActions frameActions;

    public GuiFrameModule() {
    }

    @Override
    public void init(XBModuleHandler moduleHandler) {
        this.application = (XBApplication) moduleHandler;
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(GuiFrameModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Override
    public void createMainMenu() {
        ensureSetup();
        initMainMenu();
        initMainToolBar();
    }

    private void initMainMenu() {
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenu(MAIN_MENU_ID, MODULE_ID);
        actionModule.registerMenu(FILE_MENU_ID, MODULE_ID);
        actionModule.registerMenu(EDIT_MENU_ID, MODULE_ID);
        actionModule.registerMenu(VIEW_MENU_ID, MODULE_ID);
        actionModule.registerMenu(TOOLS_MENU_ID, MODULE_ID);
        actionModule.registerMenu(OPTIONS_MENU_ID, MODULE_ID);
        actionModule.registerMenu(HELP_MENU_ID, MODULE_ID);

        actionModule.registerMenuItem(MAIN_MENU_ID, MODULE_ID, FILE_MENU_ID, resourceBundle.getString("fileMenu.text"), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(MAIN_MENU_ID, MODULE_ID, EDIT_MENU_ID, resourceBundle.getString("editMenu.text"), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(MAIN_MENU_ID, MODULE_ID, VIEW_MENU_ID, resourceBundle.getString("viewMenu.text"), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(MAIN_MENU_ID, MODULE_ID, TOOLS_MENU_ID, resourceBundle.getString("toolsMenu.text"), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(MAIN_MENU_ID, MODULE_ID, OPTIONS_MENU_ID, resourceBundle.getString("optionsMenu.text"), new MenuPosition(PositionMode.TOP));
        actionModule.registerMenuItem(MAIN_MENU_ID, MODULE_ID, HELP_MENU_ID, resourceBundle.getString("helpMenu.text"), new MenuPosition(PositionMode.TOP));
    }

    private void initMainToolBar() {
        GuiActionModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        menuModule.registerToolBar(MAIN_TOOL_BAR_ID, MODULE_ID);
    }

    @Override
    public void unregisterModule(String moduleId) {
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
        if (framePosition.preferencesExists(application.getAppPreferences(), PREFERENCES_FRAME_PREFIX)) {
            framePosition.loadFromPreferences(application.getAppPreferences(), PREFERENCES_FRAME_PREFIX);
            WindowUtils.setWindowPosition(frame, framePosition);
        }
    }

    @Override
    public void saveFramePosition() {
        WindowPosition windowPosition = WindowUtils.getWindowPosition(frame);
        windowPosition.saveToPreferences(application.getAppPreferences(), PREFERENCES_FRAME_PREFIX);
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
        ActionUtils.setupAction(exitAction, resourceBundle, "exitAction");
        exitAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.KeyEvent.ALT_DOWN_MASK));

        return exitAction;
    }

    @Override
    public void registerExitAction() {
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        String appClosingActionsGroup = "ApplicationClosingActionsGroup";
        actionModule.registerMenuGroup(GuiFrameModuleApi.FILE_MENU_ID, new MenuGroup(appClosingActionsGroup, new MenuPosition(PositionMode.BOTTOM_LAST), SeparationMode.ABOVE));
        actionModule.registerMenuItem(GuiFrameModuleApi.FILE_MENU_ID, MODULE_ID, getExitAction(), new MenuPosition(appClosingActionsGroup));
    }

    @Nonnull
    @Override
    public ApplicationFrameHandler getFrameHandler() {
        if (frame == null) {
            frame = new XBApplicationFrame();
            frame.setApplication(application);
            frame.loadMainMenu(application);
            frame.loadMainToolBar(application);
            frame.setApplicationExitHandler(exitHandler);

            if (frameActions != null) {
                frameActions.setApplicationFrame(frame);
            }
        }

        return frame;
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
            if (frame != null) {
                frame.setApplicationExitHandler(exitHandler);
            }
        }

        return exitHandler;
    }

    @Nonnull
    private StatusBarHandler getStatusBarHandler() {
        getFrameHandler();
        if (statusBarHandler == null) {
            statusBarHandler = new StatusBarHandler(frame);
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
            frameActions.setup(application, resourceBundle);
            if (frame != null) {
                frameActions.setApplicationFrame(frame);
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
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        getFrameActions();
        createViewBarsMenuGroup();
        actionModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, frameActions.getViewToolBarAction(), new MenuPosition(VIEW_BARS_GROUP_ID));
        actionModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, frameActions.getViewToolBarCaptionsAction(), new MenuPosition(VIEW_BARS_GROUP_ID));
    }

    @Override
    public void registerStatusBarVisibilityActions() {
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        getFrameActions();
        createViewBarsMenuGroup();
        actionModule.registerMenuItem(GuiFrameModuleApi.VIEW_MENU_ID, MODULE_ID, frameActions.getViewStatusBarAction(), new MenuPosition(VIEW_BARS_GROUP_ID));
    }

    private void createViewBarsMenuGroup() {
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        if (!actionModule.menuGroupExists(GuiFrameModuleApi.VIEW_MENU_ID, VIEW_BARS_GROUP_ID)) {
            actionModule.registerMenuGroup(GuiFrameModuleApi.VIEW_MENU_ID, new MenuGroup(VIEW_BARS_GROUP_ID, new MenuPosition(PositionMode.TOP), SeparationMode.BELOW));
        }
    }

    @Nonnull
    @Override
    public DialogWrapper createDialog() {
        return createDialog(getFrame(), Dialog.ModalityType.APPLICATION_MODAL);
    }

    @Nonnull
    @Override
    public DialogWrapper createDialog(Window parentWindow, Dialog.ModalityType modalityType) {
        return createDialog(parentWindow, modalityType, null);
    }

    @Nonnull
    @Override
    public DialogWrapper createDialog(JPanel panel) {
        return createDialog(getFrame(), Dialog.ModalityType.APPLICATION_MODAL, panel);
    }

    @Nonnull
    @Override
    public DialogWrapper createDialog(Window parentWindow, Dialog.ModalityType modalityType, JPanel panel) {
        DialogWrapper dialog = WindowUtils.createDialog(panel, parentWindow, "", modalityType);
        Image applicationIcon = application.getApplicationIcon();
        if (applicationIcon != null) {
            ((JDialog) dialog.getWindow()).setIconImage(applicationIcon);
        }
        return dialog;
    }

    @Override
    public void setDialogTitle(DialogWrapper dialog, ResourceBundle resourceBundle) {
        ((JDialog) dialog.getWindow()).setTitle(resourceBundle.getString(RESOURCES_DIALOG_TITLE));
    }
}
