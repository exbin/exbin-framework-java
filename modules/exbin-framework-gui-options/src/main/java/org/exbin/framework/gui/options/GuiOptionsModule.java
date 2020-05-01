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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.exbin.framework.api.Preferences;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.ApplicationFrameHandler;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.menu.api.GuiMenuModuleApi;
import org.exbin.framework.gui.menu.api.MenuGroup;
import org.exbin.framework.gui.menu.api.MenuPosition;
import org.exbin.framework.gui.menu.api.PositionMode;
import org.exbin.framework.gui.menu.api.SeparationMode;
import org.exbin.framework.gui.options.api.DefaultOptionsPage;
import org.exbin.framework.gui.options.api.GuiOptionsModuleApi;
import org.exbin.framework.gui.options.api.OptionsCapable;
import org.exbin.framework.gui.options.api.OptionsData;
import org.exbin.framework.gui.options.panel.OptionsTreePanel;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.panel.OptionsControlPanel;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.gui.options.api.OptionsPathItem;
import org.exbin.framework.gui.options.options.impl.AppearanceOptionsImpl;
import org.exbin.framework.gui.options.options.impl.FrameworkOptionsImpl;
import org.exbin.framework.gui.options.panel.AppearanceOptionsPanel;
import org.exbin.framework.gui.options.panel.MainOptionsPanel;
import org.exbin.framework.gui.options.preferences.AppearancePreferences;
import org.exbin.framework.gui.utils.ComponentResourceProvider;
import org.exbin.framework.preferences.FrameworkPreferences;
import org.exbin.framework.gui.options.api.OptionsPage;

/**
 * Implementation of framework options module.
 *
 * @version 0.2.1 2019/07/21
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GuiOptionsModule implements GuiOptionsModuleApi {

    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action optionsAction;
    private final List<OptionsPageRecord> optionsPages = new ArrayList<>();
    private OptionsPage<?> mainOptionsExtPage = null;
    private OptionsPage<?> appearanceOptionsExtPage = null;

    public GuiOptionsModule() {
    }

    @Override
    public void init(XBModuleHandler moduleHandler) {
        this.application = (XBApplication) moduleHandler;

        OptionsPage<FrameworkOptionsImpl> mainOptionsPage = new DefaultOptionsPage<FrameworkOptionsImpl>() {
            @Override
            public OptionsCapable<FrameworkOptionsImpl> createPanel() {
                return new MainOptionsPanel();
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return LanguageUtils.getResourceBundleByClass(MainOptionsPanel.class);
            }

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
                if (selectedTheme != null) {
                    if (selectedTheme.isEmpty()) {
                        String osName = System.getProperty("os.name").toLowerCase();
                        if (!osName.startsWith("windows") && !osName.startsWith("mac")) {
                            try {
                                // Try "GTK+" on linux
                                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                                return;
                            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                            }
                        }
                        selectedTheme = UIManager.getSystemLookAndFeelClassName();
                    }

                    try {
                        UIManager.setLookAndFeel(selectedTheme);
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                        Logger.getLogger(FrameworkPreferences.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        optionsPages.add(new OptionsPageRecord(null, mainOptionsPage));

        OptionsPage<AppearanceOptionsImpl> appearanceOptionsPage;
        appearanceOptionsPage = new DefaultOptionsPage<AppearanceOptionsImpl>() {
            @Override
            public OptionsCapable<AppearanceOptionsImpl> createPanel() {
                return new AppearanceOptionsPanel();
            }

            @Nonnull
            @Override
            public ResourceBundle getResourceBundle() {
                return LanguageUtils.getResourceBundleByClass(AppearanceOptionsPanel.class);
            }

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

    @Nonnull
    @Override
    public Action getOptionsAction() {
        if (optionsAction == null) {
            getResourceBundle();
            optionsAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                    OptionsTreePanel optionsTreePanel = new OptionsTreePanel(frameModule.getFrameHandler());
                    optionsPages.forEach((record) -> {
                        optionsTreePanel.addOptionsPage(record.optionsPage, record.path);
                    });
                    if (mainOptionsExtPage != null) {
                        optionsTreePanel.extendMainOptionsPage(mainOptionsExtPage);
                    }
                    if (appearanceOptionsExtPage != null) {
                        optionsTreePanel.extendAppearanceOptionsPage(appearanceOptionsExtPage);
                    }
                    optionsTreePanel.setLanguageLocales(application.getLanguageLocales());

                    optionsTreePanel.setAppEditor(application);
                    optionsTreePanel.setPreferences(application.getAppPreferences());
                    optionsTreePanel.pagesFinished();
                    optionsTreePanel.loadAllFromPreferences();

                    OptionsControlPanel controlPanel = new OptionsControlPanel();
                    JPanel dialogPanel = WindowUtils.createDialogPanel(optionsTreePanel, controlPanel);
                    final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                    frameModule.setDialogTitle(dialog, optionsTreePanel.getResourceBundle());
                    controlPanel.setHandler((actionType) -> {
                        switch (actionType) {
                            case SAVE: {
                                optionsTreePanel.saveAndApplyAll();
                                break;
                            }
                            case CANCEL: {
                                break;
                            }
                            case APPLY_ONCE: {
                                optionsTreePanel.applyPreferencesChanges();
                                break;
                            }
                        }
                        dialog.close();
                    });
                    dialog.showCentered(frameModule.getFrame());
                }
            };

            ActionUtils.setupAction(optionsAction, resourceBundle, "optionsAction");
            optionsAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
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
            page.applyPreferencesChanges(options);
        }
    }

    @Override
    public void registerMenuAction() {
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuGroup(GuiFrameModuleApi.TOOLS_MENU_ID, new MenuGroup(TOOLS_OPTIONS_MENU_GROUP_ID, new MenuPosition(PositionMode.BOTTOM_LAST), SeparationMode.AROUND));
        getOptionsAction();
        menuModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, optionsAction, new MenuPosition(TOOLS_OPTIONS_MENU_GROUP_ID));
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
