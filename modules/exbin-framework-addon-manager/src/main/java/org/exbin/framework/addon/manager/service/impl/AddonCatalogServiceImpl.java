/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.addon.manager.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.exbin.framework.addon.manager.model.AddonRecord;
import org.exbin.framework.addon.manager.service.AddonCatalogService;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Addon catalog service implementation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonCatalogServiceImpl implements AddonCatalogService {

    private static final String CATALOG_URL = "https://bined.exbin.org/addon/";

    @Nonnull
    @Override
    public AddonsListResult searchForAddons(String searchCondition) {
        List<AddonRecord> searchResult = new ArrayList<>();
        URL seachUrl;
        try {
            seachUrl = new URL(CATALOG_URL + "api/?op=list");
            try (InputStream searchStream = seachUrl.openStream()) {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(searchStream);
                NodeList resultNodes = document.getElementsByTagName("result");
                if (resultNodes.getLength() > 0) {
                    Node resultNode = resultNodes.item(0);
                    NodeList resultNodeList = resultNode.getChildNodes();
                    int childCount = resultNodeList.getLength();
                    for (int i = 0; i < childCount; i++) {
                        Node childNode = resultNodeList.item(i);
                        if ("module".equals(childNode.getNodeName())) {
                            NamedNodeMap moduleAttributes = childNode.getAttributes();
                            Node moduleIdNode = moduleAttributes.getNamedItem("id");
                            String moduleId = moduleIdNode.getNodeValue();
                            Node moduleNameNode = moduleAttributes.getNamedItem("name");
                            String moduleName = moduleNameNode.getNodeValue();
                            AddonRecord record = new AddonRecord(moduleId, moduleName);
                            NodeList moduleChildNodes = childNode.getChildNodes();
                            int moduleChildCount = moduleChildNodes.getLength();
                            for (int j = 0; j < moduleChildCount; j++) {
                                Node moduleChildNode = moduleChildNodes.item(j);
                                if ("name".equals(moduleChildNode.getNodeName())) {

                                }
                            }
                            searchResult.add(record);
                        }
                    }
                }
            } catch (IOException | SAXException | ParserConfigurationException ex) {
                Logger.getLogger(AddonCatalogServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(AddonCatalogServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new AddonsListResult() {
            @Override
            public int itemsCount() {
                return searchResult.size();
            }

            @Nonnull
            @Override
            public AddonRecord getLazyItem(int index) {
                return searchResult.get(index);
            }
        };
    }
}
