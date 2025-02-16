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
package org.exbin.framework.ui.theme;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.exbin.framework.App;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsPageManagement;
import org.exbin.framework.ui.api.UiModuleApi;
import org.exbin.framework.ui.theme.api.LafProvider;
import org.exbin.framework.ui.theme.options.ThemeOptions;
import org.exbin.framework.utils.DesktopUtils;
import org.exbin.framework.ui.theme.api.UiThemeModuleApi;
import org.exbin.framework.ui.theme.options.ThemeOptionsPage;

/**
 * Module user interface handling.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class UiThemeModule implements UiThemeModuleApi {

    private ResourceBundle resourceBundle;
    private final List<LafProvider> lafProviders = new ArrayList<>();

    private ThemeOptionsManager themeOptionsManager;

    public UiThemeModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(UiThemeModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Override
    public void registerLafPlugin(LafProvider lafProvider) {
        lafProviders.add(lafProvider);
    }

    @Nonnull
    @Override
    public List<LafProvider> getLafProviders() {
        return lafProviders;
    }

    @Override
    public void registerThemeInit() {
        UiModuleApi uiModule = App.getModule(UiModuleApi.class);
        uiModule.addPreInitAction(() -> {
            PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
            ThemeOptions themeOptions = new ThemeOptions(preferencesModule.getAppPreferences());

            LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
            String iconSet = themeOptions.getIconSet();
            if (!iconSet.isEmpty()) {
                languageModule.switchToIconSet(iconSet);
            }
            
            // Rendering should be initialized first before any GUI is used
            String renderingMode = themeOptions.getRenderingMode();
            if ("software".equals(renderingMode)) {
                System.setProperty("sun.java2d.noddraw", "true");
            } else if ("directdraw".equals(renderingMode)) {
                System.setProperty("sun.java2d.d3d", "false");
            } else if ("hw_scale".equals(renderingMode)) {
                System.setProperty("sun.java2d.d3d", "true");
                System.setProperty("sun.java2d.ddforcevram", "true");
                System.setProperty("sun.java2d.translaccel", "true");
                System.setProperty("sun.java2d.ddscale", "true");
            } else if ("opengl".equals(renderingMode)) {
                System.setProperty("sun.java2d.opengl", "true");
            } else if ("xrender".equals(renderingMode)) {
                System.setProperty("sun.java2d.xrender", "true");
            } else if ("metal".equals(renderingMode)) {
                System.setProperty("sun.java2d.metal", "true");
            } else if ("wayland".equals(renderingMode)) {
                System.setProperty("awt.toolkit.name", "WLToolkit");
            }

            String guiScaling = themeOptions.getGuiScaling();
            if (!guiScaling.isEmpty()) {
                if ("custom".equals(guiScaling)) {
                    float guiScalingRate = themeOptions.getGuiScalingRate();
                    String scalingRateText = guiScalingRate == Math.floor(guiScalingRate) ? String.format("%.0f", guiScalingRate) : String.valueOf(guiScalingRate);
                    System.setProperty("sun.java2d.uiScale.enabled", "true");
                    System.setProperty("sun.java2d.uiScale", scalingRateText);
                } else {
                    System.setProperty("sun.java2d.uiScale.enabled", guiScaling);
                }
            }

            String fontAntialiasing = themeOptions.getFontAntialiasing();
            if (!fontAntialiasing.isEmpty()) {
                System.setProperty("awt.useSystemAAFontSettings", fontAntialiasing);
            }

            if (DesktopUtils.detectBasicOs() == DesktopUtils.OsType.MACOSX) {
                boolean useScreenMenuBar = themeOptions.isUseScreenMenuBar();
                if (useScreenMenuBar) {
                    System.setProperty("apple.laf.useScreenMenuBar", "true");
                }

                String macOsAppearance = themeOptions.getMacOsAppearance();
                if ("light".equals(macOsAppearance)) {
                    System.setProperty("apple.awt.application.appearance", "NSAppearanceNameAqua");
                } else if ("dark".equals(macOsAppearance)) {
                    System.setProperty("apple.awt.application.appearance", "NSAppearanceNameDarkAqua");
                } else if ("system".equals(macOsAppearance)) {
                    System.setProperty("apple.awt.application.appearance", "system");
                }
            }

            for (LafProvider lafProvider : lafProviders) {
                lafProvider.installLaf();
            }

            switchToLookAndFeel(themeOptions.getLookAndFeel());
        });
    }

    public void switchToLookAndFeel(String laf) {
        for (LafProvider provider : lafProviders) {
            if (laf.equals(provider.getLafId())) {
                provider.applyLaf();
                return;
            }
        }

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
//                LookAndFeelApplier applier = lafPlugins.get(laf);
//                if (applier != null) {
//                    applier.applyLookAndFeel(laf);
//                } else {
                UIManager.setLookAndFeel(laf);
//                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(UiThemeModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        getThemeOptionsManager();
        ThemeOptionsPage themeOptionsPage = themeOptionsManager.createThemeOptionsPage();
        OptionsPageManagement optionsPageManagement = optionsModule.getOptionsPageManagement(MODULE_ID);
        optionsPageManagement.registerPage(themeOptionsPage);
    }

    @Nonnull
    public ThemeOptionsManager getThemeOptionsManager() {
        if (themeOptionsManager == null) {
            themeOptionsManager = new ThemeOptionsManager();
        }
        return themeOptionsManager;
    }
}
