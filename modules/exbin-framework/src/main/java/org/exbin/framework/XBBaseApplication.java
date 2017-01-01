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
package org.exbin.framework;

import java.awt.Image;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModuleRepository;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Base application class.
 *
 * @version 0.2.0 2016/12/03
 * @author ExBin Project (http://exbin.org)
 */
public class XBBaseApplication implements XBApplication {

    public static final String PREFERENCES_LOOK_AND_FEEL = "lookAndFeel";
    public static final String PREFERENCES_LOCALE_LANGUAGE = "locale.language";
    public static final String PREFERENCES_LOCALE_COUNTRY = "locale.country";
    public static final String PREFERENCES_LOCALE_VARIANT = "locale.variant";

    private ResourceBundle appBundle;
    private Preferences appPreferences;

    private final XBDefaultApplicationModuleRepository moduleRepository;
    private final List<URI> plugins = new ArrayList<>();
    private final Map<Locale, ClassLoader> languagePlugins = new HashMap<>();
    private LookAndFeel defaultLaf = null;

    public XBBaseApplication() {
        moduleRepository = new XBDefaultApplicationModuleRepository(this);
    }

    public void init() {
        // Setup language utility
        Locale locale = Locale.getDefault();
        defaultLaf = UIManager.getLookAndFeel();
        ClassLoader languageClassLoader = languagePlugins.get(locale);
        if (languageClassLoader != null) {
            LanguageUtils.setLanguageClassLoader(languageClassLoader);
        }
    }

    @Override
    public String preferencesGet(String key, String def) {
        if (getAppPreferences() == null) {
            return def;
        }

        return getAppPreferences().get(key, def);
    }

    @Override
    public ResourceBundle getAppBundle() {
        return appBundle;
    }

    /**
     * Sets application resource bundle handler.
     *
     * @param appBundle application resource bundle
     * @param bundleName this is workaround for getBaseBundleName method
     * available only in Java 1.8
     */
    public void setAppBundle(ResourceBundle appBundle, String bundleName) {
        if (!Locale.getDefault().equals(appBundle.getLocale())) {
            appBundle = ResourceBundle.getBundle(bundleName);
        }

        this.appBundle = appBundle;
    }

    public Preferences createPreferences(Class clazz) {
        Preferences preferences;
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("win")) {
            preferences = new FilePreferencesFactory().userNodeForPackage(clazz);
        } else {
            preferences = Preferences.userNodeForPackage(clazz);
        }
        setAppPreferences(preferences);
        return preferences;
    }

    @Override
    public Preferences getAppPreferences() {
        return appPreferences;
    }

    public void setAppPreferences(Preferences appPreferences) {
        this.appPreferences = appPreferences;

        // Switching language
        String localeLanguage = preferencesGet(PREFERENCES_LOCALE_LANGUAGE, Locale.US.getLanguage());
        String localeCountry = preferencesGet(PREFERENCES_LOCALE_COUNTRY, Locale.US.getCountry());
        String localeVariant = preferencesGet(PREFERENCES_LOCALE_VARIANT, Locale.US.getVariant());
        try {
            Locale locale = new Locale(localeLanguage, localeCountry, localeVariant);
            if (!locale.equals(Locale.ROOT)) {
                Locale.setDefault(locale);
            }
        } catch (SecurityException ex) {
            // Ignore it in java webstart
        }

        String laf = preferencesGet(PREFERENCES_LOOK_AND_FEEL, "");
        try {
            if (laf == null || laf.isEmpty()) {
                String osName = System.getProperty("os.name").toLowerCase();
                if (!osName.startsWith("windows") && !osName.startsWith("mac")) {
                    // Try "GTK+" on linux
                    try {
                        UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                        laf = UIManager.getSystemLookAndFeelClassName();
                    }
                } else {
                    laf = UIManager.getSystemLookAndFeelClassName();
                }
            }

            if (laf != null && !laf.isEmpty()) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(XBBaseApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Adds plugin to the list of plugins.
     *
     * @param uri URI to plugin.
     */
    public void loadPlugin(URI uri) {
        if (!plugins.add(uri)) {
            throw new RuntimeException("Unable to load plugin: " + uri.toString());
        }

        getModuleRepository().addModulesFrom(uri);
    }

    public void loadPlugin(String jarFilePath) {
        try {
            loadPlugin(new URI(jarFilePath));
        } catch (URISyntaxException ex) {
            // ignore
        }
    }

    public void loadClassPathPlugins(Class targetClass) {
        try {
            Manifest manifest = new Manifest(targetClass.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF"));
            Attributes classPaths = manifest.getAttributes("Class-Path");
            Collection<Object> values = classPaths.values();
            for (Object classPath : values) {
                if (classPath instanceof String) {
                    XBBaseApplication.this.loadPlugin((String) classPath);
                }
            }
        } catch (IOException ex) {
            // ignore
        }
    }

    @Override
    public Image getApplicationIcon() {
        return new ImageIcon(getClass().getResource(getAppBundle().getString("Application.icon"))).getImage();
    }

    @Override
    public XBApplicationModuleRepository getModuleRepository() {
        return moduleRepository;
    }

    @Override
    public void registerLanguagePlugin(Locale locale, ClassLoader classLoader) {
        languagePlugins.put(locale, classLoader);
    }

    @Override
    public Set<Locale> getLanguageLocales() {
        return languagePlugins.keySet();
    }
}
