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

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Utility static methods usable for UI.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class UiUtils {

    private static final int BUTTON_CLICK_TIME = 150;
    private static MenuBuilder menuBuilder = null;

    private UiUtils() {
    }

    /**
     * Invokes code in UI thread.
     *
     * @param runnable runnable code
     */
    public static void runInUiThread(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
            return;
        }

        try {
            SwingUtilities.invokeAndWait(runnable);
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(UiUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Shedules code to run in UI thread unless it's executed via UI thread.
     *
     * @param runnable runnable code
     */
    public static void invokeInUiThread(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
            return;
        }

        SwingUtilities.invokeLater(runnable);
    }

    /**
     * Creates new instance of given class using UI thread.
     *
     * @param <T> class type
     * @param clazz class type
     * @return class instance
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> T createInUiThread(Class<T> clazz) {
        if (SwingUtilities.isEventDispatchThread()) {
            try {
                return (T) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        try {
            final Object[] result = new Object[1];
            SwingUtilities.invokeAndWait(() -> {
                try {
                    result[0] = (T) clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            });
            return (T) result[0];
        } catch (InterruptedException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates new instance of given class using UI thread.
     *
     * @param <T> class type
     * @param instanceCreator instance creator
     * @return class instance
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> T createInUiThread(InstanceCreator<T> instanceCreator) {
        if (SwingUtilities.isEventDispatchThread()) {
            return instanceCreator.createInstance();
        }

        try {
            final Object[] result = new Object[1];
            SwingUtilities.invokeAndWait(() -> {
                result[0] = (T) instanceCreator.createInstance();
            });
            return (T) result[0];
        } catch (InterruptedException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Detects dark mode.
     *
     * @return true if dark mode assumed
     */
    public static boolean isDarkUI() {
        Color backgroundColor = UIManager.getColor("TextArea.background");
        if (backgroundColor == null) {
            return false;
        }

        int medium = (backgroundColor.getRed() + backgroundColor.getBlue() + backgroundColor.getGreen()) / 3;
        return medium < 96;
    }

    /**
     * Creates new instance of menu.
     *
     * @return new instance of menu
     */
    @Nonnull
    public static JMenu createMenu() {
        if (menuBuilder != null) {
            return menuBuilder.buildMenu();
        }

        return new JMenu();
    }

    /**
     * Creates new instance of popup menu.
     *
     * @return new instance of popup menu
     */
    @Nonnull
    public static JPopupMenu createPopupMenu() {
        if (menuBuilder != null) {
            return menuBuilder.buildPopupMenu();
        }

        return new JPopupMenu();
    }

    /**
     * Creates new instance of popup menu.
     *
     * @param showMethod show method
     * @return new instance of popup menu
     */
    @Nonnull
    public static JPopupMenu createPopupMenu(MenuShowMethod showMethod) {
        if (menuBuilder != null) {
            return menuBuilder.buildPopupMenu();
        }

        return new JPopupMenu() {
            @Override
            public void show(Component invoker, int x, int y) {
                showMethod.show(invoker, x, y);
            }
        };
    }

    /**
     * Creates new instance of menu item.
     *
     * @return new instance of menu item
     */
    @Nonnull
    public static JMenuItem createMenuItem() {
        if (menuBuilder != null) {
            return menuBuilder.buildMenuItem();
        }

        return new JMenuItem();
    }

    /**
     * Creates new instance of check box menu item.
     *
     * @return new instance of check box menu item
     */
    @Nonnull
    public static JCheckBoxMenuItem createCheckBoxMenuItem() {
        if (menuBuilder != null) {
            return menuBuilder.buildCheckBoxMenuItem();
        }

        return new JCheckBoxMenuItem();
    }

    /**
     * Creates new instance of radio button menu item.
     *
     * @return new instance of radio button menu item
     */
    @Nonnull
    public static JRadioButtonMenuItem createRadioButtonMenuItem() {
        if (menuBuilder != null) {
            return menuBuilder.buildRadioButtonMenuItem();
        }

        return new JRadioButtonMenuItem();
    }

    /**
     * Returns current popup menu builder.
     *
     * @return popup menu builder
     */
    @Nullable
    public static MenuBuilder getMenuBuilder() {
        return menuBuilder;
    }

    /**
     * Sets popup menu builder.
     *
     * @param menuBuilder popup menu builder
     */
    public static void setMenuBuilder(@Nullable MenuBuilder menuBuilder) {
        UiUtils.menuBuilder = menuBuilder;
    }

    /**
     * Finds frame component for given component.
     *
     * @param component instantiated component
     * @return frame instance if found
     */
    @Nullable
    public static Frame getFrame(Component component) {
        Window parentComponent = SwingUtilities.getWindowAncestor(component);
        while (!(parentComponent == null || parentComponent instanceof Frame)) {
            parentComponent = SwingUtilities.getWindowAncestor(parentComponent);
        }
        if (parentComponent == null) {
            parentComponent = JOptionPane.getRootFrame();
        }
        return (Frame) parentComponent;
    }

    /**
     * Performs visually visible click on the button component.
     *
     * @param button button component
     */
    public static void doButtonClick(JButton button) {
        button.doClick(BUTTON_CLICK_TIME);
    }

    public static void waitForUiThread() {
        Thread[] uiThread = new Thread[1];
        try {
            SwingUtilities.invokeAndWait(() -> {
                uiThread[0] = Thread.currentThread();
            });
            uiThread[0].join();
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(UiUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public interface MenuShowMethod {

        void show(@Nullable Component invoker, int x, int y);
    }

    public interface MenuBuilder {

        @Nonnull
        JMenu buildMenu();

        @Nonnull
        JPopupMenu buildPopupMenu();

        @Nonnull
        JMenuItem buildMenuItem();

        @Nonnull
        JCheckBoxMenuItem buildCheckBoxMenuItem();

        @Nonnull
        JRadioButtonMenuItem buildRadioButtonMenuItem();
    }

    public interface InstanceCreator<U> {

        @Nonnull
        U createInstance();
    }
}
