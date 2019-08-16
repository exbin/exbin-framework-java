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
package org.exbin.framework.bined.service.impl;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.bined.BinaryEditorProvider;
import org.exbin.framework.bined.BinedModule;
import org.exbin.framework.bined.service.BinaryAppearanceService;

/**
 * Appearance service implementation.
 *
 * @version 0.2.1 2019/07/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class BinaryAppearanceServiceImpl implements BinaryAppearanceService {

    private final BinedModule binedModule;

    public BinaryAppearanceServiceImpl(BinedModule binedModule) {
        this.binedModule = binedModule;
    }

    @Override
    public boolean getWordWrapMode() {
        return binedModule.getEditorProvider().isWordWrapMode();
    }

    @Override
    public void setWordWrapMode(boolean mode) {
        binedModule.getEditorProvider().setWordWrapMode(mode);

        binedModule.getRowWrappingHandler().getViewLineWrapAction().putValue(Action.SELECTED_KEY, mode);
    }

    @Override
    public void setShowValuesPanel(boolean showValuesPanel) {
        BinaryEditorProvider editorProvider = binedModule.getEditorProvider();
        boolean valuesPanelVisible = editorProvider.isValuesPanelVisible();
        if (valuesPanelVisible != showValuesPanel) {
            if (showValuesPanel) {
                editorProvider.showValuesPanel();
            } else {
                editorProvider.hideValuesPanel();
            }
        }

        binedModule.getViewValuesPanelHandler().getShowValuesPanelAction().putValue(Action.SELECTED_KEY, showValuesPanel);
    }
}
