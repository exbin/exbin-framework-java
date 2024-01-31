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

import com.formdev.flatlaf.extras.FlatDesktop;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.preferences.api.Preferences;
import org.exbin.framework.frame.api.ApplicationFrameHandler;
import org.exbin.framework.action.api.MenuGroup;
import org.exbin.framework.action.api.MenuPosition;
import org.exbin.framework.action.api.PositionMode;
import org.exbin.framework.action.api.SeparationMode;
import org.exbin.framework.options.api.DefaultOptionsPage;
import org.exbin.framework.options.api.OptionsModuleApi;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.options.gui.OptionsTreePanel;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.options.api.OptionsPathItem;
import org.exbin.framework.options.options.impl.AppearanceOptionsImpl;
import org.exbin.framework.options.options.impl.UiOptionsImpl;
import org.exbin.framework.options.gui.AppearanceOptionsPanel;
import org.exbin.framework.options.preferences.AppearancePreferences;
import org.exbin.framework.utils.ComponentResourceProvider;
import org.exbin.framework.options.api.OptionsPage;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.options.action.OptionsAction;
import org.exbin.framework.options.options.AppearanceOptions;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.utils.DesktopUtils;
import org.exbin.framework.options.api.OptionsComponent;

/**
 * Implementation of framework options module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsModule implements OptionsModuleApi {

    private ResourceBundle resourceBundle;

    private OptionsAction optionsAction;
    private final List<OptionsPageRecord> optionsPages = new ArrayList<>();
    private MainOptionsManager mainOptionsManager;
    private OptionsPage<?> mainOptionsExtPage = null;
    private OptionsPage<?> appearanceOptionsExtPage = null;

    public OptionsModule() {
    }

    @Nonnull
    private ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(OptionsModule.class);

            OptionsPage<UiOptionsImpl> mainOptionsPage = getMainOptionsManager().getMainOptionsPage();
            optionsPages.add(new OptionsPageRecord(null, mainOptionsPage));

            OptionsPage<AppearanceOptionsImpl> appearanceOptionsPage;
            appearanceOptionsPage = new DefaultOptionsPage<AppearanceOptionsImpl>() {
                @Nonnull
                @Override
                public OptionsComponent<AppearanceOptionsImpl> createPanel() {
                    return new AppearanceOptionsPanel();
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
            addOptionsPage(appearanceOptionsPage, optionsPath);
        }

        return resourceBundle;
    }

    private void ensureSetup() {
        if (resourceBundle == null) {
            getResourceBundle();
        }
    }

    @Nonnull
    @Override
    public Action getOptionsAction() {
        if (optionsAction == null) {
            ensureSetup();
            optionsAction = new OptionsAction();
            optionsAction.setup(resourceBundle, (OptionsTreePanel optionsTreePanel) -> {
                optionsPages.forEach((record) -> {
                    optionsTreePanel.addOptionsPage(record.optionsPage, record.path);
                });
                if (mainOptionsExtPage != null) {
                    optionsTreePanel.extendMainOptionsPage(mainOptionsExtPage);
                }
                if (appearanceOptionsExtPage != null) {
                    optionsTreePanel.extendAppearanceOptionsPage(appearanceOptionsExtPage);
                }
            });
        }

        return optionsAction;
    }
    
    @Nonnull
    public MainOptionsManager getMainOptionsManager() {
        if (mainOptionsManager == null) {
            mainOptionsManager = new MainOptionsManager();
        }
        return mainOptionsManager;
    }

    @Override
    public void notifyOptionsChanged() {
        FrameModuleApi frameModule = App.getModule(FrameModuleApi.class);
        frameModule.notifyFrameUpdated();
    }

    @Override
    public void addOptionsPage(OptionsPage<?> optionsPage, List<OptionsPathItem> path) {
        optionsPages.add(new OptionsPageRecord(path, optionsPage));
    }

    @Override
    public void addOptionsPage(OptionsPage<?> optionsPage, String parentPath) {
        ArrayList<OptionsPathItem> optionsPath = new ArrayList<>();
        String[] pathParts = parentPath.split("/");
        for (String pathPart : pathParts) {
            if (!pathPart.isEmpty()) {
                optionsPath.add(new OptionsPathItem(pathPart, null));
            }
        }
        if (optionsPage instanceof ComponentResourceProvider) {
            ResourceBundle componentResourceBundle = ((ComponentResourceProvider) optionsPage).getResourceBundle();
            String optionsDefaultName = componentResourceBundle.getString("options.name");
            String optionsDefaultCaption = componentResourceBundle.getString("options.caption");
            optionsPath.add(new OptionsPathItem(optionsDefaultName, optionsDefaultCaption));
        }
        addOptionsPage(optionsPage, optionsPath);
    }

    @Override
    public void addOptionsPage(OptionsPage<?> optionsPage) {
        String optionsDefaultPath;
        if (optionsPage instanceof ComponentResourceProvider) {
            ResourceBundle componentResourceBundle = ((ComponentResourceProvider) optionsPage).getResourceBundle();
            optionsDefaultPath = componentResourceBundle.getString("options.path");
        } else {
            optionsDefaultPath = null;
        }

        addOptionsPage(optionsPage, optionsDefaultPath);
    }

    @Override
    public void extendMainOptionsPage(OptionsPage<?> optionsPage) {
        mainOptionsExtPage = optionsPage;
    }

    @Override
    public void extendAppearanceOptionsPage(OptionsPage<?> optionsPage) {
        appearanceOptionsExtPage = optionsPage;
    }

    @Override
    public void initialLoadFromPreferences() {
        // TODO use preferences instead of options for initial apply
        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
        Preferences preferences = preferencesModule.getAppPreferences();
        for (OptionsPageRecord optionsPage : optionsPages) {
            OptionsPage page = optionsPage.optionsPage;
            OptionsData options = page.createOptions();
            page.loadFromPreferences(preferences, options);

            if (options instanceof AppearanceOptions) {
                page.applyPreferencesChanges(options);
            }
        }
        notifyOptionsChanged();
    }

    @Override
    public void registerMenuAction() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        getOptionsAction();

        boolean optionsActionRegistered = false;
        if (DesktopUtils.detectBasicOs() == DesktopUtils.OsType.MACOSX) {
            FlatDesktop.setPreferencesHandler(() -> {
                optionsAction.actionPerformed(null);
            });
            /* // TODO: Replace after migration to Java 9+
            Desktop desktop = Desktop.getDesktop();
            desktop.setPreferencesHandler((e) -> {
                optionsAction.actionPerformed(null);
            }); */
            optionsActionRegistered = true;
        }
        actionModule.registerMenuGroup(FrameModuleApi.TOOLS_MENU_ID, new MenuGroup(TOOLS_OPTIONS_MENU_GROUP_ID, new MenuPosition(PositionMode.BOTTOM_LAST), optionsActionRegistered ? SeparationMode.NONE : SeparationMode.AROUND));
        if (!optionsActionRegistered) {
            actionModule.registerMenuItem(FrameModuleApi.TOOLS_MENU_ID, MODULE_ID, optionsAction, new MenuPosition(TOOLS_OPTIONS_MENU_GROUP_ID));
        }
    }

    @ParametersAreNonnullByDefault
    private static class OptionsPageRecord {

        List<OptionsPathItem> path;
        OptionsPage<?> optionsPage;

        public OptionsPageRecord(@Nullable List<OptionsPathItem> path, OptionsPage<?> optionsPage) {
            this.path = path;
            this.optionsPage = optionsPage;
        }
    }
}

