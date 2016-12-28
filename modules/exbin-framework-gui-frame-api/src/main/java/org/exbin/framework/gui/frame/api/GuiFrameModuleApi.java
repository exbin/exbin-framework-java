/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.gui.frame.api;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplicationModule;
import org.exbin.framework.api.XBModuleRepositoryUtils;

/**
 * Interface for XBUP framework frame module.
 *
 * @version 0.2.0 2016/12/27
 * @author ExBin Project (http://exbin.org)
 */
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
    JDialog createDialog();

    /**
     * Creates basic dialog and sets it up.
     *
     * @param parentWindow parent window
     * @param modalityType modality type
     * @return dialog
     */
    JDialog createDialog(Window parentWindow, Dialog.ModalityType modalityType);

    /**
     * Creates basic dialog and sets it up.
     *
     * @param panel panel
     * @return dialog
     */
    JDialog createDialog(JPanel panel);

    /**
     * Creates basic dialog and sets it up.
     *
     * @param parentWindow parent window
     * @param modalityType modality type
     * @param panel panel
     * @return dialog
     */
    JDialog createDialog(Window parentWindow, Dialog.ModalityType modalityType, JPanel panel);

    /**
     * Returns frame instance.
     *
     * @return frame
     */
    Frame getFrame();

    /**
     * Returns exit action.
     *
     * @return exit action
     */
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

    Action getViewToolBarAction();

    Action getViewToolBarCaptionsAction();

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

    public void setDialogTitle(JDialog dialog, ResourceBundle resourceBundle);
}
