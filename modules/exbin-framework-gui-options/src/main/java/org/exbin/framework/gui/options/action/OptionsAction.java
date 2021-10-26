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
package org.exbin.framework.gui.options.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.options.gui.OptionsTreePanel;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.WindowUtils;
import org.exbin.framework.gui.utils.gui.OptionsControlPanel;

/**
 * Options action.
 *
 * @version 0.2.2 2021/10/26
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class OptionsAction extends AbstractAction {

    public static final String ACTION_ID = "optionsAction";

    private ResourceBundle resourceBundle;
    private XBApplication application;
    private OptionsPagesProvider optionsPagesProvider;

    public OptionsAction() {
    }

    public void setup(XBApplication application, ResourceBundle resourceBundle, OptionsPagesProvider optionsPagesProvider) {
        this.application = application;
        this.resourceBundle = resourceBundle;
        this.optionsPagesProvider = optionsPagesProvider;

        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GuiFrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(GuiFrameModuleApi.class);
        OptionsTreePanel optionsTreePanel = new OptionsTreePanel(frameModule.getFrameHandler());
        optionsPagesProvider.registerOptionsPages(optionsTreePanel);
        optionsTreePanel.setLanguageLocales(application.getLanguageLocales());

        optionsTreePanel.setAppEditor(application);
        optionsTreePanel.setPreferences(application.getAppPreferences());
        optionsTreePanel.pagesFinished();
        optionsTreePanel.loadAllFromPreferences();

        OptionsControlPanel controlPanel = new OptionsControlPanel();
        JPanel dialogPanel = WindowUtils.createDialogPanel(optionsTreePanel, controlPanel);
        final WindowUtils.DialogWrapper dialog = frameModule.createDialog(dialogPanel);
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

    @ParametersAreNonnullByDefault
    public interface OptionsPagesProvider {

        void registerOptionsPages(OptionsTreePanel optionsTreePanel);
    }
}
