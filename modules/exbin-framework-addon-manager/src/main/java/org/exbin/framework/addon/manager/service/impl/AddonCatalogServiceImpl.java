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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.exbin.framework.App;
import org.exbin.framework.addon.manager.api.AddonManagerModuleApi;
import org.exbin.framework.addon.manager.model.AddonRecord;
import org.exbin.framework.addon.manager.model.DependencyRecord;
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
    private static final String CATALOG_DEV_URL = "https://bined.exbin.org/addon-dev/";
    private final Map<AddonRecord, String> iconPaths = new HashMap();
    private final List<IconChangeListener> iconChangeListeners = new ArrayList<>();

    @Nonnull
    @Override
    public AddonsListResult searchForAddons(String searchCondition) {
        AddonManagerModuleApi addonManagerModule = App.getModule(AddonManagerModuleApi.class);
        List<AddonRecord> searchResult = new ArrayList<>();
        URL seachUrl;
        try {
            seachUrl = new URL((addonManagerModule.isDevMode() ? CATALOG_URL : CATALOG_URL) + "api/?op=list");
            try (InputStream searchStream = seachUrl.openStream()) {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(searchStream);
                NodeList resultNodes = document.getElementsByTagName("result");
                if (resultNodes.getLength() > 0) {
                    Node resultNode = resultNodes.item(0);
                    NodeList resultNodeList = resultNode.getChildNodes();
                    int childCount = resultNodeList.getLength();
                    for (int resultNodeIndex = 0; resultNodeIndex < childCount; resultNodeIndex++) {
                        Node childNode = resultNodeList.item(resultNodeIndex);
                        if ("module".equals(childNode.getNodeName())) {
                            NamedNodeMap moduleAttributes = childNode.getAttributes();
                            Node moduleIdNode = moduleAttributes.getNamedItem("id");
                            String moduleId = moduleIdNode.getNodeValue();
                            Node moduleNameNode = moduleAttributes.getNamedItem("name");
                            String moduleName = moduleNameNode.getNodeValue();
                            AddonRecord record = new AddonRecord(moduleId, moduleName);
                            NodeList moduleChildNodes = childNode.getChildNodes();
                            int moduleChildCount = moduleChildNodes.getLength();
                            for (int moduleNodeIndex = 0; moduleNodeIndex < moduleChildCount; moduleNodeIndex++) {
                                Node moduleChildNode = moduleChildNodes.item(moduleNodeIndex);
                                if ("description".equals(moduleChildNode.getNodeName())) {
                                    record.setDescription(moduleChildNode.getTextContent());
                                } else if ("version".equals(moduleChildNode.getNodeName())) {
                                    record.setVersion(moduleChildNode.getTextContent());
                                } else if ("homepage".equals(moduleChildNode.getNodeName())) {
                                    record.setHomepage(moduleChildNode.getTextContent());
                                } else if ("provider".equals(moduleChildNode.getNodeName())) {
                                    record.setProvider(moduleChildNode.getTextContent());
                                } else if ("dependency".equals(moduleChildNode.getNodeName())) {
                                    NodeList depencenyNodes = moduleChildNode.getChildNodes();
                                    List<DependencyRecord> dependencyRecords = new ArrayList<>();
                                    int dependecyCount = depencenyNodes.getLength();
                                    for (int depNodeIndex = 0; depNodeIndex < dependecyCount; depNodeIndex++) {
                                        Node dependencyNode = depencenyNodes.item(depNodeIndex);
                                        if ("module".equals(dependencyNode.getNodeName())) {
                                            Node dependencyModuleId = dependencyNode.getAttributes().getNamedItem("id");
                                            DependencyRecord dependencyRecord = new DependencyRecord(dependencyModuleId.getNodeValue());
                                            dependencyRecord.setType(DependencyRecord.Type.MODULE);
                                            dependencyRecords.add(dependencyRecord);
                                        } else if ("library".equals(dependencyNode.getNodeName())) {
                                            Node libraryMaven = dependencyNode.getAttributes().getNamedItem("maven");
                                            if (libraryMaven != null) {
                                                DependencyRecord dependencyRecord = new DependencyRecord(libraryMaven.getNodeValue());
                                                dependencyRecord.setType(DependencyRecord.Type.MAVEN_LIBRARY);
                                                dependencyRecords.add(dependencyRecord);
                                            } else {
                                                Node libraryJar = dependencyNode.getAttributes().getNamedItem("jar");
                                                DependencyRecord dependencyRecord = new DependencyRecord(libraryJar.getNodeValue());
                                                dependencyRecord.setType(DependencyRecord.Type.JAR_LIBRARY);
                                                dependencyRecords.add(dependencyRecord);
                                            }
                                        }
                                    }
                                    record.setDependencies(dependencyRecords);
                                } else if ("icon".equals(moduleChildNode.getNodeName())) {
                                    iconPaths.put(record, moduleChildNode.getNodeValue());
                                }
                            }
                            searchResult.add(record);
                        }
                    }
                }
            } catch (IOException | SAXException | ParserConfigurationException ex) {
                Logger.getLogger(AddonCatalogServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                return new ServiceFailureResult(ex);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(AddonCatalogServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return new ServiceFailureResult(ex);
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

    public void addIconChangeListener(IconChangeListener listener) {
        iconChangeListeners.add(listener);
    }

    public void removeIconChangeListener(IconChangeListener listener) {
        iconChangeListeners.remove(listener);
    }

    public interface IconChangeListener {

        void iconsChanged();
    }

    @ParametersAreNonnullByDefault
    public static class ServiceFailureResult implements AddonsListResult {

        private final Exception ex;

        public ServiceFailureResult(Exception ex) {
            this.ex = ex;
        }

        @Nonnull
        public Exception getException() {
            return ex;
        }

        @Override
        public int itemsCount() {
            return 0;
        }

        @Override
        public AddonRecord getLazyItem(int index) {
            throw new IllegalStateException();
        }
    }
}
