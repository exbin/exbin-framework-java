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
package org.exbin.framework.editor.xbup.gui;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JComponent;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.data.gui.cell.ComponentPropertyTableCellPanel;
import org.exbin.framework.frame.api.FrameModuleApi;
import org.exbin.framework.utils.WindowUtils;
import org.exbin.framework.utils.WindowUtils.DialogWrapper;
import org.exbin.framework.utils.handler.DefaultControlHandler;
import org.exbin.framework.utils.gui.DefaultControlPanel;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.parser.XBProcessingException;
import org.exbin.xbup.core.parser.token.XBTToken;
import org.exbin.xbup.core.parser.token.convert.XBTListenerToToken;
import org.exbin.xbup.parser_tree.XBATreeParamExtractor;
import org.exbin.xbup.parser_tree.XBTTreeDocument;
import org.exbin.xbup.parser_tree.XBTTreeNode;
import org.exbin.xbup.parser_tree.XBTTreeReader;
import org.exbin.xbup.plugin.XBPluginRepository;

/**
 * Properties table cell panel.
 *
 * @version 0.2.1 2019/06/27
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class XBPropertyTableCellPanel extends ComponentPropertyTableCellPanel {

    private XBApplication application;
    private XBACatalog catalog;
    private final XBPluginRepository pluginRepository;
    private XBTTreeNode node;
    private final XBTTreeDocument doc;
    private final int row;

    public XBPropertyTableCellPanel(JComponent cellComponent, XBACatalog catalog, XBPluginRepository pluginRepository, XBTTreeNode node, XBTTreeDocument doc, int row) {
        super(cellComponent);

        this.catalog = catalog;
        this.pluginRepository = pluginRepository;
        this.node = node;
        this.doc = doc;
        this.row = row;
        init();
    }

    public XBPropertyTableCellPanel(XBACatalog catalog, XBPluginRepository pluginRepository, XBTTreeNode node, XBTTreeDocument doc, int row) {
        super();

        this.catalog = catalog;
        this.pluginRepository = pluginRepository;
        this.node = node;
        this.doc = doc;
        this.row = row;
        init();
    }

    private void init() {
        setEditorAction((ActionEvent e) -> {
            performEditorAction();
        });
    }

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    public void performEditorAction() {
        // TODO: Subparting instead of copy until modify operation
        XBATreeParamExtractor paramExtractor = new XBATreeParamExtractor(node, catalog);
        paramExtractor.setParameterIndex(row);
        XBTTreeNode paramNode = new XBTTreeNode();
        XBTTreeReader reader = new XBTTreeReader(paramNode);
        int depth = 0;
        try {
            do {
                XBTToken token = paramExtractor.pullXBTToken();
                switch (token.getTokenType()) {
                    case BEGIN: {
                        depth++;
                        break;
                    }
                    case END: {
                        depth--;
                        break;
                    }
                }
                XBTListenerToToken.tokenToListener(token, reader);
            } while (depth > 0);
        } catch (XBProcessingException | IOException ex) {
            Logger.getLogger(XBPropertyTableCellPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        FrameModuleApi frameModule = application.getModuleRepository().getModuleByInterface(FrameModuleApi.class);
        ModifyBlockPanel panel = new ModifyBlockPanel();
        panel.setApplication(application);
        panel.setCatalog(catalog);
        panel.setPluginRepository(pluginRepository);
        panel.setNode(paramNode, doc);
        DefaultControlPanel controlPanel = new DefaultControlPanel();
        final DialogWrapper dialog = frameModule.createDialog(panel, controlPanel);
        WindowUtils.addHeaderPanel(dialog.getWindow(), ModifyBlockPanel.class, panel.getResourceBundle());
        controlPanel.setHandler((DefaultControlHandler.ControlActionType actionType) -> {
            if (actionType == DefaultControlHandler.ControlActionType.OK) {
                XBTTreeNode newNode = panel.getNode();

                // TODO
            }

            dialog.close();
            dialog.dispose();
        });
        dialog.showCentered(this);
    }

    public XBACatalog getCatalog() {
        return catalog;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;
    }

    public XBTTreeNode getNode() {
        return node;
    }

    public void setNode(XBTTreeNode node) {
        this.node = node;
    }
}
