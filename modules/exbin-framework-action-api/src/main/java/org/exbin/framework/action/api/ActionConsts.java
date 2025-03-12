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
package org.exbin.framework.action.api;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Action related constants.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ActionConsts {

    /**
     * Action type like or check, radio.
     *
     * Value is {@link ActionType}.
     */
    public static final String ACTION_TYPE = "type";
    /**
     * Radio group name value.
     *
     * Value is {@link String}.
     */
    public static final String ACTION_RADIO_GROUP = "radioGroup";
    /**
     * Action mode for actions opening dialogs.
     *
     * Value is {@link Boolean}.
     */
    public static final String ACTION_DIALOG_MODE = "dialogMode";
    /**
     * Menu creation handler.
     *
     * Value is {@link ActionMenuCreation}.
     */
    public static final String ACTION_MENU_CREATION = "menuCreation";
    /**
     * Menu activation bus / message registration.
     *
     * Value is {@link ActionMenuActivation}.
     */
    public static final String ACTION_MENU_ACTIVATION = "menuActivation";
    /**
     * Context change bus / message registration.
     *
     * Value is {@link ActionContextChange}.
     */
    public static final String ACTION_CONTEXT_CHANGE = "actionContextChange";

    public static final String ACTION_ID = "actionId";
    public static final String ACTION_NAME_POSTFIX = ".text";
    public static final String ACTION_SHORT_DESCRIPTION_POSTFIX = ".shortDescription";
    public static final String ACTION_SMALL_ICON_POSTFIX = ".smallIcon";
    public static final String ACTION_SMALL_LARGE_POSTFIX = ".largeIcon";
    public static final String CYCLE_POPUP_MENU = "cyclePopupMenu";

    public static final String MAIN_MENU_ID = "mainMenu";
    public static final String MAIN_TOOL_BAR_ID = "mainToolBar";
    public static final String FILE_SUBMENU_ID = MAIN_MENU_ID + "/File";
    public static final String EDIT_SUBMENU_ID = MAIN_MENU_ID + "/Edit";
    public static final String VIEW_SUBMENU_ID = MAIN_MENU_ID + "/View";
    public static final String TOOLS_SUBMENU_ID = MAIN_MENU_ID + "/Tools";
    public static final String OPTIONS_SUBMENU_ID = MAIN_MENU_ID + "/Options";
    public static final String HELP_SUBMENU_ID = MAIN_MENU_ID + "/Help";

    private ActionConsts() {
    }
}
