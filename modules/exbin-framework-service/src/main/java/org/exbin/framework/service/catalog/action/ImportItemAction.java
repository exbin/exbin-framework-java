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
package org.exbin.framework.service.catalog.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import org.exbin.framework.service.YamlFileType;
import org.exbin.xbup.catalog.convert.XBCatalogYaml;
import org.exbin.xbup.catalog.entity.XBENode;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCItem;
import org.exbin.xbup.core.catalog.base.XBCNode;

/**
 * Export catalog item action.
 *
 * @version 0.2.2 2021/12/24
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ImportItemAction extends AbstractAction {

    private final XBCatalogYaml catalogYaml = new XBCatalogYaml();

    private Component parentComponent;
    private XBCItem currentItem;

    public ImportItemAction() {
    }

    @Nullable
    public XBCItem getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(@Nullable XBCItem currentItem) {
        this.currentItem = currentItem;
    }

    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if ((currentItem != null) && (currentItem instanceof XBCNode)) {
            JFileChooser importFileChooser = new JFileChooser();
            importFileChooser.addChoosableFileFilter(new YamlFileType());
            importFileChooser.setAcceptAllFileFilterUsed(true);
            if (importFileChooser.showOpenDialog(parentComponent) == JFileChooser.APPROVE_OPTION) {
                FileInputStream fileStream;
                try {
                    fileStream = new FileInputStream(importFileChooser.getSelectedFile().getAbsolutePath());
                    try {
                        catalogYaml.importCatalogItem(fileStream, (XBENode) currentItem);
                    } finally {
                        fileStream.close();
                    }
                } catch (FileNotFoundException ex) {
                } catch (IOException ex) {
                    Logger.getLogger(ImportItemAction.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void setCatalog(XBACatalog catalog) {
        catalogYaml.setCatalog(catalog);
    }
}
