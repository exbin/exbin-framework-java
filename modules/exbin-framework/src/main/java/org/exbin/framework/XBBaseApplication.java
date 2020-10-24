/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework;

import org.exbin.framework.preferences.PreferencesWrapper;
import org.exbin.framework.preferences.FilePreferencesFactory;
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
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ImageIcon;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.exbin.xbup.plugin.LookAndFeelApplier;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.api.XBApplicationModuleRepository;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.preferences.FrameworkPreferences;

/**
 * Base application class.
 *
 * @version 0.2.1 2020/09/26
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class XBBaseApplication implements XBApplication {

    private ResourceBundle appBundle;
    private Preferences appPreferences;

    private final XBDefaultApplicationModuleRepository moduleRepository;
    private final List<URI> plugins = new ArrayList<>();
    private final Map<Locale, ClassLoader> languagePlugins = new HashMap<>();
    private final Map<String, LookAndFeelApplier> lafPlugins = new HashMap<>();
    private LookAndFeel defaultLaf = null;
    private String targetLaf = null;

    public XBBaseApplication() {
        moduleRepository = new XBDefaultApplicationModuleRepository(this);
    }

    public void init() {
        // Setup language utility
        Locale locale = Locale.getDefault();
        ClassLoader languageClassLoader = languagePlugins.get(locale);
        if (languageClassLoader != null) {
            LanguageUtils.setLanguageClassLoader(languageClassLoader);
        }
        if (targetLaf != null) {
            applyLookAndFeel(targetLaf);
        }
    }

    @Nonnull
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

    @Nonnull
    public Preferences createPreferences(Class clazz) {
        java.util.prefs.Preferences prefsPreferences;
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("win")) {
            prefsPreferences = new FilePreferencesFactory().userNodeForPackage(clazz);
        } else {
            prefsPreferences = java.util.prefs.Preferences.userNodeForPackage(clazz);
        }
        PreferencesWrapper wrapper = new PreferencesWrapper(prefsPreferences);
        setAppPreferences(wrapper);
        return wrapper;
    }

    @Nonnull
    @Override
    public Preferences getAppPreferences() {
        return appPreferences;
    }

    public void setAppPreferences(Preferences appPreferences) {
        this.appPreferences = appPreferences;
        initByPreferences();
    }

    private void initByPreferences() {
        FrameworkPreferences frameworkParameters = new FrameworkPreferences(appPreferences);

        // Switching language
        Locale locale = frameworkParameters.getLocale();
        if (!locale.equals(Locale.ROOT)) {
            Locale.setDefault(locale);
        }

        defaultLaf = UIManager.getLookAndFeel();
        targetLaf = frameworkParameters.getLookAndFeel();
    }

    @Override
    public void applyLookAndFeel(String laf) {
        try {
            if (laf.isEmpty()) {
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
                LookAndFeelApplier applier = lafPlugins.get(laf);
                if (applier != null) {
                    applier.applyLookAndFeel(laf);
                } else {
                    UIManager.setLookAndFeel(laf);
                }
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
            values.stream().filter((classPath) -> (classPath instanceof String)).forEachOrdered((classPath) -> {
                XBBaseApplication.this.loadPlugin((String) classPath);
            });
        } catch (IOException ex) {
            // ignore
        }
    }

    @Nonnull
    @Override
    public Image getApplicationIcon() {
        return new ImageIcon(getClass().getResource(getAppBundle().getString("Application.icon"))).getImage();
    }

    @Nonnull
    @Override
    public XBApplicationModuleRepository getModuleRepository() {
        return moduleRepository;
    }

    @Override
    public void registerLanguagePlugin(Locale locale, ClassLoader classLoader) {
        languagePlugins.put(locale, classLoader);
    }

    @Override
    public void registerLafPlugin(String className, LookAndFeelApplier applier) {
        lafPlugins.put(className, applier);
        if (className.equals(targetLaf)) {
            applyLookAndFeel(targetLaf);
            targetLaf = null;
        }
    }

    @Nonnull
    @Override
    public Set<Locale> getLanguageLocales() {
        return languagePlugins.keySet();
    }
}
