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
package org.exbin.framework.bined;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.exbin.bined.extended.layout.ExtendedCodeAreaLayoutProfile;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Code area theme handler.
 *
 * @version 0.2.1 2019/07/06
 * @author ExBin Project (http://exbin.org)
 */
public class LayoutHandler {

    private final BinaryEditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action showHeaderAction;
    private Action showRowPositionAction;

    public LayoutHandler(XBApplication application, BinaryEditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(BinedModule.class);
    }

    public void init() {
        showHeaderAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtendedCodeAreaLayoutProfile layoutProfile = editorProvider.getCodeArea().getLayoutProfile();
                layoutProfile.setShowHeader(!editorProvider.getCodeArea().getLayoutProfile().isShowHeader());
                editorProvider.getCodeArea().setLayoutProfile(layoutProfile);
            }
        };
        ActionUtils.setupAction(showHeaderAction, resourceBundle, "showHeaderAction");
        showHeaderAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
        showHeaderAction.putValue(Action.SELECTED_KEY, editorProvider.getCodeArea().getLayoutProfile().isShowHeader());

        showRowPositionAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ExtendedCodeAreaLayoutProfile layoutProfile = editorProvider.getCodeArea().getLayoutProfile();
                layoutProfile.setShowRowPosition(!editorProvider.getCodeArea().getLayoutProfile().isShowRowPosition());
                editorProvider.getCodeArea().setLayoutProfile(layoutProfile);
            }
        };
        ActionUtils.setupAction(showRowPositionAction, resourceBundle, "showRowPositionAction");
        showRowPositionAction.putValue(ActionUtils.ACTION_TYPE, ActionUtils.ActionType.CHECK);
        showRowPositionAction.putValue(Action.SELECTED_KEY, editorProvider.getCodeArea().getLayoutProfile().isShowRowPosition());
    }

    public Action getShowHeaderAction() {
        return showHeaderAction;
    }

    public Action getShowRowPositionAction() {
        return showRowPositionAction;
    }
}
