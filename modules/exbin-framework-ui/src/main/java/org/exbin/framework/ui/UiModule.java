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
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
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

    private MainOptionsManager mainOptionsManager;
    private AppearanceOptionsPanel appearanceOptionsPanel;
    private List<Runnable> preInitActions = new ArrayList<>();
    private List<Runnable> postInitActions = new ArrayList<>();

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
        executePreInitActions();
        
        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
        UiPreferences uiPreferences = new UiPreferences(preferencesModule.getAppPreferences());

        // Switching language
        // TODO Move to language module, because language can be independent of UI
        Locale locale = uiPreferences.getLocale();
        LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
        languageModule.switchToLanguage(locale);

        String iconSet = uiPreferences.getIconSet();
        if (!iconSet.isEmpty()) {
            languageModule.switchToIconSet(iconSet);
        }

        executePostInitActions();
    }

    @Override
    public void addPreInitAction(Runnable runnable) {
        preInitActions.add(runnable);
    }

    @Override
    public void executePreInitActions() {
        for (Runnable runnable : preInitActions) {
            runnable.run();
        }
        preInitActions.clear();
    }

    @Override
    public void addPostInitAction(Runnable runnable) {
        postInitActions.add(runnable);
    }

    @Override
    public void executePostInitActions() {
        for (Runnable runnable : postInitActions) {
            runnable.run();
        }
        postInitActions.clear();
    }

    @Override
    public void registerOptionsPanels() {
        OptionsModuleApi optionsModule = App.getModule(OptionsModuleApi.class);
        getMainOptionsManager();
        OptionsPage<UiOptionsImpl> mainOptionsPage = mainOptionsManager.getMainOptionsPage();
        // TODO optionsModule.addOptionsPage(mainOptionsPage, "");
        Optional<MainOptionsPanel> mainOptionsPanel = mainOptionsManager.getMainOptionsPanel();
        // TODO
        /*
        if (mainOptionsExtPage != null) {
            mainOptionsPanel.get().addExtendedPanel(mainOptionsExtPage.createPanel());
        } */

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
        
        // TODO
        /*
        optionsModule.addOptionsPage(appearanceOptionsPage, optionsPath);
        if (appearanceOptionsExtPage != null) {
            appearanceOptionsPanel.addExtendedPanel(appearanceOptionsExtPage.createPanel());
        }
        */
    }

    @Nonnull
    public MainOptionsManager getMainOptionsManager() {
        if (mainOptionsManager == null) {
            mainOptionsManager = new MainOptionsManager();
        }
        return mainOptionsManager;
    }
}
