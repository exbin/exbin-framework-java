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
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.DefaultComboBoxModel;
import javax.swing.UIManager;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.OptionsComponent;
import org.exbin.framework.options.api.OptionsPage;
import org.exbin.framework.options.gui.MainOptionsPanel;
import org.exbin.framework.options.options.impl.FrameworkOptionsImpl;
import org.exbin.framework.preferences.FrameworkPreferences;
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
    private List<Locale> languageLocales = null;

    public MainOptionsManager() {
    }

    @Nonnull
    public MainOptionsPanel getMainOptionsPanel() {
        if (mainOptionsPanel == null) {
            mainOptionsPanel = new MainOptionsPanel();
            mainOptionsPanel.setLanguageLocales(application.getLanguageLocales());

            themes = new ArrayList<>();
            themes.add("");
            boolean extraCrossPlatformLAF = !"javax.swing.plaf.metal.MetalLookAndFeel".equals(UIManager.getCrossPlatformLookAndFeelClassName());
            if (extraCrossPlatformLAF) {
                themes.add(UIManager.getCrossPlatformLookAndFeelClassName());
            }
            themes.add("javax.swing.plaf.metal.MetalLookAndFeel");
            themes.add("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            themeNames = new ArrayList<>();
            themeNames.add(resourceBundle.getString("defaultTheme"));
            if (extraCrossPlatformLAF) {
                themeNames.add(resourceBundle.getString("crossPlatformTheme"));
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

            mainOptionsPanel.setThemeNames(themeNames);

            languageLocales = new ArrayList<>();
            languageLocales.add(Locale.ROOT);
            languageLocales.add(new Locale("en", "US"));
            mainOptionsPanel.setLanguageLocales(languageLocales);
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
