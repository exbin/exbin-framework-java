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
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.Action;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.editor.xbup.action.CatalogBrowserAction;
import org.exbin.framework.gui.editor.api.EditorProvider;
import org.exbin.framework.gui.utils.LanguageUtils;
import org.exbin.xbup.core.catalog.XBACatalog;

/**
 * Catalog browser handler.
 *
 * @version 0.2.1 2020/07/19
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CatalogBrowserHandler {

    private final EditorProvider editorProvider;
    private final XBApplication application;
    private final ResourceBundle resourceBundle;
    private XBACatalog catalog;

    private CatalogBrowserAction catalogBrowserAction = null;

    public CatalogBrowserHandler(XBApplication application, EditorProvider editorProvider) {
        this.application = application;
        this.editorProvider = editorProvider;
        resourceBundle = LanguageUtils.getResourceBundleByClass(EditorXbupModule.class);
    }

    public void init() {
    }

    @Nonnull
    public Action getCatalogBrowserAction() {
        if (catalogBrowserAction == null) {
            catalogBrowserAction = new CatalogBrowserAction();
            catalogBrowserAction.setApplication(application);
            catalogBrowserAction.setCatalog(catalog);
        }
        return catalogBrowserAction;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
        if (catalogBrowserAction != null) {
            catalogBrowserAction.setCatalog(catalog);
        }
    }
}
