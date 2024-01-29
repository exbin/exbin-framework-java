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
package org.exbin.framework.options.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.window.api.WindowModuleApi;
import org.exbin.framework.options.gui.OptionsTreePanel;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.framework.utils.ActionUtils;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.window.api.WindowHandler;
import org.exbin.framework.window.api.gui.OptionsControlPanel;

/**
 * Options action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsAction extends AbstractAction {

    public static final String ACTION_ID = "optionsAction";

    private ResourceBundle resourceBundle;
    private OptionsPagesProvider optionsPagesProvider;

    public OptionsAction() {
    }

    public void setup(ResourceBundle resourceBundle, OptionsPagesProvider optionsPagesProvider) {
        this.resourceBundle = resourceBundle;
        this.optionsPagesProvider = optionsPagesProvider;

        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
        WindowModuleApi windowModule = App.getModule(WindowModuleApi.class);
        OptionsTreePanel optionsTreePanel = new OptionsTreePanel(windowModule.getFrameHandler());
        optionsPagesProvider.registerOptionsPages(optionsTreePanel);
        Dimension preferredSize = optionsTreePanel.getPreferredSize();
        optionsTreePanel.setPreferredSize(new Dimension(preferredSize.width + 200, preferredSize.height + 200));
        optionsTreePanel.setPreferences(preferencesModule.getAppPreferences());
        optionsTreePanel.pagesFinished();
        optionsTreePanel.loadAllFromPreferences();

        OptionsControlPanel controlPanel = new OptionsControlPanel();
        final WindowHandler dialog = windowModule.createDialog(optionsTreePanel, controlPanel);
        windowModule.setWindowTitle(dialog, optionsTreePanel.getResourceBundle());
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
        dialog.showCentered(windowModule.getFrame());
    }

    @ParametersAreNonnullByDefault
    public interface OptionsPagesProvider {

        void registerOptionsPages(OptionsTreePanel optionsTreePanel);
    }
}
