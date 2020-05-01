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
package org.exbin.framework.gui.help;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.frame.api.GuiFrameModuleApi;
import org.exbin.framework.gui.help.api.GuiHelpModuleApi;
import org.exbin.framework.gui.menu.api.GuiMenuModuleApi;
import org.exbin.framework.gui.menu.api.MenuPosition;
import org.exbin.framework.gui.menu.api.PositionMode;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.xbup.plugin.XBModuleHandler;

/**
 * Implementation of XBUP framework help module.
 *
 * @version 0.2.0 2016/07/14
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class GuiHelpModule implements GuiHelpModuleApi {

    private XBApplication application;
    private final java.util.ResourceBundle bundle = LanguageUtils.getResourceBundleByClass(GuiHelpModule.class);
    private HelpSet helpSet;
    private HelpBroker helpBroker;
    private ActionListener helpActionLisneter;

    private Action helpAction;

    public GuiHelpModule() {
    }

    @Override
    public void init(XBModuleHandler moduleHandler) {
        this.application = (XBApplication) moduleHandler;

        String path = ".";
        try {
            path = (new File(".")).getCanonicalPath();
        } catch (IOException ex) {
            Logger.getLogger(GuiHelpModule.class.getName()).log(Level.SEVERE, null, ex);
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
    public void unregisterModule(String moduleId) {
    }

    @Nonnull
    @Override
    public Action getHelpAction() {
        if (helpAction == null) {
            helpAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    helpActionLisneter.actionPerformed(e);
                }
            };
            ActionUtils.setupAction(helpAction, bundle, "helpAction");
            helpAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
            helpAction.putValue(Action.ACCELERATOR_KEY, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        }

        return helpAction;
    }

    @Override
    public void registerMainMenu() {
        GuiMenuModuleApi menuModule = application.getModuleRepository().getModuleByInterface(GuiMenuModuleApi.class);
        menuModule.registerMenuItem(GuiFrameModuleApi.HELP_MENU_ID, MODULE_ID, getHelpAction(), new MenuPosition(PositionMode.TOP));
    }

    /**
     * Finds the helpset file and create a HelpSet object.
     */
    @Nonnull
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
