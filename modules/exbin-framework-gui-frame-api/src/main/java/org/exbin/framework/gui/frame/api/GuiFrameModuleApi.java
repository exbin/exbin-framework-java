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
package org.exbin.framework.gui.frame.api;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;

/**
 * Interface for framework frame module.
 *
 * @version 0.2.1 2019/07/15
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface GuiFrameModuleApi extends XBApplicationModule {

    public static String MODULE_ID = XBModuleRepositoryUtils.getModuleIdByApi(GuiFrameModuleApi.class);
    public static String MAIN_MENU_ID = MODULE_ID + ".mainMenu";
    public static String MAIN_TOOL_BAR_ID = MODULE_ID + ".mainToolBar";
    public static String FILE_MENU_ID = MAIN_MENU_ID + "/File";
    public static String EDIT_MENU_ID = MAIN_MENU_ID + "/Edit";
    public static String VIEW_MENU_ID = MAIN_MENU_ID + "/View";
    public static String TOOLS_MENU_ID = MAIN_MENU_ID + "/Tools";
    public static String OPTIONS_MENU_ID = MAIN_MENU_ID + "/Options";
    public static String HELP_MENU_ID = MAIN_MENU_ID + "/Help";

    public static String DEFAULT_STATUS_BAR_ID = "default";
    public static String MAIN_STATUS_BAR_ID = "main";
    public static String PROGRESS_STATUS_BAR_ID = "progress";
    public static String BUSY_STATUS_BAR_ID = "busy";

    public static String PREFERENCES_FRAME_RECTANGLE = "frameRectangle";

    /**
     * Returns frame handler.
     *
     * @return frame handler
     */
    @Nonnull
    ApplicationFrameHandler getFrameHandler();

    /**
     * Creates and initializes main menu and toolbar.
     */
    void createMainMenu();

    /**
     * Creates basic dialog and sets it up.
     *
     * @return dialog
     */
    @Nonnull
    DialogWrapper createDialog();

    /**
     * Creates basic dialog and sets it up.
     *
     * @param parentWindow parent window
     * @param modalityType modality type
     * @return dialog
     */
    @Nonnull
    DialogWrapper createDialog(Window parentWindow, Dialog.ModalityType modalityType);

    /**
     * Creates basic dialog and sets it up.
     *
     * @param panel panel
     * @return dialog
     */
    @Nonnull
    DialogWrapper createDialog(JPanel panel);

    /**
     * Creates basic dialog and sets it up.
     *
     * @param parentWindow parent window
     * @param modalityType modality type
     * @param panel panel
     * @return dialog
     */
    DialogWrapper createDialog(Window parentWindow, Dialog.ModalityType modalityType, JPanel panel);

    /**
     * Returns frame instance.
     *
     * @return frame
     */
    @Nonnull
    Frame getFrame();

    /**
     * Returns exit action.
     *
     * @return exit action
     */
    @Nonnull
    Action getExitAction();

    /**
     * Registers exit action in default menu location.
     */
    void registerExitAction();

    /**
     * Adds exit listener.
     *
     * @param listener listener
     */
    void addExitListener(ApplicationExitListener listener);

    /**
     * Removes exit listener.
     *
     * @param listener listener
     */
    void removeExitListener(ApplicationExitListener listener);

    @Nonnull
    Action getViewToolBarAction();

    @Nonnull
    Action getViewToolBarCaptionsAction();

    @Nonnull
    Action getViewStatusBarAction();

    void registerBarsVisibilityActions();

    void registerToolBarVisibilityActions();

    void registerStatusBarVisibilityActions();

    /**
     * Registers new status bar with unique ID.
     *
     * @param moduleId module id
     * @param statusBarId statusbar id
     * @param panel panel
     */
    void registerStatusBar(String moduleId, String statusBarId, JPanel panel);

    /**
     * Switches to status bar with specific ID.
     *
     * @param statusBarId statusbar id
     */
    void switchStatusBar(String statusBarId);

    void loadFramePosition();

    void saveFramePosition();

    void setDialogTitle(DialogWrapper dialog, ResourceBundle resourceBundle);
}
