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
package org.exbin.framework.options;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import org.exbin.framework.api.LanguageProvider;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.options.api.OptionsPage;
import org.exbin.framework.options.gui.MainOptionsPanel;
import org.exbin.framework.options.model.LanguageRecord;
import org.exbin.framework.options.options.impl.FrameworkOptionsImpl;
import org.exbin.framework.preferences.FrameworkPreferences;
import org.exbin.framework.utils.DesktopUtils;
import org.exbin.framework.utils.LanguageUtils;

/**
 * Interface for application options panels management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MainOptionsManager {

    private final ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(MainOptionsManager.class);

    private XBApplication application;

    private MainOptionsPanel mainOptionsPanel;

    private List<String> themes;
    private List<String> themeNames;
    private List<LanguageRecord> languageLocales = null;
    private List<GuiRenderingMethod> renderingMethods;
    private List<String> renderingMethodNames;
    private List<GuiFontAntialiasing> fontAntialiasings;
    private List<String> fontAntialiasingNames;
    private List<GuiScaling> guiScalings;
    private List<String> guiScalingNames;
    private List<GuiMacOsAppearance> guiMacOsAppearances;
    private List<String> guiMacOsAppearanceNames;

    public MainOptionsManager() {
    }

    @Nonnull
    public MainOptionsPanel getMainOptionsPanel() {
        if (mainOptionsPanel == null) {
            mainOptionsPanel = new MainOptionsPanel();

            themes = new ArrayList<>();
            themes.add("");
            boolean extraCrossPlatformLAF = !"javax.swing.plaf.metal.MetalLookAndFeel".equals(UIManager.getCrossPlatformLookAndFeelClassName());
            if (extraCrossPlatformLAF) {
                themes.add(UIManager.getCrossPlatformLookAndFeelClassName());
            }
            themes.add("javax.swing.plaf.metal.MetalLookAndFeel");
            themes.add("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            themeNames = new ArrayList<>();
            themeNames.add(resourceBundle.getString("theme.defaultTheme"));
            if (extraCrossPlatformLAF) {
                themeNames.add(resourceBundle.getString("theme.crossPlatformTheme"));
            }
            themeNames.add("Metal");
            themeNames.add("Motif");
            UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
            for (UIManager.LookAndFeelInfo lookAndFeelInfo : infos) {
                if (!themes.contains(lookAndFeelInfo.getClassName())) {
                    themes.add(lookAndFeelInfo.getClassName());
                    themeNames.add(lookAndFeelInfo.getName());
                }
            }

            mainOptionsPanel.setThemes(themes, themeNames);

            languageLocales = new ArrayList<>();
            languageLocales.add(new LanguageRecord(Locale.ROOT, null));
            languageLocales.add(new LanguageRecord(new Locale("en", "US"), new ImageIcon(getClass().getResource(resourceBundle.getString("locale.englishFlag")))));
            for (LanguageProvider languageProvider : application.getLanguagePlugins()) {
                ImageIcon flag = null;
                try {
                    flag = languageProvider.getFlag().orElse(null);
                } catch (Throwable ex) {
                    Logger.getLogger(MainOptionsManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                languageLocales.add(new LanguageRecord(languageProvider.getLocale(), flag, null));
            }
            mainOptionsPanel.setDefaultLocaleName("<" + resourceBundle.getString("locale.defaultLanguage") + ">");
            mainOptionsPanel.setLanguageLocales(languageLocales);

            renderingMethods = GuiRenderingMethod.getAvailableMethods();
            List<String> renderingMethodKeys = new ArrayList<>();
            renderingMethodNames = new ArrayList<>();
            DesktopUtils.DesktopOs desktopOs = DesktopUtils.detectBasicOs();
            for (GuiRenderingMethod renderingMethod : renderingMethods) {
                renderingMethodKeys.add(renderingMethod.getPropertyValue());
                if (renderingMethod == GuiRenderingMethod.DEFAULT) {
                    renderingMethodNames.add(resourceBundle.getString("renderingMethod.default"));
                } else {
                    if (renderingMethod == GuiRenderingMethod.SOFTWARE && desktopOs == DesktopUtils.DesktopOs.WINDOWS) {
                        renderingMethodNames.add(resourceBundle.getString("renderingMethod.software.windows"));
                    } else {
                        String propertyValue = renderingMethod.getPropertyValue();
                        renderingMethodNames.add(resourceBundle.getString("renderingMethod." + propertyValue));
                    }
                }
            }
            mainOptionsPanel.setRenderingModes(renderingMethodKeys, renderingMethodNames);

            fontAntialiasings = GuiFontAntialiasing.getAvailable();
            List<String> macOsAppearancesKeys = new ArrayList<>();
            fontAntialiasingNames = new ArrayList<>();
            for (GuiFontAntialiasing fontAntialiasing : fontAntialiasings) {
                macOsAppearancesKeys.add(fontAntialiasing.getPropertyValue());
                if (fontAntialiasing == GuiFontAntialiasing.DEFAULT) {
                    fontAntialiasingNames.add(resourceBundle.getString("fontAntialiasing.default"));
                } else {
                    String propertyValue = fontAntialiasing.getPropertyValue();
                    fontAntialiasingNames.add(resourceBundle.getString("fontAntialiasing." + propertyValue));
                }
            }
            mainOptionsPanel.setFontAntialiasings(macOsAppearancesKeys, fontAntialiasingNames);

            guiScalings = GuiScaling.getAvailable();
            List<String> guiScalingKeys = new ArrayList<>();
            guiScalingNames = new ArrayList<>();
            for (GuiScaling guiScaling : guiScalings) {
                guiScalingKeys.add(guiScaling.getPropertyValue());
                if (guiScaling == GuiScaling.DEFAULT) {
                    guiScalingNames.add(resourceBundle.getString("guiScaling.default"));
                } else {
                    String propertyValue = guiScaling.getPropertyValue();
                    guiScalingNames.add(resourceBundle.getString("guiScaling." + propertyValue));
                }
            }
            mainOptionsPanel.setGuiScalings(guiScalingKeys, guiScalingNames);

            if (DesktopUtils.detectBasicOs() == DesktopUtils.DesktopOs.MAC_OS) {
                guiMacOsAppearances = GuiMacOsAppearance.getAvailable();
                List<String> guiMacOsAppearancesKeys = new ArrayList<>();
                guiMacOsAppearanceNames = new ArrayList<>();
                for (GuiFontAntialiasing fontAntialiasing : fontAntialiasings) {
                    macOsAppearancesKeys.add(fontAntialiasing.getPropertyValue());
                    if (fontAntialiasing == GuiFontAntialiasing.DEFAULT) {
                        guiMacOsAppearanceNames.add(resourceBundle.getString("macOsAppearances.default"));
                    } else {
                        String propertyValue = fontAntialiasing.getPropertyValue();
                        guiMacOsAppearanceNames.add(resourceBundle.getString("macOsAppearances." + propertyValue));
                    }
                }
                mainOptionsPanel.setMacOsAppearances(guiMacOsAppearancesKeys, guiMacOsAppearanceNames);
            }
        }
        return mainOptionsPanel;
    }

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    @Nonnull
    public OptionsPage<FrameworkOptionsImpl> getMainOptionsPage() {
        return new DefaultOptionsPage<FrameworkOptionsImpl>() {
            @Nonnull
            @Override
            public OptionsComponent<FrameworkOptionsImpl> createPanel() {
                return getMainOptionsPanel();
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return LanguageUtils.getResourceBundleByClass(MainOptionsPanel.class);
            }

            @Nonnull
            @Override
            public FrameworkOptionsImpl createOptions() {
                return new FrameworkOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, FrameworkOptionsImpl options) {
                FrameworkPreferences prefs = new FrameworkPreferences(preferences);
                options.loadFromPreferences(prefs);
            }

            @Override
            public void saveToPreferences(Preferences preferences, FrameworkOptionsImpl options) {
                FrameworkPreferences prefs = new FrameworkPreferences(preferences);
                options.saveToParameters(prefs);
            }

            @Override
            public void applyPreferencesChanges(FrameworkOptionsImpl options) {
                String selectedTheme = options.getLookAndFeel();
                application.applyLookAndFeel(selectedTheme);
            }
        };
    }
}
