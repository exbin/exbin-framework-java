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
package org.exbin.framework.api;

import java.awt.Image;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.Preferences;
import org.exbin.xbup.plugin.XBModuleHandler;

/**
 * Interface for application module management.
 *
 * @version 0.2.0 2016/05/19
 * @author ExBin Project (http://exbin.org)
 */
public interface XBApplication extends XBModuleHandler {

    /**
     * Gets application bundle.
     *
     * @return the appBundle
     */
    ResourceBundle getAppBundle();

    /**
     * Gets application preferences.
     *
     * @return the appPreferences
     */
    Preferences getAppPreferences();

    /**
     * Gets modules repository.
     *
     * @return the moduleRepository
     */
    @Override
    XBApplicationModuleRepository getModuleRepository();

    /**
     * Gets preferences key value.
     *
     * @param key key
     * @param def default value
     * @return value
     */
    String preferencesGet(String key, String def);

    /**
     * Gets application icon.
     *
     * @return application icon image
     */
    Image getApplicationIcon();

    /**
     * Registers locale and class loader which should be used to load resources
     * for it.
     *
     * @param locale language locale
     * @param classLoader class loader
     */
    void registerLanguagePlugin(Locale locale, ClassLoader classLoader);

    /**
     * Returns set of registered locales.
     *
     * @return set of locales
     */
    Set<Locale> getLanguageLocales();
}
