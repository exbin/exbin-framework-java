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
package org.exbin.framework.gui.options.api;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.gui.utils.ComponentResourceProvider;

/**
 * Interface for basic options panels.
 *
 * @version 0.2.1 2019/07/13
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface OptionsCapable extends ComponentResourceProvider {

    /**
     * Inicializes configuration from given properties.
     */
    void applyPreferencesChanges();

    /**
     * Loads configuration from given properties.
     *
     * @param preferences preferences
     */
    void loadFromPreferences(Preferences preferences);

    /**
     * Saves configuration from given properties.
     *
     * @param preferences preferences
     */
    void saveToPreferences(Preferences preferences);

    /**
     * Registers listener for changes monitoring.
     *
     * @param listener modified options listener
     */
    void setOptionsModifiedListener(OptionsModifiedListener listener);
}
