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
package org.exbin.framework.gui.options;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.gui.options.api.OptionsCapable;

/**
 * Interface for application options panels management.
 *
 * @version 0.2.1 2019/07/08
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface OptionsManagement {

    /**
     * Adds options panel.
     *
     * @param optionsPanel options panel
     */
    void addOptionsPanel(OptionsCapable optionsPanel);

    /**
     * Extends main options panel.
     *
     * @param optionsPanel options panel
     */
    void extendMainOptionsPanel(OptionsCapable optionsPanel);

    /**
     * Extends appearance options panel.
     *
     * @param optionsPanel options panel
     */
    void extendAppearanceOptionsPanel(OptionsCapable optionsPanel);

    /**
     * Gets preferences.
     *
     * @return prefereces
     */
    @Nonnull
    Preferences getPreferences();
}
