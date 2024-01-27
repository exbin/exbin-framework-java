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
package org.exbin.framework.window;

import com.formdev.flatlaf.extras.FlatDesktop;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import org.exbin.framework.App;
import org.exbin.framework.window.api.ApplicationExitListener;
import org.exbin.framework.window.api.ApplicationFrameHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.preferences.api.Preferences;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.WindowPosition;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.window.action.FrameActions;
import org.exbin.framework.utils.DesktopUtils;
import org.exbin.framework.utils.OkCancelListener;
import org.exbin.framework.utils.UiUtils;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.gui.WindowHeaderPanel;
import org.exbin.framework.window.api.handler.OkCancelService;

/**
 * Module window handling.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WindowModule implements WindowModuleApi {

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
    private ApplicationExitHandler exitHandler = null;
    private StatusBarHandler statusBarHandler = null;
    private FrameActions frameActions;

    public WindowModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(WindowModule.class);
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
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
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
        ActionModuleApi menuModule = App.getModule(ActionModuleApi.class);
        menuModule.registerToolBar(MAIN_TOOL_BAR_ID, MODULE_ID);
    }

    @Nonnull
    @Override
    public WindowHeaderPanel addHeaderPanel(@Nonnull Window window, @Nonnull Class<?> resourceClass, @Nonnull ResourceBundle resourceBundle) {
        URL iconUrl = resourceClass.getResource(resourceBundle.getString("header.icon"));
        Icon headerIcon = iconUrl != null ? new ImageIcon(iconUrl) : null;
        return addHeaderPanel(window, resourceBundle.getString("header.title"), resourceBundle.getString("header.description"), headerIcon);
    }

    @Nonnull
    @Override
    public WindowHeaderPanel addHeaderPanel(@Nonnull Window window, @Nonnull String headerTitle, @Nonnull String headerDescription, @Nullable Icon headerIcon) {
        WindowHeaderPanel headerPanel = new WindowHeaderPanel();
        headerPanel.setTitle(headerTitle);
        headerPanel.setDescription(headerDescription);
        if (headerIcon != null) {
            headerPanel.setIcon(headerIcon);
        }
        if (window instanceof WindowHeaderPanel.WindowHeaderDecorationProvider) {
            ((WindowHeaderPanel.WindowHeaderDecorationProvider) window).setHeaderDecoration(headerPanel);
        } else {
            Frame frame = UiUtils.getFrame(window);
            if (frame instanceof WindowHeaderPanel.WindowHeaderDecorationProvider) {
                ((WindowHeaderPanel.WindowHeaderDecorationProvider) frame).setHeaderDecoration(headerPanel);
            }
        }
        int height = window.getHeight() + headerPanel.getPreferredSize().height;
        ((JDialog) window).getContentPane().add(headerPanel, java.awt.BorderLayout.PAGE_START);
        window.setSize(window.getWidth(), height);
        return headerPanel;
    }

    @Nonnull
    @Override
    public WindowHandler createWindow(final JComponent component, Component parent, String dialogTitle, Dialog.ModalityType modalityType) {
        final JDialog dialog = new JDialog(WindowUtils.getWindow(parent), modalityType);
        Dimension size = component.getPreferredSize();
        dialog.add(component);
        dialog.getContentPane().setPreferredSize(new Dimension(size.width, size.height));
        dialog.pack();
        dialog.setTitle(dialogTitle);
        if (component instanceof OkCancelService) {
            WindowUtils.assignGlobalKeyListener(dialog, ((OkCancelService) component).getOkCancelListener());
        }
        return new WindowHandler() {
            @Override
            public void show() {
                dialog.setVisible(true);
            }

            @Override
            public void showCentered(@Nullable Component component) {
                center(component);
                show();
            }

            @Override
            public void close() {
                WindowUtils.closeWindow(dialog);
            }

            @Override
            public void dispose() {
                dialog.dispose();
            }

            @Override
            public Window getWindow() {
                return dialog;
            }

            @Override
            public Container getParent() {
                return dialog.getParent();
            }

            @Override
            public void center(@Nullable Component component) {
                if (component == null) {
                    center();
                } else {
                    dialog.setLocationRelativeTo(component);
                }
            }

            @Override
            public void center() {
                dialog.setLocationByPlatform(true);
            }
        };
    }

    @Nonnull
    @Override
    public JDialog createWindow(final JComponent component) {
        JDialog dialog = new JDialog();
        Dimension size = component.getPreferredSize();
        dialog.add(component);
        dialog.getContentPane().setPreferredSize(new Dimension(size.width, size.height));
        dialog.pack();
        if (component instanceof OkCancelService) {
            WindowUtils.assignGlobalKeyListener(dialog, ((OkCancelService) component).getOkCancelListener());
        }
        return dialog;
    }

    @Override
    public void invokeWindow(final JComponent component) {
        JDialog dialog = createWindow(component);
        WindowUtils.invokeWindow(dialog);
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

    static private void saveFramePositionToPreferences(WindowPosition windowPosition, Preferences pref, String prefix) {
        pref.putInt(prefix + PREFERENCES_SCREEN_INDEX, windowPosition.getScreenIndex());
        pref.putInt(prefix + PREFERENCES_SCREEN_WIDTH, windowPosition.getScreenWidth());
        pref.putInt(prefix + PREFERENCES_SCREEN_HEIGHT, windowPosition.getScreenHeight());
        pref.putInt(prefix + PREFERENCES_POSITION_X, windowPosition.getRelativeX());
        pref.putInt(prefix + PREFERENCES_POSITION_Y, windowPosition.getRelativeY());
        pref.putInt(prefix + PREFERENCES_WIDTH, windowPosition.getWidth());
        pref.putInt(prefix + PREFERENCES_HEIGHT, windowPosition.getHeight());
        pref.putBoolean(prefix + PREFERENCES_MAXIMIZED, windowPosition.isMaximized());
    }

    static private void loadFramePositionFromPreferences(WindowPosition windowPosition, Preferences pref, String prefix) {
        windowPosition.setScreenIndex(pref.getInt(prefix + PREFERENCES_SCREEN_INDEX, 0));
        windowPosition.setScreenWidth(pref.getInt(prefix + PREFERENCES_SCREEN_WIDTH, 0));
        windowPosition.setScreenHeight(pref.getInt(prefix + PREFERENCES_SCREEN_HEIGHT, 0));
        windowPosition.setRelativeX(pref.getInt(prefix + PREFERENCES_POSITION_X, 0));
        windowPosition.setRelativeY(pref.getInt(prefix + PREFERENCES_POSITION_Y, 0));
        windowPosition.setWidth(pref.getInt(prefix + PREFERENCES_WIDTH, 0));
        windowPosition.setHeight(pref.getInt(prefix + PREFERENCES_HEIGHT, 0));
        windowPosition.setMaximized(pref.getBoolean(prefix + PREFERENCES_MAXIMIZED, false));
    }

    static private boolean preferencesFramePositionExists(Preferences pref, String prefix) {
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
        ActionUtils.setupAction(exitAction, resourceBundle, "exitAction");
        exitAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.KeyEvent.ALT_DOWN_MASK));

        return exitAction;
    }

    @Override
    public void registerExitAction() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        String appClosingActionsGroup = "ApplicationClosingActionsGroup";
        boolean exitActionRegistered = false;

        if (DesktopUtils.detectBasicOs() == DesktopUtils.DesktopOs.MAC_OS) {
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

        actionModule.registerMenuGroup(WindowModuleApi.FILE_MENU_ID, new MenuGroup(appClosingActionsGroup, new MenuPosition(PositionMode.BOTTOM_LAST), exitActionRegistered ? SeparationMode.NONE : SeparationMode.ABOVE));
        if (!exitActionRegistered) {
            actionModule.registerMenuItem(WindowModuleApi.FILE_MENU_ID, MODULE_ID, getExitAction(), new MenuPosition(appClosingActionsGroup));
        }
    }

    @Nonnull
    @Override
    public ApplicationFrameHandler getFrameHandler() {
        if (applicationFrame == null) {
            applicationFrame = new ApplicationFrame();
            applicationFrame.initApplication();
            applicationFrame.loadMainMenu();
            applicationFrame.loadMainToolBar();
            applicationFrame.setApplicationExitHandler(exitHandler);

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
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        getFrameActions();
        createViewBarsMenuGroup();
        actionModule.registerMenuItem(WindowModuleApi.VIEW_MENU_ID, MODULE_ID, frameActions.getViewToolBarAction(), new MenuPosition(VIEW_BARS_GROUP_ID));
        actionModule.registerMenuItem(WindowModuleApi.VIEW_MENU_ID, MODULE_ID, frameActions.getViewToolBarCaptionsAction(), new MenuPosition(VIEW_BARS_GROUP_ID));
    }

    @Override
    public void registerStatusBarVisibilityActions() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        getFrameActions();
        createViewBarsMenuGroup();
        actionModule.registerMenuItem(WindowModuleApi.VIEW_MENU_ID, MODULE_ID, frameActions.getViewStatusBarAction(), new MenuPosition(VIEW_BARS_GROUP_ID));
    }

    private void createViewBarsMenuGroup() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        if (!actionModule.menuGroupExists(WindowModuleApi.VIEW_MENU_ID, VIEW_BARS_GROUP_ID)) {
            actionModule.registerMenuGroup(WindowModuleApi.VIEW_MENU_ID, new MenuGroup(VIEW_BARS_GROUP_ID, new MenuPosition(PositionMode.TOP), SeparationMode.BELOW));
        }
    }

    @Nonnull
    @Override
    public WindowHandler createDialog() {
        return createDialog(null, null);
    }

    @Nonnull
    @Override
    public WindowHandler createDialog(@Nullable JComponent component) {
        return createDialog(getFrame(), Dialog.ModalityType.APPLICATION_MODAL, component, null);
    }

    @Nonnull
    @Override
    public WindowHandler createDialog(@Nullable JComponent component, @Nullable JPanel controlPanel) {
        return createDialog(getFrame(), Dialog.ModalityType.APPLICATION_MODAL, component, controlPanel);
    }

    @Nonnull
    @Override
    public WindowHandler createDialog(Component parentComponent, Dialog.ModalityType modalityType, @Nullable JComponent component) {
        return createDialog(parentComponent, modalityType, component, null);
    }

    @Nonnull
    @Override
    public WindowHandler createDialog(Component parentComponent, Dialog.ModalityType modalityType, @Nullable JComponent component, @Nullable JPanel controlPanel) {
        JComponent dialogComponent = controlPanel != null ? createDialogPanel(component, controlPanel) : component;

        WindowHandler dialog = createWindow(dialogComponent, parentComponent, "", modalityType);
        Optional<Image> applicationIcon = Optional.empty(); // TODO application.getApplicationIcon();
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
    public void setWindowTitle(WindowHandler windowWrapper, ResourceBundle resourceBundle) {
        ((JDialog) windowWrapper.getWindow()).setTitle(resourceBundle.getString(RESOURCES_DIALOG_TITLE));
    }

    /**
     * Creates panel for given main and control panel.
     *
     * @param mainComponent main panel
     * @param controlPanel control panel
     * @return panel
     */
    @Nonnull
    @Override
    public JPanel createDialogPanel(JComponent mainComponent, JPanel controlPanel) {
        JPanel dialogPanel;
        if (controlPanel instanceof OkCancelService) {
            dialogPanel = new DialogPanel((OkCancelService) controlPanel);
        } else {
            dialogPanel = new JPanel(new BorderLayout());
        }
        dialogPanel.add(mainComponent, BorderLayout.CENTER);
        dialogPanel.add(controlPanel, BorderLayout.SOUTH);
        Dimension mainPreferredSize = mainComponent.getPreferredSize();
        Dimension controlPreferredSize = controlPanel.getPreferredSize();
        int height = mainPreferredSize.height + (controlPreferredSize != null ? controlPreferredSize.height : 0);
        dialogPanel.setPreferredSize(new Dimension(mainPreferredSize.width, height));
        return dialogPanel;
    }

    @ParametersAreNonnullByDefault
    private static final class DialogPanel extends JPanel implements OkCancelService {

        private final OkCancelService okCancelService;

        public DialogPanel(OkCancelService okCancelService) {
            super(new BorderLayout());
            this.okCancelService = okCancelService;
        }

        @Nullable
        @Override
        public JButton getDefaultButton() {
            return null;
        }

        @Nonnull
        @Override
        public OkCancelListener getOkCancelListener() {
            return okCancelService.getOkCancelListener();
        }
    }
}
