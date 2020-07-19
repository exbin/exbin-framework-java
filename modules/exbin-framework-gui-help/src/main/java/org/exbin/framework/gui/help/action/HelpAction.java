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
package org.exbin.framework.gui.help.action;

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
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Help action.
 *
 * @version 0.2.0 2020/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class HelpAction extends AbstractAction {

    public static final String ACTION_ID = "helpAction";

    private final java.util.ResourceBundle resourceBundle = LanguageUtils.getResourceBundleByClass(HelpAction.class);

    private HelpSet helpSet;
    private HelpBroker helpBroker;
    private ActionListener helpActionLisneter;

    public HelpAction() {
        init();
    }

    private void init() {
        ActionUtils.setupAction(this, resourceBundle, ACTION_ID);
        putValue(ActionUtils.ACTION_DIALOG_MODE, true);
        putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));

        String path = ".";
        try {
            path = (new File(".")).getCanonicalPath();
        } catch (IOException ex) {
            Logger.getLogger(HelpAction.class.getName()).log(Level.SEVERE, null, ex);
        }
        helpSet = getHelpSet(path + "/help/help.hs");
        if (helpSet != null) {
            // Temporary for Java webstart, include help in jar later
            helpBroker = helpSet.createHelpBroker();
            // CSH.setHelpIDString(helpContextMenuItem, "top");
            helpActionLisneter = new CSH.DisplayHelpFromSource(helpBroker);
            // helpContextMenuItem.addActionListener(helpActionLisneter);
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
        HelpSet hs = null;
        ClassLoader cl = getClass().getClassLoader();
        try {
            URL hsURL = HelpSet.findHelpSet(cl, helpSetFile);
            File file = new File("./help/help.hs");
            if (!file.exists()) {
                file = new File("./../help/help.hs");
            }
            if (hsURL == null) {
                hsURL = (file.toURI()).toURL();
            }
            hs = new HelpSet(null, hsURL);
        } catch (MalformedURLException | HelpSetException ex) {
            System.out.println("HelpSet: " + ex.getMessage());
            System.out.println("HelpSet: " + helpSetFile + " not found");
        }
        return hs;
    }
}
