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
package org.exbin.framework.help.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.App;
import org.exbin.framework.action.api.ActionConsts;
import org.exbin.framework.action.api.ActionModuleApi;
import org.exbin.framework.language.api.LanguageModuleApi;

/**
 * Help action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class HelpAction extends AbstractAction {

    public static final String ACTION_ID = "helpAction";
    public static final String HELP_SET_FILE = "help/help.hs";

    private final java.util.ResourceBundle resourceBundle = App.getModule(LanguageModuleApi.class).getBundle(HelpAction.class);

    private HelpSet mainHelpSet;
    private HelpBroker mainHelpBroker;
    private ActionListener helpActionLisneter;

    public HelpAction() {
    }

    public void setup() {
        ActionModuleApi actionModule = App.getModule(ActionModuleApi.class);
        actionModule.initAction(this, resourceBundle, ACTION_ID);
        putValue(ActionConsts.ACTION_DIALOG_MODE, true);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));

        String path;
        try {
            path = getAppDirectory().getCanonicalPath();
            mainHelpSet = getHelpSet(path + "/" + HELP_SET_FILE);
            if (mainHelpSet != null) {
                // Temporary for Java webstart, include help in jar later
                mainHelpBroker = mainHelpSet.createHelpBroker();
                // CSH.setHelpIDString(helpContextMenuItem, "top");
                helpActionLisneter = new CSH.DisplayHelpFromSource(mainHelpBroker);
                // helpContextMenuItem.addActionListener(helpActionLisneter);
            }
        } catch (IOException ex) {
            Logger.getLogger(HelpAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        helpActionLisneter.actionPerformed(e);
    }

    /**
     * Finds the helpset file and create a HelpSet object.
     */
    @Nullable
    private HelpSet getHelpSet(String helpSetFile) {
        HelpSet helpSet = null;
        ClassLoader cl = getClass().getClassLoader();
        try {
            URL helpSetURL = HelpSet.findHelpSet(cl, helpSetFile);
            File appDirectory = getAppDirectory();
            File file = new File(appDirectory.getAbsolutePath() + "/" + HELP_SET_FILE);
            if (!file.exists()) {
                file = new File(appDirectory.getAbsolutePath() + "/../" + HELP_SET_FILE);
                if (!file.exists()) {
                    file = new File(System.getProperty("java.class.path"));
                    file = file.getParentFile();
                    file = new File(file + "/" + HELP_SET_FILE);
                }
            }
            if (helpSetURL == null) {
                helpSetURL = (file.toURI()).toURL();
            }
            helpSet = new HelpSet(null, helpSetURL);
        } catch (MalformedURLException | HelpSetException ex) {
            Logger.getLogger(HelpAction.class.getName()).log(Level.INFO, "HelpSet: " + helpSetFile + " not found", ex);
        }
        return helpSet;
    }
    
    File getAppDirectory() {
        // TODO
        return new File("");
    }
}
