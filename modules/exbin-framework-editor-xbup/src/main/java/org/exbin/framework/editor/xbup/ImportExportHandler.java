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
package org.exbin.framework.editor.xbup;

import java.util.ResourceBundle;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.action.ExportItemAsFileAction;
import org.exbin.framework.editor.xbup.action.ImportFileAsItemAction;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.ActionUtils;
import org.exbin.framework.gui.utils.LanguageUtils;

/**
 * Import / export handler.
 *
 * @version 0.2.0 2016/02/13
 * @author ExBin Project (http://exbin.org)
 */
public class ImportExportHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;

    private Action importItemAction;
    private Action exportItemAction;

    public ImportExportHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorXbupModule.class);
    }

    public void init() {
        importItemAction = new ImportFileAsItemAction();
        ActionUtils.setupAction(importItemAction, resourceBundle, "importItemAction");
        importItemAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);

        exportItemAction = new ExportItemAsFileAction();
        ActionUtils.setupAction(exportItemAction, resourceBundle, "exportItemAction");
        exportItemAction.putValue(ActionUtils.ACTION_DIALOG_MODE, true);
    }

    public Action getImportItemAction() {
        return importItemAction;
    }

    public Action getExportItemAction() {
        return exportItemAction;
    }
}
