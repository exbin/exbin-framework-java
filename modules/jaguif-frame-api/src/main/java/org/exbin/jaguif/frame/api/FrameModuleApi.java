/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.frame.api;

import java.awt.Frame;
import java.awt.Image;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import javax.swing.JComponent;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;
import org.exbin.jaguif.utils.ComponentProvider;
import org.exbin.jaguif.utils.WindowClosingListener;

/**
 * Interface for framework frame module.
 */
@ParametersAreNonnullByDefault
public interface FrameModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(FrameModuleApi.class);

    public static final String SETTINGS_PAGE_ID = "appearance";
    public static final String DEFAULT_STATUS_BAR_ID = "default";
    public static final String MAIN_STATUS_BAR_ID = "main";
    public static final String PROGRESS_STATUS_BAR_ID = "progress";
    public static final String BUSY_STATUS_BAR_ID = "busy";

    public static final String OPTIONS_KEY_FRAME_RECTANGLE = "frameRectangle";

    /**
     * Returns resource bundle.
     *
     * @return resource bundle
     */
    @Nonnull
    ResourceBundle getResourceBundle();

    /**
     * Returns frame controller.
     *
     * @return frame controller
     */
    @Nonnull
    FrameController getFrameController();

    /**
     * Attaches component provider to the component frame.
     *
     * @param componentProvider component provider
     */
    void attachFrameContentComponent(ComponentProvider componentProvider);

    /**
     * Creates and initializes main menu and toolbar.
     */
    void init();

    /**
     * Returns frame instance.
     *
     * TODO: Support for multiple frames
     *
     * @return frame
     */
    @Nonnull
    Frame getFrame();

    /**
     * Creates exit action.
     *
     * @return exit action
     */
    @Nonnull
    Action createExitAction();

    /**
     * Registers exit action in default menu location.
     */
    void registerExitAction();

    /**
     * Adds closing listener.
     *
     * @param listener listener
     */
    void addClosingListener(WindowClosingListener listener);

    /**
     * Removes closing listener.
     *
     * @param listener listener
     */
    void removeClosingListener(WindowClosingListener listener);

    /**
     * Registers visibility actions for both status bar and tool bar.
     */
    void registerBarsVisibilityActions();

    /**
     * Registers visibility actions for both tool bar.
     */
    void registerToolBarVisibilityActions();

    /**
     * Registers visibility actions for both status bar.
     */
    void registerStatusBarVisibilityActions();

    /**
     * Registers new status bar with unique ID.
     *
     * @param moduleId module id
     * @param statusBarId statusbar id
     * @param component status bar component
     */
    void registerStatusBar(String moduleId, String statusBarId, JComponent component);

    /**
     * Switches to status bar with specific ID.
     *
     * @param statusBarId statusbar id
     */
    void switchStatusBar(String statusBarId);

    /**
     * Loads frame position.
     */
    void loadFramePosition();

    /**
     * Saves frame position.
     */
    void saveFramePosition();

    /**
     * Switches frame to full screen mode.
     */
    void switchFrameToFullscreen();

    /**
     * Switches frame to undecorated mode.
     */
    void switchFrameToUndecorated();

    /**
     * Returns icon for this frame.
     *
     * @return icon if assigned
     */
    @Nonnull
    Optional<Image> getApplicationIcon();

    /**
     * Registers settings options and panels.
     */
    void registerSettings();
}
