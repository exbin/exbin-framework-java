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
package org.exbin.framework.utils;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.JTextComponent;

/**
 * Utility static methods usable for windows and dialogs.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class WindowUtils {

    public static final String ESC_CANCEL_KEY = "esc-cancel";
    public static final String ENTER_OK_KEY = "enter-ok";

    private static LookAndFeel lookAndFeel = null;

    private WindowUtils() {
    }

    public static void invokeWindow(final @Nonnull Window window) {
        if (lookAndFeel != null) {
            try {
                javax.swing.UIManager.setLookAndFeel(lookAndFeel);
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(WindowUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        java.awt.EventQueue.invokeLater(() -> {
            if (window instanceof JDialog) {
                ((JDialog) window).setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            }

            window.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            window.setVisible(true);
        });
    }

    public static void invokeWindow(final @Nonnull JComponent component) {
        JDialog dialog = new JDialog();
        Dimension size = component.getPreferredSize();
        if (size.width < 10) {
            size.width = 400;
        }
        if (size.height < 10) {
            size.height = 200;
        }
        dialog.add(component);
        dialog.getContentPane().setPreferredSize(new Dimension(size.width, size.height));
        dialog.pack();
        invokeWindow(dialog);
    }

    @Nullable
    public static LookAndFeel getLookAndFeel() {
        return lookAndFeel;
    }

    public static void setLookAndFeel(LookAndFeel lookAndFeel) {
        WindowUtils.lookAndFeel = lookAndFeel;
    }

    public static void closeWindow(Window window) {
        window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
    }

    @Nonnull
    public static JDialog createBasicDialog() {
        JDialog dialog = new JDialog(new javax.swing.JFrame(), true);
        dialog.setSize(640, 480);
        dialog.setLocationByPlatform(true);
        return dialog;
    }

    @Nullable
    public static Window getWindow(Component component) {
        return SwingUtilities.getWindowAncestor(component);
    }

    /**
     * Assign ESCAPE/ENTER key for all focusable components recursively.
     *
     * @param component target component
     * @param closeButton button which will be used for closing operation
     */
    public static void assignGlobalKeyListener(Component component, final JButton closeButton) {
        assignGlobalKeyListener(component, closeButton, closeButton);
    }

    /**
     * Assign ESCAPE/ENTER key for all focusable components recursively.
     *
     * @param component target component
     * @param okButton button which will be used for default ENTER
     * @param cancelButton button which will be used for closing operation
     */
    public static void assignGlobalKeyListener(Component component, final JButton okButton, final JButton cancelButton) {
        assignGlobalKeyListener(component, new OkCancelControl() {
            @Override
            public void invokeOkEvent() {
                UiUtils.doButtonClick(okButton);
            }

            @Override
            public void invokeCancelEvent() {
                UiUtils.doButtonClick(cancelButton);
            }
        });
    }

    /**
     * Assign ESCAPE/ENTER key for all focusable components recursively.
     *
     * @param component target component
     * @param listener ok and cancel event listener
     */
    public static void assignGlobalKeyListener(Component component, @Nullable final OkCancelControl listener) {
        JRootPane rootPane = SwingUtilities.getRootPane(component);
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ESC_CANCEL_KEY);
        rootPane.getActionMap().put(ESC_CANCEL_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (listener == null) {
                    return;
                }

                boolean performCancelAction = true;

                Window window = SwingUtilities.getWindowAncestor(event.getSource() instanceof JRootPane ? (JRootPane) event.getSource() : rootPane);
                if (window != null) {
                    Component focusOwner = window.getFocusOwner();
                    if (focusOwner instanceof JComboBox) {
                        performCancelAction = !((JComboBox) focusOwner).isPopupVisible();
                    } else if (focusOwner instanceof JRootPane) {
                        // Ignore in popup menus
                        // performCancelAction = false;
                    }
                }

                if (performCancelAction) {
                    listener.invokeCancelEvent();
                }
            }
        });

        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ENTER_OK_KEY);
        rootPane.getActionMap().put(ENTER_OK_KEY, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (listener == null) {
                    return;
                }

                boolean performOkAction = true;

                Window window = SwingUtilities.getWindowAncestor(event.getSource() instanceof JRootPane ? (JRootPane) event.getSource() : rootPane);
                if (window != null) {
                    Component focusOwner = window.getFocusOwner();
                    if (focusOwner instanceof JTextArea || focusOwner instanceof JEditorPane) {
                        performOkAction = !((JTextComponent) focusOwner).isEditable();
                    }
                }

                if (performOkAction) {
                    listener.invokeOkEvent();
                }
            }
        });
    }

    @Nonnull
    public static WindowPosition getWindowPosition(Window window) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screenDevices = ge.getScreenDevices();
        int windowX = window.getX();
        int windowY = window.getY();
        int screenX = 0;
        int screenY = 0;
        int screenWidth = 0;
        int screenHeight = 0;
        int screenIndex = 0;
        for (GraphicsDevice screen : screenDevices) {
            Rectangle bounds = screen.getDefaultConfiguration().getBounds();
            if (bounds.contains(windowX, windowY)) {
                screenX = bounds.x;
                screenY = bounds.y;
                screenWidth = bounds.width;
                screenHeight = bounds.height;
                break;
            }
            screenIndex++;
        }
        WindowPosition position = new WindowPosition();
        position.setScreenIndex(screenIndex);
        position.setScreenWidth(screenWidth);
        position.setScreenHeight(screenHeight);
        position.setRelativeX(window.getX() - screenX);
        position.setRelativeY(window.getY() - screenY);
        position.setWidth(window.getWidth());
        position.setHeight(window.getHeight());
        position.setMaximized(window instanceof Frame ? (((Frame) window).getExtendedState() & JFrame.MAXIMIZED_BOTH) > 0 : false);
        return position;
    }

    public static void setWindowPosition(Window window, WindowPosition position) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screenDevices = ge.getScreenDevices();
        GraphicsDevice device;
        if (screenDevices.length > position.getScreenIndex()) {
            device = screenDevices[position.getScreenIndex()];
        } else {
            device = ge.getDefaultScreenDevice();
        }
        Rectangle screenBounds = device.getDefaultConfiguration().getBounds();
        double absoluteX = position.getScreenWidth() > 0
                ? screenBounds.x + position.getRelativeX() * screenBounds.width / position.getScreenWidth()
                : screenBounds.x + position.getRelativeX();
        double absoluteY = position.getScreenHeight() > 0
                ? screenBounds.y + position.getRelativeY() * screenBounds.height / position.getScreenHeight()
                : screenBounds.y + position.getRelativeY();
        double widthX = position.getScreenWidth() > 0
                ? position.getWidth() * screenBounds.width / position.getScreenWidth()
                : position.getWidth();
        double widthY = position.getScreenHeight() > 0
                ? position.getHeight() * screenBounds.height / position.getScreenHeight()
                : position.getHeight();
        if (position.isMaximized()) {
            window.setLocation((int) absoluteX, (int) absoluteY);
            if (window instanceof Frame) {
                ((Frame) window).setExtendedState(Frame.MAXIMIZED_BOTH);
            } else {
                if (window instanceof Window) {
                    // TODO
//                    final EventQueue eventQueue = AccessController.doPrivileged(
//                            (PrivilegedAction<EventQueue>) Toolkit.getDefaultToolkit()::getSystemEventQueue);
//                    eventQueue.postEvent(new AWTEvent(window, WindowEvent.WINDOW_ICONIFIED) {});
                }
            }
        } else {
            window.setBounds((int) absoluteX, (int) absoluteY, (int) widthX, (int) widthY);
        }
    }
}
