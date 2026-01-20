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
package org.exbin.framework.addon.catalog.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.exbin.framework.App;
import org.exbin.framework.addon.catalog.AddonCatalogModule;
import org.exbin.framework.addon.manager.api.AddonCatalogService;
import org.exbin.framework.addon.manager.api.AddonCatalogServiceException;
import org.exbin.framework.addon.manager.api.AddonRecord;
import org.exbin.framework.addon.manager.api.DependencyRecord;
import org.exbin.framework.addon.manager.api.UpdateRecord;
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

    private String addonServiceUrl;
    private final Map<AddonRecord, String> iconPaths = new HashMap<>();
    private final List<IconChangeListener> iconChangeListeners = new ArrayList<>();

    public AddonCatalogServiceImpl() {
        AddonCatalogModule addonCatalogModule = App.getModule(AddonCatalogModule.class);
        this.addonServiceUrl = addonCatalogModule.getAddonServiceUrl();
    }

    public AddonCatalogServiceImpl(String addonServiceUrl) {
        this.addonServiceUrl = addonServiceUrl;
    }

    public void setAddonServiceUrl(String addonServiceUrl) {
        this.addonServiceUrl = addonServiceUrl;
    }

    @Override
    public int checkStatus(String version) throws AddonCatalogServiceException {
        URL checkUrl = createApiCall("check-" + version);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(checkUrl.openStream()))) {
            String line = reader.readLine();
            if (line == null || line.isEmpty()) {
                return -1;
            }
            return Integer.parseInt(line);
        } catch (IOException ex) {
            throw new AddonCatalogServiceException("Invalid response for status", ex);
        }
    }

    @Nonnull
    @Override
    public List<AddonRecord> searchForAddons(String searchCondition) throws AddonCatalogServiceException {
        List<AddonRecord> searchResult = new ArrayList<>();
        URL searchUrl = createApiCall("list");
        try (InputStream searchStream = searchUrl.openStream()) {
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
                        record.setAddon(true);
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
                            } else if ("license".equals(moduleChildNode.getNodeName())) {
                                if (moduleChildNode.hasAttributes()) {
                                    Node spdxNode = moduleChildNode.getAttributes().getNamedItem("spdx");
                                    if (spdxNode != null) {
                                        record.setLicenseSpdx(spdxNode.getNodeValue());
                                    }
                                    Node fileNode = moduleChildNode.getAttributes().getNamedItem("file");
                                    if (fileNode != null) {
                                        record.setLicenseRemoteFile(fileNode.getNodeValue());
                                    }
                                }
                                record.setLicense(moduleChildNode.getTextContent());
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
            throw new AddonCatalogServiceException(ex);
        }

        return searchResult;
    }

    @Nonnull
    @Override
    public AddonRecord getAddonDependency(String addonId) throws AddonCatalogServiceException {
        URL requestUrl = createApiCall("=addondep", "id=" + addonId);
        try (InputStream searchStream = requestUrl.openStream()) {
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
                        record.setAddon(true);
                        NodeList moduleChildNodes = childNode.getChildNodes();
                        int moduleChildCount = moduleChildNodes.getLength();
                        for (int moduleNodeIndex = 0; moduleNodeIndex < moduleChildCount; moduleNodeIndex++) {
                            Node moduleChildNode = moduleChildNodes.item(moduleNodeIndex);
                            if ("license".equals(moduleChildNode.getNodeName())) {
                                if (moduleChildNode.hasAttributes()) {
                                    Node spdxNode = moduleChildNode.getAttributes().getNamedItem("spdx");
                                    if (spdxNode != null) {
                                        record.setLicenseSpdx(spdxNode.getNodeValue());
                                    }
                                    Node fileNode = moduleChildNode.getAttributes().getNamedItem("file");
                                    if (fileNode != null) {
                                        record.setLicenseRemoteFile(fileNode.getNodeValue());
                                    }
                                }
                                record.setLicense(moduleChildNode.getTextContent());
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
                            }
                        }
                        return record;
                    }
                }
            }
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            throw new AddonCatalogServiceException("Error processing response for addon dependency for addon: " + addonId, ex);
        }

        throw new AddonCatalogServiceException("No record for addon: " + addonId);
    }

    @Nonnull
    @Override
    public String getAddonFile(String addonId) throws AddonCatalogServiceException {
        URL requestUrl = createApiCall("addonfile", "id=" + addonId);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(requestUrl.openStream()))) {
            String line = reader.readLine();
            if (line == null || line.isEmpty()) {
                throw new AddonCatalogServiceException("Empty response for file request for addon: " + addonId);
            }
            return line;
        } catch (IOException ex) {
            throw new AddonCatalogServiceException("Invalid response for file request for addon: " + addonId, ex);
        }
    }

    @Nonnull
    @Override
    public List<UpdateRecord> getUpdateRecords() throws AddonCatalogServiceException {
        List<UpdateRecord> records = new ArrayList<>();
        URL requestUrl = createApiCall("updates");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(requestUrl.openStream()))) {
            String line;
            do {
                line = reader.readLine();
                if (line != null && !line.isEmpty()) {
                    int split = line.indexOf(":");
                    records.add(new UpdateRecord(line.substring(0, split), line.substring(split + 1)));
                }
            } while (line != null);
        } catch (IOException ex) {
            throw new AddonCatalogServiceException("Invalid response for addon updates", ex);
        }

        return records;
    }

    @Nonnull
    @Override
    public String getModuleDetails(String addonId) throws AddonCatalogServiceException {
        URL requestUrl = createApiCall("addondetail", "id=" + addonId);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(requestUrl.openStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException ex) {
            throw new AddonCatalogServiceException("Invalid response for file request for addon: " + addonId, ex);
        }
    }

    @Override
    public URL getFileDownloadUrl(String remoteFilePath) throws AddonCatalogServiceException {
        String fileDownloadPrefix = addonServiceUrl + "download/?f=";
        try {
            return new URI(fileDownloadPrefix + remoteFilePath).toURL();
        } catch (MalformedURLException | URISyntaxException ex) {
            throw new AddonCatalogServiceException("Invalid response for download file URL request: " + remoteFilePath, ex);
        }
    }

    @Nonnull
    @Override
    public URL getLicenseDownloadUrl(String remoteFilePath) throws AddonCatalogServiceException {
        String licenseDownloadPrefix = addonServiceUrl + "license/";
        try {
            return new URI(licenseDownloadPrefix + remoteFilePath).toURL();
        } catch (MalformedURLException | URISyntaxException ex) {
            throw new AddonCatalogServiceException("Invalid response for download license URL request: " + remoteFilePath, ex);
        }
    }

    public void addIconChangeListener(IconChangeListener listener) {
        iconChangeListeners.add(listener);
    }

    public void removeIconChangeListener(IconChangeListener listener) {
        iconChangeListeners.remove(listener);
    }

    @Nonnull
    public URL createApiCall(String operation) throws AddonCatalogServiceException {
        return createApiCall(operation, null);
    }

    @Nonnull
    public URL createApiCall(String operation, @Nullable String parameters) throws AddonCatalogServiceException {
        try {
            return new URI(addonServiceUrl + "api/?op=" + operation + (parameters == null ? "" : "&" + parameters)).toURL();
        } catch (MalformedURLException | URISyntaxException ex) {
            throw new AddonCatalogServiceException("Invalid URL for operation " + operation, ex);
        }
    }

    public interface IconChangeListener {

        void iconsChanged();
    }
}
