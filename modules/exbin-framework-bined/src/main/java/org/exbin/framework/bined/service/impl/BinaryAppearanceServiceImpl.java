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
package org.exbin.framework.bined.service.impl;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
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
        binedModule.getShowValuesPanelHandler().setShowValuesPanel(showValuesPanel);
    }
}
