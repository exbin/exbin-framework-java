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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import org.exbin.framework.App;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.utils.WindowUtils;
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
    private boolean hideHeaderPanels = false;

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

    @Nonnull
    @Override
    public JPanel addHeaderPanel(Window window, Class<?> resourceClass, ResourceBundle resourceBundle) {
        if (hideHeaderPanels) {
            return new JPanel();
        }

        URL iconUrl = resourceClass.getResource(resourceBundle.getString("header.icon"));
        Icon headerIcon = iconUrl != null ? new ImageIcon(iconUrl) : null;
        return addHeaderPanel(window, resourceBundle.getString("header.title"), resourceBundle.getString("header.description"), headerIcon);
    }

    @Nonnull
    @Override
    public JPanel addHeaderPanel(Window window, String headerTitle, String headerDescription, @Nullable Icon headerIcon) {
        if (hideHeaderPanels) {
            return new JPanel();
        }

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

    @Override
    public void setHideHeaderPanels(boolean hide) {
        this.hideHeaderPanels = hide;
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
            WindowUtils.assignGlobalKeyListener(dialog, (OkCancelService) component);
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

            @Nonnull
            @Override
            public Window getWindow() {
                return dialog;
            }

            @Nonnull
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
            WindowUtils.assignGlobalKeyListener(dialog, (OkCancelService) component);
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
    public WindowHandler createDialog() {
        return createDialog(null, null);
    }

    @Nonnull
    @Override
    public WindowHandler createDialog(@Nullable JComponent component) {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        // TODO Replace when adding support for multiple frames
        return createDialog(frameModule.getFrame(), Dialog.ModalityType.APPLICATION_MODAL, component, null);
    }

    @Nonnull
    @Override
    public WindowHandler createDialog(@Nullable JComponent component, @Nullable JPanel controlPanel) {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        return createDialog(frameModule.getFrame(), Dialog.ModalityType.APPLICATION_MODAL, component, controlPanel);
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

        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        WindowHandler dialog = createWindow(dialogComponent, parentComponent, "", modalityType);
        Optional<Image> applicationIcon = frameModule.getApplicationIcon();
        if (applicationIcon.isPresent()) {
            ((JDialog) dialog.getWindow()).setIconImage(applicationIcon.get());
        }
        if (controlPanel instanceof OkCancelService) {
            Optional<JButton> defaultButton = ((OkCancelService) controlPanel).getDefaultButton();
            if (defaultButton.isPresent()) {
                JRootPane rootPane = SwingUtilities.getRootPane(dialog.getWindow());
                if (rootPane != null) {
                    rootPane.setDefaultButton(defaultButton.get());
                }
            }
        }
        return dialog;
    }

    @Override
    public void setWindowTitle(WindowHandler windowWrapper, ResourceBundle resourceBundle) {
        ((JDialog) windowWrapper.getWindow()).setTitle(resourceBundle.getString(RESOURCES_DIALOG_TITLE));
    }

//    /**
//     * Sets window header decoration provider.
//     *
//     * @param windowHeaderDecorationProvider window header decoration provider
//     */
//    void setWindowHeaderDecorationProvider(WindowHeaderPanel.WindowHeaderDecorationProvider windowHeaderDecorationProvider);

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

        @Nonnull
        @Override
        public Optional<JButton> getDefaultButton() {
            return Optional.empty();
        }

        @Override
        public void invokeOkEvent() {
            okCancelService.invokeOkEvent();
        }

        @Override
        public void invokeCancelEvent() {
            okCancelService.invokeCancelEvent();
        }
    }
}
