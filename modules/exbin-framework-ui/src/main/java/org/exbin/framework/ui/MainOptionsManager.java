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

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.UIManager;
import org.exbin.framework.App;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.language.api.IconSetProvider;
import org.exbin.framework.preferences.api.Preferences;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.options.api.OptionsPage;
import org.exbin.framework.ui.api.ConfigurableLafProvider;
import org.exbin.framework.ui.gui.MainOptionsPanel;
import org.exbin.framework.ui.options.impl.UiOptionsImpl;
import org.exbin.framework.utils.DesktopUtils;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.language.api.LanguageProvider;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.ui.api.LafOptionsHandler;
import org.exbin.framework.ui.api.LafProvider;
import org.exbin.framework.ui.api.UiModuleApi;
import org.exbin.framework.ui.api.preferences.UiPreferences;
import org.exbin.framework.ui.model.LanguageRecord;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;

/**
 * Interface for application options panels management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class MainOptionsManager {

    private final ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(MainOptionsManager.class);

    private boolean valuesInitialized = false;
    private List<String> themes;
    private List<String> themeNames;
    private Map<String, ConfigurableLafProvider> themeOptions;
    private List<String> iconSets;
    private List<String> iconSetNames;
    private List<LanguageRecord> languageLocales = null;
    private List<String> renderingMethodKeys;
    private List<String> renderingMethodNames;
    private List<String> fontAntialiasingNames;
    private List<String> guiScalingKeys;
    private List<String> guiScalingNames;
    private List<String> guiMacOsAppearanceNames;
    private List<String> guiMacOsAppearancesKeys;
    private List<String> macOsAppearancesKeys;

    private MainOptionsPanel mainOptionsPanel;

    public MainOptionsManager() {
    }

    public void initValues() {
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
        themeOptions = new HashMap<>();
        List<LafProvider> lafProviders = App.getModule(UiModuleApi.class).getLafProviders();
        for (LafProvider lafProvider : lafProviders) {
            if (lafProvider instanceof ConfigurableLafProvider) {
                themeOptions.put(lafProvider.getLafId(), (ConfigurableLafProvider) lafProvider);
            }
        }

        iconSets = new ArrayList<>();
        iconSets.add("");
        iconSetNames = new ArrayList<>();
        iconSetNames.add(resourceBundle.getString("iconset.defaultTheme"));
        List<IconSetProvider> providers = App.getModule(LanguageModuleApi.class).getIconSets();
        for (IconSetProvider provider : providers) {
            iconSets.add(provider.getId());
            iconSetNames.add(provider.getName());
        }

        languageLocales = new ArrayList<>();
        languageLocales.add(new LanguageRecord(Locale.ROOT, null));
        languageLocales.add(new LanguageRecord(Locale.US, new ImageIcon(getClass().getResource(resourceBundle.getString("locale.englishFlag")))));

        List<LanguageProvider> languagePlugins = App.getModule(LanguageModuleApi.class).getLanguagePlugins();
        for (LanguageProvider languageProvider : languagePlugins) {
            ImageIcon flag = null;
            try {
                flag = languageProvider.getFlag().orElse(null);
            } catch (Throwable ex) {
                Logger.getLogger(MainOptionsManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            languageLocales.add(new LanguageRecord(languageProvider.getLocale(), flag, null));
        }

        renderingMethodKeys = new ArrayList<>();
        renderingMethodNames = new ArrayList<>();
        DesktopUtils.OsType desktopOs = DesktopUtils.detectBasicOs();
        for (GuiRenderingMethod renderingMethod : GuiRenderingMethod.getAvailableMethods()) {
            renderingMethodKeys.add(renderingMethod.getPropertyValue());
            if (renderingMethod == GuiRenderingMethod.DEFAULT) {
                renderingMethodNames.add(resourceBundle.getString("renderingMethod.default"));
            } else {
                if (renderingMethod == GuiRenderingMethod.SOFTWARE && desktopOs == DesktopUtils.OsType.WINDOWS) {
                    renderingMethodNames.add(resourceBundle.getString("renderingMethod.software.windows"));
                } else {
                    String propertyValue = renderingMethod.getPropertyValue();
                    renderingMethodNames.add(resourceBundle.getString("renderingMethod." + propertyValue));
                }
            }
        }

        macOsAppearancesKeys = new ArrayList<>();
        fontAntialiasingNames = new ArrayList<>();
        for (GuiFontAntialiasing fontAntialiasing : GuiFontAntialiasing.getAvailable()) {
            macOsAppearancesKeys.add(fontAntialiasing.getPropertyValue());
            if (fontAntialiasing == GuiFontAntialiasing.DEFAULT) {
                fontAntialiasingNames.add(resourceBundle.getString("fontAntialiasing.default"));
            } else {
                String propertyValue = fontAntialiasing.getPropertyValue();
                fontAntialiasingNames.add(resourceBundle.getString("fontAntialiasing." + propertyValue));
            }
        }

        guiScalingKeys = new ArrayList<>();
        guiScalingNames = new ArrayList<>();
        for (GuiScaling guiScaling : GuiScaling.getAvailable()) {
            guiScalingKeys.add(guiScaling.getPropertyValue());
            if (guiScaling == GuiScaling.DEFAULT) {
                guiScalingNames.add(resourceBundle.getString("guiScaling.default"));
            } else {
                String propertyValue = guiScaling.getPropertyValue();
                guiScalingNames.add(resourceBundle.getString("guiScaling." + propertyValue));
            }
        }

        if (DesktopUtils.detectBasicOs() == DesktopUtils.OsType.MACOSX) {
            List<GuiMacOsAppearance> guiMacOsAppearances = GuiMacOsAppearance.getAvailable();
            guiMacOsAppearancesKeys = new ArrayList<>();
            guiMacOsAppearanceNames = new ArrayList<>();
            for (GuiMacOsAppearance macOsAppearance : guiMacOsAppearances) {
                guiMacOsAppearancesKeys.add(macOsAppearance.getPropertyValue());
                macOsAppearancesKeys.add(macOsAppearance.getPropertyValue());
                if (macOsAppearance == GuiMacOsAppearance.DEFAULT) {
                    guiMacOsAppearanceNames.add(resourceBundle.getString("macOsAppearances.default"));
                } else {
                    String propertyValue = macOsAppearance.getPropertyValue();
                    guiMacOsAppearanceNames.add(resourceBundle.getString("macOsAppearances." + propertyValue));
                }
            }
        }
    }

    @Nonnull
    public OptionsPage<UiOptionsImpl> getMainOptionsPage() {
        return new DefaultOptionsPage<UiOptionsImpl>() {
            @Nonnull
            @Override
            public OptionsComponent<UiOptionsImpl> createPanel() {
                if (!valuesInitialized) {
                    valuesInitialized = true;
                    initValues();
                }

                Map<String, LafOptionsHandler> themeOptionsHandlers = new HashMap<>();
                mainOptionsPanel = new MainOptionsPanel();
                mainOptionsPanel.setThemes(themes, themeNames, themeOptions);
                mainOptionsPanel.setIconSets(iconSets, iconSetNames);
                mainOptionsPanel.setDefaultLocaleName("<" + resourceBundle.getString("locale.defaultLanguage") + ">");
                mainOptionsPanel.setLanguageLocales(languageLocales);
                mainOptionsPanel.setRenderingModes(renderingMethodKeys, renderingMethodNames);
                mainOptionsPanel.setFontAntialiasings(macOsAppearancesKeys, fontAntialiasingNames);
                mainOptionsPanel.setGuiScalings(guiScalingKeys, guiScalingNames);
                if (DesktopUtils.detectBasicOs() == DesktopUtils.OsType.MACOSX) {
                    mainOptionsPanel.setMacOsAppearances(guiMacOsAppearancesKeys, guiMacOsAppearanceNames);
                }
                mainOptionsPanel.setThemeConfigurationListener((ConfigurableLafProvider lafProvider) -> {
                    LafOptionsHandler optionsHandler = themeOptionsHandlers.get(lafProvider.getLafId());
                    if (optionsHandler == null) {
                        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
                        optionsHandler = lafProvider.getOptionsHandler();
                        optionsHandler.loadFromPreferences(preferencesModule.getAppPreferences());
                    }
                    final LafOptionsHandler finalOptionsHandler = optionsHandler;

                    DefaultControlPanel controlPanel = new DefaultControlPanel();
                    WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
                    FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
                    final WindowHandler dialog = windowModule.createDialog(frameModule.getFrame(), Dialog.ModalityType.APPLICATION_MODAL, optionsHandler.createOptionsComponent(), controlPanel);
                    ((JDialog) dialog.getWindow()).setTitle(resourceBundle.getString("theme.optionsWindow.title"));
                    controlPanel.setHandler((actionType) -> {
                        switch (actionType) {
                            case OK: {
                                themeOptionsHandlers.put(lafProvider.getLafId(), finalOptionsHandler);
                                break;
                            }
                            case CANCEL: {
                                // ignore
                                break;
                            }
                        }
                        dialog.close();
                    });

                    dialog.showCentered(frameModule.getFrame());

                });
                return mainOptionsPanel;
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return App.getModule(LanguageModuleApi.class).getBundle(MainOptionsPanel.class);
            }

            @Nonnull
            @Override
            public UiOptionsImpl createOptions() {
                return new UiOptionsImpl();
            }

            @Override
            public void loadFromPreferences(Preferences preferences, UiOptionsImpl options) {
                UiPreferences prefs = new UiPreferences(preferences);
                options.loadFromPreferences(prefs);
            }

            @Override
            public void saveToPreferences(Preferences preferences, UiOptionsImpl options) {
                UiPreferences prefs = new UiPreferences(preferences);
                options.saveToParameters(prefs);
            }

            @Override
            public void applyPreferencesChanges(UiOptionsImpl options) {
                String selectedTheme = options.getLookAndFeel();
                // TODO application.applyLookAndFeel(selectedTheme);

                // TODO This would require rebuild of the window to force icons reload
                // App.getModule(LanguageModuleApi.class).switchToIconSet(options.getIconSet());
            }
        };
    }

    @Nonnull
    public Optional<MainOptionsPanel> getMainOptionsPanel() {
        return Optional.ofNullable(mainOptionsPanel);
    }

}
