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
package org.exbin.framework.window.api;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;
import org.exbin.framework.window.api.gui.WindowHeaderPanel;

/**
 * Interface for framework window module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface WindowModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(WindowModuleApi.class);

    /**
     * Creates basic dialog and sets it up.
     *
     * @return dialog
     */
    @Nonnull
    WindowHandler createDialog();

    /**
     * Creates basic dialog and sets it up.
     *
     * @param component panel
     * @return dialog
     */
    @Nonnull
    WindowHandler createDialog(@Nullable JComponent component);

    /**
     * Creates basic dialog and sets it up.
     *
     * @param component component
     * @param controlPanel control panel
     * @return dialog
     */
    @Nonnull
    WindowHandler createDialog(@Nullable JComponent component, @Nullable JPanel controlPanel);

    /**
     * Creates basic dialog and sets it up.
     *
     * @param parentComponent parent component
     * @param modalityType modality type
     * @param component panel
     * @return dialog
     */
    @Nonnull
    WindowHandler createDialog(@Nullable Component parentComponent, Dialog.ModalityType modalityType, @Nullable JComponent component);

    /**
     * Creates basic dialog and sets it up.
     *
     * @param parentComponent parent component
     * @param controlPanel control panel
     * @param modalityType modality type
     * @param component panel
     * @return dialog
     */
    @Nonnull
    WindowHandler createDialog(@Nullable Component parentComponent, Dialog.ModalityType modalityType, @Nullable JComponent component, @Nullable JPanel controlPanel);

    @Nonnull
    WindowHeaderPanel addHeaderPanel(@Nonnull Window window, @Nonnull Class<?> resourceClass, @Nonnull ResourceBundle resourceBundle);

    @Nonnull
    WindowHeaderPanel addHeaderPanel(@Nonnull Window window, @Nonnull String headerTitle, @Nonnull String headerDescription, @Nullable Icon headerIcon);

    @Nonnull
    WindowHandler createWindow(final JComponent component, Component parent, String dialogTitle, Dialog.ModalityType modalityType);

    @Nonnull
    JDialog createWindow(final JComponent component);

    void invokeWindow(final JComponent component);

    @Nonnull
    JPanel createDialogPanel(JComponent mainComponent, JPanel controlPanel);

    void setWindowTitle(WindowHandler windowHandler, ResourceBundle resourceBundle);
}
