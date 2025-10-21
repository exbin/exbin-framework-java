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
package org.exbin.framework.ui.theme.settings;

import java.awt.Dialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JDialog;
import javax.swing.UIManager;
import org.exbin.framework.App;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.language.api.IconSetProvider;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.ui.theme.GuiFontAntialiasing;
import org.exbin.framework.ui.theme.GuiMacOsAppearance;
import org.exbin.framework.ui.theme.GuiRenderingMethod;
import org.exbin.framework.ui.theme.GuiScaling;
import org.exbin.framework.ui.theme.api.ConfigurableLafProvider;
import org.exbin.framework.ui.theme.api.LafOptionsHandler;
import org.exbin.framework.ui.theme.api.LafProvider;
import org.exbin.framework.ui.theme.api.UiThemeModuleApi;
import org.exbin.framework.ui.theme.settings.gui.ThemeSettingsPanel;
import org.exbin.framework.utils.DesktopUtils;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.settings.api.SettingsComponent;
import org.exbin.framework.options.settings.api.SettingsComponentProvider;

/**
 * UI theme settings.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ThemeSettingsComponent implements SettingsComponentProvider<ThemeOptions> {
    
    public static final String COMPONENT_ID = "theme";

    private ResourceBundle resourceBundle;

    private Map<String, LafOptionsHandler> themeOptionsHandlers = new HashMap<>();

    private boolean valuesInitialized = false;
    private List<String> themes;
    private List<String> themeNames;
    private Map<String, ConfigurableLafProvider> themeOptions;
    private List<String> iconSets;
    private List<String> iconSetNames;
    private List<String> renderingMethodKeys;
    private List<String> renderingMethodNames;
    private List<String> fontAntialiasingKeys;
    private List<String> fontAntialiasingNames;
    private List<String> guiScalingKeys;
    private List<String> guiScalingNames;
    private List<String> guiMacOsAppearanceKeys;
    private List<String> guiMacOsAppearanceNames;

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
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
        List<LafProvider> lafProviders = App.getModule(UiThemeModuleApi.class).getLafProviders();
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

        fontAntialiasingKeys = new ArrayList<>();
        fontAntialiasingNames = new ArrayList<>();
        for (GuiFontAntialiasing fontAntialiasing : GuiFontAntialiasing.getAvailable()) {
            fontAntialiasingKeys.add(fontAntialiasing.getPropertyValue());
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
            guiMacOsAppearanceKeys = new ArrayList<>();
            guiMacOsAppearanceNames = new ArrayList<>();
            for (GuiMacOsAppearance macOsAppearance : guiMacOsAppearances) {
                guiMacOsAppearanceKeys.add(macOsAppearance.getPropertyValue());
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
    @Override
    public SettingsComponent<ThemeOptions> createComponent() {
        if (!valuesInitialized) {
            valuesInitialized = true;
            initValues();
        }

        ThemeSettingsPanel themeOptionsPanel = new ThemeSettingsPanel();
        themeOptionsPanel.setThemes(themes, themeNames, themeOptions);
        themeOptionsPanel.setIconSets(iconSets, iconSetNames);
        themeOptionsPanel.setRenderingModes(renderingMethodKeys, renderingMethodNames);
        themeOptionsPanel.setFontAntialiasings(fontAntialiasingKeys, fontAntialiasingNames);
        themeOptionsPanel.setGuiScalings(guiScalingKeys, guiScalingNames);
        if (DesktopUtils.detectBasicOs() == DesktopUtils.OsType.MACOSX) {
            themeOptionsPanel.setMacOsAppearances(guiMacOsAppearanceKeys, guiMacOsAppearanceNames);
        }
        themeOptionsPanel.setThemeConfigurationListener((ConfigurableLafProvider lafProvider) -> {
            LafOptionsHandler optionsHandler = themeOptionsHandlers.get(lafProvider.getLafId());
            if (optionsHandler == null) {
                OptionsModuleApi preferencesModule = App.getModule(OptionsModuleApi.class);
                optionsHandler = lafProvider.getOptionsHandler();
                optionsHandler.loadFromPreferences(preferencesModule.getAppOptions());
            }
            final LafOptionsHandler finalOptionsHandler = optionsHandler;

            DefaultControlPanel controlPanel = new DefaultControlPanel();
            WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
            FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
            final WindowHandler dialog = windowModule.createDialog(frameModule.getFrame(), Dialog.ModalityType.APPLICATION_MODAL, optionsHandler.createOptionsComponent(), controlPanel);
            ((JDialog) dialog.getWindow()).setTitle(resourceBundle.getString("theme.optionsWindow.title"));
            controlPanel.setController((actionType) -> {
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
        return themeOptionsPanel;
    }
}
