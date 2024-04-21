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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.exbin.framework.App;
import org.exbin.framework.frame.api.ApplicationFrameHandler;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsPage;
import org.exbin.framework.options.api.OptionsPathItem;
import org.exbin.framework.preferences.api.Preferences;
import org.exbin.framework.ui.api.LafProvider;
import org.exbin.framework.ui.api.UiModuleApi;
import org.exbin.framework.ui.api.preferences.UiPreferences;
import org.exbin.framework.ui.gui.AppearanceOptionsPanel;
import org.exbin.framework.ui.gui.MainOptionsPanel;
import org.exbin.framework.ui.options.impl.AppearanceOptionsImpl;
import org.exbin.framework.ui.options.impl.UiOptionsImpl;
import org.exbin.framework.ui.preferences.AppearancePreferences;
import org.exbin.framework.utils.ComponentResourceProvider;
import org.exbin.framework.utils.DesktopUtils;

/**
 * Module user interface handling.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class UiModule implements UiModuleApi {

    private ResourceBundle resourceBundle;
    private final List<LafProvider> lafProviders = new ArrayList<>();

    private MainOptionsManager mainOptionsManager;
    private OptionsPage<?> mainOptionsExtPage = null;
    private OptionsPage<?> appearanceOptionsExtPage = null;
    private AppearanceOptionsPanel appearanceOptionsPanel;

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
    public void registerLafPlugin(LafProvider lafProvider) {
        lafProviders.add(lafProvider);
        lafProvider.installLaf();
    }

    @Nonnull
    @Override
    public List<LafProvider> getLafProviders() {
        return lafProviders;
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
        LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
        languageModule.switchToLanguage(locale);

        switchToLookAndFeel(uiPreferences.getLookAndFeel());
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
            Logger.getLogger(UiModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        getMainOptionsManager();
        OptionsPage<UiOptionsImpl> mainOptionsPage = mainOptionsManager.getMainOptionsPage();
        optionsModule.addOptionsPage(mainOptionsPage, "");
        Optional<MainOptionsPanel> mainOptionsPanel = mainOptionsManager.getMainOptionsPanel();
        if (mainOptionsExtPage != null) {
            mainOptionsPanel.get().addExtendedPanel(mainOptionsExtPage.createPanel());
        }

        OptionsPage<AppearanceOptionsImpl> appearanceOptionsPage = new DefaultOptionsPage<AppearanceOptionsImpl>() {
            @Nonnull
            @Override
            public OptionsComponent<AppearanceOptionsImpl> createPanel() {
                appearanceOptionsPanel = new AppearanceOptionsPanel();
                return appearanceOptionsPanel;
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return App.getModule(LanguageModuleApi.class).getBundle(AppearanceOptionsPanel.class);
            }

            @Nonnull
            @Override
            public AppearanceOptionsImpl createOptions() {
                return new AppearanceOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, AppearanceOptionsImpl options) {
                AppearancePreferences prefs = new AppearancePreferences(preferences);
                options.loadFromPreferences(prefs);
            }

            @Override
            public void saveToPreferences(Preferences preferences, AppearanceOptionsImpl options) {
                AppearancePreferences prefs = new AppearancePreferences(preferences);
                options.saveToParameters(prefs);
            }

            @Override
            public void applyPreferencesChanges(AppearanceOptionsImpl options) {
                // TODO Drop frame module dependency / move frame options to frame module
                FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
                ApplicationFrameHandler frame = frameModule.getFrameHandler();
                frame.setToolBarVisible(options.isShowToolBar());
                frame.setToolBarCaptionsVisible(options.isShowToolBarCaptions());
                frame.setStatusBarVisible(options.isShowStatusBar());
                frameModule.notifyFrameUpdated();
            }
        };
        ResourceBundle optionsResourceBundle = ((ComponentResourceProvider) appearanceOptionsPage).getResourceBundle();
        List<OptionsPathItem> optionsPath = new ArrayList<>();
        optionsPath.add(new OptionsPathItem(optionsResourceBundle.getString("options.name"), optionsResourceBundle.getString("options.caption")));
        optionsModule.addOptionsPage(appearanceOptionsPage, optionsPath);
        if (appearanceOptionsExtPage != null) {
            appearanceOptionsPanel.addExtendedPanel(appearanceOptionsExtPage.createPanel());
        }
    }

    @Nonnull
    public MainOptionsManager getMainOptionsManager() {
        if (mainOptionsManager == null) {
            mainOptionsManager = new MainOptionsManager();
        }
        return mainOptionsManager;
    }

    @Override
    public void extendMainOptionsPage(OptionsPage<?> optionsPage) {
        if (mainOptionsExtPage != null) {
            throw new IllegalStateException("Main options page extension already registered");
        }
        mainOptionsExtPage = optionsPage;

        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        Optional<MainOptionsPanel> mainOptionsPanel = mainOptionsManager.getMainOptionsPanel();
        if (mainOptionsPanel.isPresent()) {
            mainOptionsPanel.get().addExtendedPanel(mainOptionsExtPage.createPanel());
        }
    }

    @Override
    public void extendAppearanceOptionsPage(OptionsPage<?> optionsPage) {
        if (appearanceOptionsExtPage != null) {
            throw new IllegalStateException("Appearance options page extension already registered");
        }
        appearanceOptionsExtPage = optionsPage;
        if (appearanceOptionsPanel != null) {
            appearanceOptionsPanel.addExtendedPanel(appearanceOptionsExtPage.createPanel());
        }
    }
}
