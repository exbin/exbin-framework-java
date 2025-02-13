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
package org.exbin.framework.ui.theme.options;

import java.awt.Dialog;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JDialog;
import org.exbin.framework.App;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.DefaultOptionsStorage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.preferences.api.OptionsStorage;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.ui.theme.api.ConfigurableLafProvider;
import org.exbin.framework.ui.theme.api.LafOptionsHandler;
import org.exbin.framework.ui.theme.gui.ThemeOptionsPanel;
import org.exbin.framework.ui.theme.options.impl.ThemeOptionsImpl;
import org.exbin.framework.utils.DesktopUtils;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.window.api.gui.DefaultControlPanel;

/**
 * UI theme options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ThemeOptionsPage implements DefaultOptionsPage<ThemeOptions> {
    
    public static final String PAGE_ID = "theme";

    @Nonnull
    @Override
    public String getId() {
        return PAGE_ID;
    }

    @Nonnull
    @Override
    public OptionsComponent<ThemeOptions> createPanel() {
        if (!valuesInitialized) {
            valuesInitialized = true;
            initValues();
        }

        ThemeOptionsPanel mainOptionsPanel = new ThemeOptionsPanel();
        mainOptionsPanel.setThemes(themes, themeNames, themeOptions);
        mainOptionsPanel.setIconSets(iconSets, iconSetNames);
        mainOptionsPanel.setRenderingModes(renderingMethodKeys, renderingMethodNames);
        mainOptionsPanel.setFontAntialiasings(fontAntialiasingKeys, fontAntialiasingNames);
        mainOptionsPanel.setGuiScalings(guiScalingKeys, guiScalingNames);
        if (DesktopUtils.detectBasicOs() == DesktopUtils.OsType.MACOSX) {
            mainOptionsPanel.setMacOsAppearances(guiMacOsAppearanceKeys, guiMacOsAppearanceNames);
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
        return App.getModule(LanguageModuleApi.class).getBundle(ThemeOptionsPanel.class);
    }

    @Nonnull
    @Override
    public ThemeOptions createOptions() {
        return new ThemeOptions(new DefaultOptionsStorage());
    }

    @Override
    public void loadFromPreferences(OptionsStorage preferences, ThemeOptions options) {
        ThemeOptions prefs = new ThemeOptions(preferences);
        options.loadFromPreferences(prefs);
    }

    @Override
    public void saveToPreferences(OptionsStorage preferences, ThemeOptions options) {
        ThemeOptions prefs = new ThemeOptions(preferences);
        options.saveToParameters(prefs);
        for (LafOptionsHandler lafOptions : themeOptionsHandlers.values()) {
            lafOptions.saveToPreferences(preferences);
        }
    }

    @Override
    public void applyPreferencesChanges(ThemeOptions options) {
        String selectedTheme = options.getLookAndFeel();
        // TODO application.applyLookAndFeel(selectedTheme);

        // TODO This would require rebuild of the window to force icons reload
        // App.getModule(LanguageModuleApi.class).switchToIconSet(options.getIconSet());
    }
}
