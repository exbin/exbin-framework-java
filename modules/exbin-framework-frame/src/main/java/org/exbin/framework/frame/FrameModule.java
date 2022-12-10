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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.frame.api.ApplicationExitListener;
import org.exbin.framework.frame.api.ApplicationFrameHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.LanguageUtils;
import org.exbin.framework.utils.WindowPosition;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.frame.action.FrameActions;
import org.exbin.framework.utils.handler.OkCancelService;

/**
 * Implementation of XBUP framework frame module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class FrameModule implements FrameModuleApi {

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

    public FrameModule() {
    }

    @Override
    public void init(XBModuleHandler moduleHandler) {
        this.application = (XBApplication) moduleHandler;
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(FrameModule.class);
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
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
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
        ActionModuleApi menuModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
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
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        String appClosingActionsGroup = "ApplicationClosingActionsGroup";
        actionModule.registerMenuGroup(FrameModuleApi.FILE_MENU_ID, new MenuGroup(appClosingActionsGroup, new MenuPosition(PositionMode.BOTTOM_LAST), SeparationMode.ABOVE));
        actionModule.registerMenuItem(FrameModuleApi.FILE_MENU_ID, MODULE_ID, getExitAction(), new MenuPosition(appClosingActionsGroup));
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
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        getFrameActions();
        createViewBarsMenuGroup();
        actionModule.registerMenuItem(FrameModuleApi.VIEW_MENU_ID, MODULE_ID, frameActions.getViewToolBarAction(), new MenuPosition(VIEW_BARS_GROUP_ID));
        actionModule.registerMenuItem(FrameModuleApi.VIEW_MENU_ID, MODULE_ID, frameActions.getViewToolBarCaptionsAction(), new MenuPosition(VIEW_BARS_GROUP_ID));
    }

    @Override
    public void registerStatusBarVisibilityActions() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        getFrameActions();
        createViewBarsMenuGroup();
        actionModule.registerMenuItem(FrameModuleApi.VIEW_MENU_ID, MODULE_ID, frameActions.getViewStatusBarAction(), new MenuPosition(VIEW_BARS_GROUP_ID));
    }

    private void createViewBarsMenuGroup() {
        ActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(ActionModuleApi.class);
        if (!actionModule.menuGroupExists(FrameModuleApi.VIEW_MENU_ID, VIEW_BARS_GROUP_ID)) {
            actionModule.registerMenuGroup(FrameModuleApi.VIEW_MENU_ID, new MenuGroup(VIEW_BARS_GROUP_ID, new MenuPosition(PositionMode.TOP), SeparationMode.BELOW));
        }
    }

    @Nonnull
    @Override
    public DialogWrapper createDialog() {
        return createDialog(null, null);
    }

    @Nonnull
    @Override
    public DialogWrapper createDialog(@Nullable JPanel panel) {
        return createDialog(getFrame(), Dialog.ModalityType.APPLICATION_MODAL, panel, null);
    }

    @Nonnull
    @Override
    public DialogWrapper createDialog(@Nullable JPanel panel, @Nullable JPanel controlPanel) {
        return createDialog(getFrame(), Dialog.ModalityType.APPLICATION_MODAL, panel, controlPanel);
    }

    @Nonnull
    @Override
    public DialogWrapper createDialog(Component parentComponent, Dialog.ModalityType modalityType, @Nullable JPanel panel) {
        return createDialog(parentComponent, modalityType, panel, null);
    }

    @Nonnull
    @Override
    public DialogWrapper createDialog(Component parentComponent, Dialog.ModalityType modalityType, @Nullable JPanel panel, @Nullable JPanel controlPanel) {
        JPanel dialogPanel = controlPanel != null ? WindowUtils.createDialogPanel(panel, controlPanel) : panel;

        DialogWrapper dialog = WindowUtils.createDialog(dialogPanel, parentComponent, "", modalityType);
        Optional<Image> applicationIcon = application.getApplicationIcon();
        if (applicationIcon.isPresent()) {
            ((JDialog) dialog.getWindow()).setIconImage(applicationIcon.get());
        }
        if (controlPanel instanceof OkCancelService) {
            JButton defaultButton = ((OkCancelService) controlPanel).getDefaultButton();
            if (defaultButton != null) {
                JRootPane rootPane = SwingUtilities.getRootPane(dialog.getWindow());
                if (rootPane != null) {
                    rootPane.setDefaultButton(defaultButton);
                }
            }
        }
        return dialog;
    }

    @Override
    public void setDialogTitle(DialogWrapper dialog, ResourceBundle resourceBundle) {
        ((JDialog) dialog.getWindow()).setTitle(resourceBundle.getString(RESOURCES_DIALOG_TITLE));
    }
}
