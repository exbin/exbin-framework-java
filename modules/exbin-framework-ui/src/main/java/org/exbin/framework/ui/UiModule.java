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
package org.exbin.framework.ui;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.ui.api.UiModuleApi;
import org.exbin.framework.ui.api.preferences.UiPreferences;
import org.exbin.framework.utils.DesktopUtils;

/**
 * Module window handling.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class UiModule implements UiModuleApi {

    private ResourceBundle resourceBundle;

    public UiModule() {
    }

    @Nonnull
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(UiModule.class);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Override
    public void initSwingUi() {
        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
        UiPreferences uiPreferences = new UiPreferences(preferencesModule.getAppPreferences());

        // Rendering should be initialized first before any GUI is used
        String renderingMode = uiPreferences.getRenderingMode();
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

        String guiScaling = uiPreferences.getGuiScaling();
        if (!guiScaling.isEmpty()) {
            if ("custom".equals(guiScaling)) {
                float guiScalingRate = uiPreferences.getGuiScalingRate();
                String scalingRateText = guiScalingRate == Math.floor(guiScalingRate) ? String.format("%.0f", guiScalingRate) : String.valueOf(guiScalingRate);
                System.setProperty("sun.java2d.uiScale.enabled", "true");
                System.setProperty("sun.java2d.uiScale", scalingRateText);
            } else {
                System.setProperty("sun.java2d.uiScale.enabled", guiScaling);
            }
        }

        String fontAntialiasing = uiPreferences.getFontAntialiasing();
        if (!fontAntialiasing.isEmpty()) {
            System.setProperty("awt.useSystemAAFontSettings", fontAntialiasing);
        }

        if (DesktopUtils.detectBasicOs() == DesktopUtils.OsType.MACOSX) {
            boolean useScreenMenuBar = uiPreferences.isUseScreenMenuBar();
            if (useScreenMenuBar) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
            }

            String macOsAppearance = uiPreferences.getMacOsAppearance();
            if ("light".equals(macOsAppearance)) {
                System.setProperty("apple.awt.application.appearance", "NSAppearanceNameAqua");
            } else if ("dark".equals(macOsAppearance)) {
                System.setProperty("apple.awt.application.appearance", "NSAppearanceNameDarkAqua");
            } else if ("system".equals(macOsAppearance)) {
                System.setProperty("apple.awt.application.appearance", "system");
            }
        }

        // Switching language
        Locale locale = uiPreferences.getLocale();
        if (!locale.equals(Locale.ROOT)) {
            Locale.setDefault(locale);
        }

//        targetLaf = frameworkParameters.getLookAndFeel();
    }
    /*
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
            Logger.getLogger(UiModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     */
}