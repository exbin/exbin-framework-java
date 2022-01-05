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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import org.exbin.framework.service.XBFileType;
import org.exbin.framework.service.YamlFileType;
import org.exbin.xbup.catalog.convert.XBCatalogYaml;
import org.exbin.xbup.core.block.declaration.catalog.XBCBlockDecl;
import org.exbin.xbup.core.block.declaration.catalog.XBCFormatDecl;
import org.exbin.xbup.core.block.declaration.catalog.XBCGroupDecl;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCBlockRev;
import org.exbin.xbup.core.catalog.base.XBCBlockSpec;
import org.exbin.xbup.core.catalog.base.XBCFormatRev;
import org.exbin.xbup.core.catalog.base.XBCFormatSpec;
import org.exbin.xbup.core.catalog.base.XBCGroupRev;
import org.exbin.xbup.core.catalog.base.XBCGroupSpec;
import org.exbin.xbup.core.catalog.base.XBCItem;
import org.exbin.xbup.core.catalog.base.XBCRev;
import org.exbin.xbup.core.catalog.base.XBCSpec;
import org.exbin.xbup.core.catalog.base.service.XBCRevService;
import org.exbin.xbup.core.catalog.base.service.XBCSpecService;
import org.exbin.xbup.core.parser.basic.convert.XBTTypeUndeclaringFilter;
import org.exbin.xbup.core.parser.token.event.XBEventWriter;
import org.exbin.xbup.core.parser.token.event.convert.XBTEventListenerToListener;
import org.exbin.xbup.core.parser.token.event.convert.XBTListenerToEventListener;
import org.exbin.xbup.core.parser.token.event.convert.XBTToXBEventConvertor;
import org.exbin.xbup.core.serial.XBPSerialWriter;
import org.exbin.xbup.core.serial.XBSerializable;

/**
 * Export catalog item action.
 *
 * @version 0.2.2 2021/12/24
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ExportItemAction extends AbstractAction {

    private final XBCatalogYaml catalogYaml = new XBCatalogYaml();
    private XBACatalog catalog;
    private XBCSpecService specService;
    private XBCRevService revService;

    private Component parentComponent;
    private XBCItem currentItem;

    public ExportItemAction() {
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
    public void actionPerformed(@Nullable ActionEvent arg0) {
        if (currentItem != null) {
            JFileChooser exportFileChooser = new JFileChooser();
            YamlFileType yamlFileType = new YamlFileType();
            XBFileType xbFileType = new XBFileType();
            exportFileChooser.addChoosableFileFilter(yamlFileType);
            exportFileChooser.addChoosableFileFilter(xbFileType);
            exportFileChooser.setAcceptAllFileFilterUsed(true);
            if (exportFileChooser.showSaveDialog(parentComponent) == JFileChooser.APPROVE_OPTION) {
                if (exportFileChooser.getFileFilter() == yamlFileType) {
                    FileWriter fileWriter;
                    try {
                        fileWriter = new FileWriter(exportFileChooser.getSelectedFile().getAbsolutePath());
                        try {
                            catalogYaml.exportCatalogItem(currentItem, fileWriter);
                        } finally {
                            fileWriter.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ExportItemAction.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (exportFileChooser.getFileFilter() == xbFileType) {
                    if (currentItem instanceof XBCSpec) {
                        FileOutputStream output = null;
                        try {
                            output = new FileOutputStream(exportFileChooser.getSelectedFile().getAbsolutePath());
                            long revision = revService.findMaxRevXB((XBCSpec) currentItem);
                            XBCRev specRev = revService.findRevByXB((XBCSpec) currentItem, revision);
                            XBSerializable decl
                                    = currentItem instanceof XBCFormatSpec ? specService.getFormatDeclAsLocal(new XBCFormatDecl((XBCFormatRev) specRev, catalog))
                                            : currentItem instanceof XBCGroupSpec ? specService.getGroupDeclAsLocal(new XBCGroupDecl((XBCGroupRev) specRev, catalog))
                                                    : currentItem instanceof XBCBlockSpec ? specService.getBlockDeclAsLocal(new XBCBlockDecl((XBCBlockRev) specRev, catalog)) : null;
                            XBTTypeUndeclaringFilter typeProcessing = new XBTTypeUndeclaringFilter(catalog);
                            typeProcessing.attachXBTListener(new XBTEventListenerToListener(new XBTToXBEventConvertor(new XBEventWriter(output))));
                            XBPSerialWriter writer = new XBPSerialWriter(new XBTListenerToEventListener(typeProcessing));
                            writer.write(decl);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(ExportItemAction.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(ExportItemAction.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            if (output != null) {
                                try {
                                    output.close();
                                } catch (IOException ex) {
                                    Logger.getLogger(ExportItemAction.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    } else {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                } else {
                    throw new IllegalStateException("Unknown file type");
                }
            }
        }
    }
    
    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;

        specService = catalog == null ? null : catalog.getCatalogService(XBCSpecService.class);
        revService = catalog == null ? null : catalog.getCatalogService(XBCRevService.class);

        catalogYaml.setCatalog(catalog);
    }
}
