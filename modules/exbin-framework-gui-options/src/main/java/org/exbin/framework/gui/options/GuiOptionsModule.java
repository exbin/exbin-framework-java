/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.gui.options;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.menu.api.GuiMenuModuleApi;
import org.exbin.framework.gui.menu.api.MenuGroup;
import org.exbin.framework.gui.menu.api.MenuPosition;
import org.exbin.framework.gui.menu.api.PositionMode;
import org.exbin.framework.gui.menu.api.SeparationMode;
import org.exbin.framework.gui.options.api.GuiOptionsModuleApi;
import org.exbin.framework.gui.options.panel.OptionsTreePanel;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.gui.utils.panel.OptionsControlPanel;
import org.exbin.xbup.plugin.XBModuleHandler;
import org.exbin.framework.gui.options.api.OptionsCapable;
import org.exbin.framework.gui.options.api.OptionsPathItem;
import org.exbin.framework.gui.utils.ComponentResourceProvider;

/**
 * Implementation of framework options module.
 *
 * @version 0.2.1 2019/07/14
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GuiOptionsModule implements GuiOptionsModuleApi {

    private XBApplication application;
    private ResourceBundle resourceBundle;

    private Action optionsAction;
    private OptionsTreePanel optionsTreePanel;
    private final Map<List<OptionsPathItem>, OptionsCapable> optionsPanels = new HashMap<>();
    private OptionsCapable mainOptionsExt = null;
    private OptionsCapable appearanceOptionsExt = null;

    public GuiOptionsModule() {
    }

    @Override
    public void init(XBModuleHandler moduleHandler) {
        this.application = (XBApplication) moduleHandler;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

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
                    if (optionsTreePanel == null) {
                        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                        optionsTreePanel = new OptionsTreePanel(frameModule.getFrameHandler());
                        optionsPanels.entrySet().forEach((optionsPanel) -> {
                            optionsTreePanel.addOptionsPanel(optionsPanel.getValue(), optionsPanel.getKey());
                        });
                        if (mainOptionsExt != null) {
                            optionsTreePanel.extendMainOptionsPanel(mainOptionsExt);
                        }
                        if (appearanceOptionsExt != null) {
                            optionsTreePanel.extendAppearanceOptionsPanel(appearanceOptionsExt);
                        }
                        optionsTreePanel.setLanguageLocales(application.getLanguageLocales());
                    }

                    optionsTreePanel.setAppEditor(application);
                    optionsTreePanel.setPreferences(application.getAppPreferences());

                    GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
                    OptionsControlPanel controlPanel = new OptionsControlPanel();
                    JPanel dialogPanel = WindowUtils.createDialogPanel(optionsTreePanel, controlPanel);
                    final DialogWrapper dialog = frameModule.createDialog(dialogPanel);
                    frameModule.setDialogTitle(dialog, optionsTreePanel.getResourceBundle());
                    controlPanel.setHandler((actionType) -> {
                        switch (actionType) {
                            case SAVE: {
                                optionsTreePanel.applyPreferencesChanges();
                                optionsTreePanel.saveToPreferences();
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
                    dialog.center(dialog.getParent());
                    dialog.show();
                }
            };

            ActionUtils.setupAction(optionsAction, resourceBundle, "optionsAction");
            optionsAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        }

        return optionsAction;
    }

    @Override
    public void addOptionsPanel(OptionsCapable optionsPanel, List<OptionsPathItem> path) {
        optionsPanels.put(path, optionsPanel);
        if (optionsTreePanel != null) {
            optionsTreePanel.addOptionsPanel(optionsPanel, path);
        }
    }

    @Override
    public void addOptionsPanel(OptionsCapable optionsPanel, String parentPath) {
        ArrayList<OptionsPathItem> optionsPath = new ArrayList<>();
        String[] pathParts = parentPath.split("/");
        for (String pathPart : pathParts) {
            if (!pathPart.isEmpty()) {
                optionsPath.add(new OptionsPathItem(pathPart, null));
            }
        }
        if (optionsPanel instanceof ComponentResourceProvider) {
            ResourceBundle componentResourceBundle = ((ComponentResourceProvider) optionsPanel).getResourceBundle();
            String optionsDefaultName = componentResourceBundle.getString("options.name");
            String optionsDefaultCaption = componentResourceBundle.getString("options.caption");
            optionsPath.add(new OptionsPathItem(optionsDefaultName, optionsDefaultCaption));
        }
        addOptionsPanel(optionsPanel, optionsPath);
    }

    @Override
    public void addOptionsPanel(OptionsCapable optionsPanel) {
        String optionsDefaultPath;
        if (optionsPanel instanceof ComponentResourceProvider) {
            ResourceBundle componentResourceBundle = ((ComponentResourceProvider) optionsPanel).getResourceBundle();
            optionsDefaultPath = componentResourceBundle.getString("options.path");
        } else {
            optionsDefaultPath = null;
        }

        addOptionsPanel(optionsPanel, optionsDefaultPath);
    }

    @Override
    public void extendMainOptionsPanel(OptionsCapable panel) {
        mainOptionsExt = panel;
        if (optionsTreePanel != null) {
            optionsTreePanel.extendMainOptionsPanel(panel);
        }
    }

    @Override
    public void extendAppearanceOptionsPanel(OptionsCapable panel) {
        appearanceOptionsExt = panel;
        if (optionsTreePanel != null) {
            optionsTreePanel.extendAppearanceOptionsPanel(panel);
        }
    }

    @Override
    public void registerMenuAction() {
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuGroup(GuiFrameModuleApi.TOOLS_MENU_ID, new MenuGroup(TOOLS_OPTIONS_MENU_GROUP_ID, new MenuPosition(PositionMode.BOTTOM_LAST), SeparationMode.AROUND));
        getOptionsAction();
        menuModule.registerMenuItem(GuiFrameModuleApi.TOOLS_MENU_ID, MODULE_ID, optionsAction, new MenuPosition(TOOLS_OPTIONS_MENU_GROUP_ID));
    }
}
