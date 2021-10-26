/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.gui.options;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.ApplicationFrameHandler;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.action.api.MenuGroup;
import org.exbin.framework.gui.action.api.MenuPosition;
import org.exbin.framework.gui.action.api.PositionMode;
import org.exbin.framework.gui.action.api.SeparationMode;
import org.exbin.framework.gui.options.api.DefaultOptionsPage;
import org.exbin.framework.gui.options.api.GuiOptionsModuleApi;
import org.exbin.framework.gui.options.api.OptionsCapable;
import org.exbin.framework.gui.options.api.OptionsData;
import org.exbin.framework.gui.options.gui.OptionsTreePanel;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.gui.options.api.OptionsPathItem;
import org.exbin.framework.gui.options.options.impl.AppearanceOptionsImpl;
import org.exbin.framework.gui.options.options.impl.FrameworkOptionsImpl;
import org.exbin.framework.gui.options.gui.AppearanceOptionsPanel;
import org.exbin.framework.gui.options.gui.MainOptionsPanel;
import org.exbin.framework.gui.options.preferences.AppearancePreferences;
import org.exbin.framework.gui.utils.ComponentResourceProvider;
import org.exbin.framework.preferences.FrameworkPreferences;
import org.exbin.framework.gui.options.api.OptionsPage;
import org.exbin.framework.gui.action.api.GuiActionModuleApi;
import org.exbin.framework.gui.options.action.OptionsAction;

/**
 * Implementation of framework options module.
 *
 * @version 0.2.1 2021/10/26
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GuiOptionsModule implements GuiOptionsModuleApi {

    private XBApplication application;
    private ResourceBundle resourceBundle;

    private OptionsAction optionsAction;
    private final List<OptionsPageRecord> optionsPages = new ArrayList<>();
    private OptionsPage<?> mainOptionsExtPage = null;
    private OptionsPage<?> appearanceOptionsExtPage = null;

    public GuiOptionsModule() {
    }

    @Override
    public void init(XBModuleHandler moduleHandler) {
        this.application = (XBApplication) moduleHandler;

        OptionsPage<FrameworkOptionsImpl> mainOptionsPage = new DefaultOptionsPage<FrameworkOptionsImpl>() {
            @Nonnull
            @Override
            public OptionsCapable<FrameworkOptionsImpl> createPanel() {
                return new MainOptionsPanel();
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
        optionsPages.add(new OptionsPageRecord(null, mainOptionsPage));

        OptionsPage<AppearanceOptionsImpl> appearanceOptionsPage;
        appearanceOptionsPage = new DefaultOptionsPage<AppearanceOptionsImpl>() {
            @Nonnull
            @Override
            public OptionsCapable<AppearanceOptionsImpl> createPanel() {
                return new AppearanceOptionsPanel();
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return LanguageUtils.getResourceBundleByClass(AppearanceOptionsPanel.class);
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
                GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                ApplicationFrameHandler frame = frameModule.getFrameHandler();
                frame.setToolBarVisible(options.isShowToolBar());
                frame.setToolBarCaptionsVisible(options.isShowToolBarCaptions());
                frame.setStatusBarVisible(options.isShowStatusBar());
                frameModule.notifyFrameUpdated();
            }
        };
        ResourceBundle optionsResourceBundle = LanguageUtils.getResourceBundleByClass(AppearanceOptionsPanel.class);
        List<OptionsPathItem> optionsPath = new ArrayList<>();
        optionsPath.add(new OptionsPathItem(optionsResourceBundle.getString("options.name"), optionsResourceBundle.getString("options.caption")));
        addOptionsPage(appearanceOptionsPage, optionsPath);
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    private ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = LanguageUtils.getResourceBundleByClass(GuiOptionsModule.class);
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
            optionsAction.setup(application, resourceBundle, (OptionsTreePanel optionsTreePanel) -> {
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
        Preferences preferences = application.getAppPreferences();
        for (OptionsPageRecord optionsPage : optionsPages) {
            OptionsPage page = optionsPage.optionsPage;
            OptionsData options = page.createOptions();
            page.loadFromPreferences(preferences, options);
        }
    }

    @Override
    public void registerMenuAction() {
        GuiActionModuleApi actionModule = application.getModuleRepository().getModuleByInterface(GuiActionModuleApi.class);
        actionModule.registerMenuGroup(GuiFrameModuleApi.TOOLS_MENU_ID, new MenuGroup(TOOLS_OPTIONS_MENU_GROUP_ID, new MenuPosition(PositionMode.BOTTOM_LAST), SeparationMode.AROUND));
        getOptionsAction();
        actionModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, optionsAction, new MenuPosition(TOOLS_OPTIONS_MENU_GROUP_ID));
    }

    private static class OptionsPageRecord {

        List<OptionsPathItem> path;
        OptionsPage<?> optionsPage;

        public OptionsPageRecord(@Nullable List<OptionsPathItem> path, OptionsPage<?> optionsPage) {
            this.path = path;
            this.optionsPage = optionsPage;
        }
    }
}
