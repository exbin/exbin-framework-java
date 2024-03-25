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
package org.exbin.framework.client;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import org.exbin.framework.App;
import org.exbin.framework.preferences.PreferencesWrapper;
import org.exbin.framework.preferences.api.Preferences;
import org.exbin.framework.client.api.ClientConnectionEvent;
import org.exbin.framework.client.api.ClientConnectionListener;
import org.exbin.framework.client.api.ClientModuleApi;
import org.exbin.framework.client.api.ConnectionStatus;
import org.exbin.framework.client.api.PluginRepositoryListener;
import org.exbin.framework.preferences.api.PreferencesModuleApi;
import org.exbin.xbup.catalog.XBAECatalog;
import org.exbin.xbup.catalog.entity.XBERoot;
import org.exbin.xbup.catalog.entity.service.XBEXDescService;
import org.exbin.xbup.catalog.entity.service.XBEXFileService;
import org.exbin.xbup.catalog.entity.service.XBEXHDocService;
import org.exbin.xbup.catalog.entity.service.XBEXIconService;
import org.exbin.xbup.catalog.entity.service.XBEXLangService;
import org.exbin.xbup.catalog.entity.service.XBEXUiService;
import org.exbin.xbup.catalog.entity.service.XBEXNameService;
import org.exbin.xbup.catalog.entity.service.XBEXPlugService;
import org.exbin.xbup.catalog.entity.service.XBEXStriService;
import org.exbin.xbup.catalog.update.XBCatalogServiceUpdateHandler;
import org.exbin.xbup.client.XBCallException;
import org.exbin.xbup.client.update.XBCUpdateListener;
import org.exbin.xbup.client.XBCatalogNetServiceClient;
import org.exbin.xbup.client.XBTCPServiceClient;
import org.exbin.xbup.client.catalog.XBARCatalog;
import org.exbin.xbup.client.catalog.remote.service.XBRXDescService;
import org.exbin.xbup.client.catalog.remote.service.XBRXFileService;
import org.exbin.xbup.client.catalog.remote.service.XBRXHDocService;
import org.exbin.xbup.client.catalog.remote.service.XBRXIconService;
import org.exbin.xbup.client.catalog.remote.service.XBRXLangService;
import org.exbin.xbup.client.catalog.remote.service.XBRXUiService;
import org.exbin.xbup.client.catalog.remote.service.XBRXNameService;
import org.exbin.xbup.client.catalog.remote.service.XBRXPlugService;
import org.exbin.xbup.client.catalog.remote.service.XBRXStriService;
import org.exbin.xbup.client.update.XBCUpdateHandler;
import org.exbin.xbup.core.catalog.XBACatalog;
import org.exbin.xbup.core.catalog.base.XBCRoot;
import org.exbin.xbup.core.catalog.base.service.XBCRootService;
import org.exbin.xbup.core.catalog.base.service.XBCXDescService;
import org.exbin.xbup.core.catalog.base.service.XBCXFileService;
import org.exbin.xbup.core.catalog.base.service.XBCXHDocService;
import org.exbin.xbup.core.catalog.base.service.XBCXIconService;
import org.exbin.xbup.core.catalog.base.service.XBCXLangService;
import org.exbin.xbup.core.catalog.base.service.XBCXUiService;
import org.exbin.xbup.core.catalog.base.service.XBCXNameService;
import org.exbin.xbup.core.catalog.base.service.XBCXPlugService;
import org.exbin.xbup.core.catalog.base.service.XBCXStriService;
import org.exbin.xbup.plugin.XBPluginRepository;

/**
 * Implementation of XBUP framework client module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ClientModule implements ClientModuleApi {

    private boolean devMode = false;
    private XBACatalog catalog;
    private XBPluginRepository pluginRepository;
    private final List<ClientConnectionListener> connectionListeners = new ArrayList<>();
    private final List<PluginRepositoryListener> pluginRepositoryListeners = new ArrayList<>();

    public ClientModule() {
    }

    public void unregisterModule(String moduleId) {
    }

    @Override
    public boolean connectToService() {
        connectionStatusChanged(ConnectionStatus.DISCONNECTED);
        setCatalog(null);

        int defaultPort = devMode ? XBTCPServiceClient.DEFAULT_DEV_PORT : XBTCPServiceClient.DEFAULT_PORT;

        String catalogConnection = "localhost:" + defaultPort;
        String catalogHost;
        int catalogPort = defaultPort;
        int pos = catalogConnection.indexOf(":");
        if (pos >= 0) {
            catalogHost = catalogConnection.substring(0, pos);
            catalogPort = Integer.valueOf(catalogConnection.substring(pos + 1));
        } else {
            catalogHost = catalogConnection;
        }

        XBCatalogNetServiceClient serviceClient = new XBCatalogNetServiceClient(catalogHost, catalogPort);
        connectionStatusChanged(ConnectionStatus.CONNECTING);
        if (serviceClient.validate()) {
            XBARCatalog catalogHandler = new XBARCatalog(serviceClient);
            catalog = catalogHandler;
            initializePlugins(catalog);

            catalogHandler.addCatalogService(XBCXLangService.class, new XBRXLangService(catalogHandler));
            catalogHandler.addCatalogService(XBCXStriService.class, new XBRXStriService(catalogHandler));
            catalogHandler.addCatalogService(XBCXNameService.class, new XBRXNameService(catalogHandler));
            catalogHandler.addCatalogService(XBCXDescService.class, new XBRXDescService(catalogHandler));
            catalogHandler.addCatalogService(XBCXFileService.class, new XBRXFileService(catalogHandler));
            catalogHandler.addCatalogService(XBCXIconService.class, new XBRXIconService(catalogHandler));
            catalogHandler.addCatalogService(XBCXPlugService.class, new XBRXPlugService(catalogHandler));
            catalogHandler.addCatalogService(XBCXUiService.class, new XBRXUiService(catalogHandler));
            catalogHandler.addCatalogService(XBCXHDocService.class, new XBRXHDocService(catalogHandler));

            if ("localhost".equals(serviceClient.getHost()) || "127.0.0.1".equals(serviceClient.getHost())) {
                connectionStatusChanged(ConnectionStatus.LOCAL);
            } else {
                connectionStatusChanged(ConnectionStatus.NETWORK);
            }

            return true;
        } else {
            connectionStatusChanged(ConnectionStatus.FAILED);
            return false;
        }
    }

    private void connectionStatusChanged(ConnectionStatus connectionStatus) {
        ClientConnectionEvent connectionEvent = new ClientConnectionEvent(connectionStatus);
        for (int i = 0; i < connectionListeners.size(); i++) {
            ClientConnectionListener listener = connectionListeners.get(i);
            listener.connectionChanged(connectionEvent);
        }
    }

    @Override
    public boolean runLocalCatalog() {
        boolean started = startLocalCatalog();
        if (!started) {
            return false;
        }

        return updateLocalCatalog();
    }
    
    @Override
    public boolean startLocalCatalog() {
        connectionStatusChanged(ConnectionStatus.CONNECTING);
        try {
            // Database Initialization
            PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
            Preferences preferences = preferencesModule.getAppPreferences();
            String derbyHome = System.getProperty("user.home") + "/.java/.userPrefs/" + ((PreferencesWrapper) preferences).getInnerPreferences().absolutePath();
            if (devMode) {
                derbyHome += "-dev";
            }
            System.setProperty("derby.system.home", derbyHome);
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("XBEditorPU");
            EntityManager em = emf.createEntityManager();
            em.setFlushMode(FlushModeType.AUTO);

            catalog = createInternalCatalog(em);
            initializePlugins(catalog);
            return true;
        } catch (Exception ex) {
            Logger.getLogger(ClientModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    @Override
    public boolean updateLocalCatalog() {
        XBAECatalog intCatalog = (XBAECatalog) catalog;
        try {
            if (intCatalog.isShallInit()) {
                connectionStatusChanged(ConnectionStatus.INITIALIZING);
                intCatalog.initCatalog();
            }

            try {
                int defaultPort = devMode ? XBTCPServiceClient.DEFAULT_DEV_PORT : XBTCPServiceClient.DEFAULT_PORT;
                String mainCatalogHost = devMode ? XBTCPServiceClient.MAIN_DEV_CATALOG_HOST : XBTCPServiceClient.MAIN_CATALOG_HOST;
                XBCatalogNetServiceClient mainClient = new XBCatalogNetServiceClient(mainCatalogHost, defaultPort);
                XBCatalogServiceUpdateHandler updateHandler = new XBCatalogServiceUpdateHandler(intCatalog, mainClient);
                intCatalog.setUpdateHandler(updateHandler);
                XBCRootService rootService = intCatalog.getCatalogService(XBCRootService.class);
                try {
                    Optional<Date> localLastUpdate = rootService.getMainLastUpdate();
                    Date remoteLastUpdate = (Date) updateHandler.getMainLastUpdate();
                    if (!rootService.isMainPresent() || !localLastUpdate.isPresent() || localLastUpdate.get().before(remoteLastUpdate)) {
                        System.out.print("Update due to: ");
                        if (!rootService.isMainPresent()) {
                            System.out.println("Missing main root");
                        } else if (!localLastUpdate.isPresent()) {
                            System.out.println("No last update date");
                        } else {
                            System.out.println("Never update available " + remoteLastUpdate.toString() + " > " + localLastUpdate.get().toString());
                        }

                        connectionStatusChanged(ConnectionStatus.UPDATING);
                        // TODO: As there is currently no diff update available - wipe out entire database instead
                        intCatalog.getEntityManager().close();
                        EntityManagerFactory emfDrop = Persistence.createEntityManagerFactory("XBEditorPU-drop");
                        EntityManager emDrop = emfDrop.createEntityManager();
                        emDrop.setFlushMode(FlushModeType.AUTO);
                        intCatalog = createInternalCatalog(emDrop);
                        intCatalog.setUpdateHandler(new XBCatalogServiceUpdateHandler(intCatalog, mainClient));
                        intCatalog.initCatalog();
                        rootService = intCatalog.getCatalogService(XBCRootService.class);
                        performUpdate(intCatalog, rootService, remoteLastUpdate);
                    }

                    connectionStatusChanged(ConnectionStatus.INTERNET);
                } catch (XBCallException ex) {
                    if (!(ex.getCause() instanceof ConnectException)) {
                        Logger.getLogger(ClientModule.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    connectionStatusChanged(ConnectionStatus.FAILED);
                } catch (Exception ex) {
                    Logger.getLogger(ClientModule.class.getName()).log(Level.SEVERE, null, ex);
                    connectionStatusChanged(ConnectionStatus.FAILED);
                }
            } catch (Exception ex) {
                Logger.getLogger(ClientModule.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            return true;
        } catch (Exception ex) {
            Logger.getLogger(ClientModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    private void performUpdate(XBAECatalog catalog, XBCRootService rootService, Date remoteLastUpdate) {
        XBCUpdateHandler updateHandler = catalog.getUpdateHandler();
        XBCUpdateListener updateListener = new XBCUpdateListener() {
            private boolean toolBarVisibleTemp;

            @Override
            public void webServiceUsage(boolean status) {
//                if (status == true) {
//                    toolBarVisbleTemp = getStatusBar().isVisible();
//                    ((CardLayout) statusPanel.getLayout()).show(statusPanel, "updateCat");
//                    LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
//                    activityProgressBar.setString(languageModule.getActionWithDialogText(resourceBundle.getString("main_updatecat")));
//                    getStatusBar().setVisible(true);
//                } else {
//                    ((CardLayout) statusPanel.getLayout()).first(statusPanel);
//                    //                                statusBar.setVisible(toolBarVisibleTemp);
//                }
            }
        };

        updateHandler.addUpdateListener(updateListener);
        updateHandler.performUpdateMain();

        EntityManager em = catalog.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        XBCRoot mainRoot = rootService.getMainRoot();
        ((XBERoot) mainRoot).setLastUpdate(remoteLastUpdate);
        rootService.persistItem(mainRoot);
        tx.commit();

        updateHandler.removeUpdateListener(updateListener);
    }

    @Override
    public void useBuildInCatalog() {
        // Database Initialization
        PreferencesModuleApi preferencesModule = App.getModule(PreferencesModuleApi.class);
        Preferences preferences = preferencesModule.getAppPreferences();
        String derbyHome = System.getProperty("user.home") + "/.java/.userPrefs/" + ((PreferencesWrapper) preferences).getInnerPreferences().absolutePath();
        if (devMode) {
            derbyHome += "-dev";
        }
        System.setProperty("derby.system.home", derbyHome);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("XBEditorPU");
        EntityManager em = emf.createEntityManager();
        em.setFlushMode(FlushModeType.AUTO);

        XBAECatalog catalogHandler = createInternalCatalog(em);

        if (catalogHandler.isShallInit()) {
//            LanguageModuleApi languageModule = App.getModule(LanguageModuleApi.class);
//            operationProgressBar.setString(languageModule.getActionWithDialogText(resourceBundle.getString("main_defaultcat")));
            catalogHandler.initCatalog();
        }

        this.catalog = catalogHandler;
        initializePlugins(catalog);
    }

    private void initializePlugins(XBACatalog catalogHandler) {
        pluginRepository = new XBPluginRepository();
//        pluginRepository.addPluginsFrom(new File("plugins/").toURI());
//        pluginRepository.processPlugins();
        pluginRepository.setCatalog(catalogHandler);

        pluginRepositoryListeners.forEach(listener -> {
            listener.repositoryChanged(pluginRepository);
        });
    }

    @Nonnull
    private XBAECatalog createInternalCatalog(EntityManager em) {
        XBAECatalog createdCatalog = new XBAECatalog(em);

        createdCatalog.addCatalogService(XBCXLangService.class, new XBEXLangService(createdCatalog));
        createdCatalog.addCatalogService(XBCXStriService.class, new XBEXStriService(createdCatalog));
        createdCatalog.addCatalogService(XBCXNameService.class, new XBEXNameService(createdCatalog));
        createdCatalog.addCatalogService(XBCXDescService.class, new XBEXDescService(createdCatalog));
        createdCatalog.addCatalogService(XBCXFileService.class, new XBEXFileService(createdCatalog));
        createdCatalog.addCatalogService(XBCXIconService.class, new XBEXIconService(createdCatalog));
        createdCatalog.addCatalogService(XBCXPlugService.class, new XBEXPlugService(createdCatalog));
        createdCatalog.addCatalogService(XBCXUiService.class, new XBEXUiService(createdCatalog));
        createdCatalog.addCatalogService(XBCXHDocService.class, new XBEXHDocService(createdCatalog));
        return createdCatalog;
    }

    public void setCatalog(XBACatalog catalog) {
        this.catalog = catalog;

//        activePanel.setCatalog(catalog);
//        activePanel.reportStructureChange((XBTBlock) activePanel.getDoc().getRootBlock());
//
//        if (propertiesDialog != null) {
//            propertiesDialog.setCatalog(catalog);
//        }
//
//        if (catalogEditorDialog != null) {
//            catalogEditorDialog.setCatalog(catalog);
//        }
    }

    @Nonnull
    @Override
    public XBACatalog getCatalog() {
        return catalog;
    }

    @Nonnull
    @Override
    public XBPluginRepository getPluginRepository() {
        return pluginRepository;
    }

    @Override
    public boolean isDevMode() {
        return devMode;
    }

    @Override
    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    @Override
    public void addClientConnectionListener(ClientConnectionListener listener) {
        connectionListeners.add(listener);
    }

    @Override
    public void removeClientConnectionListener(ClientConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    @Override
    public void addPluginRepositoryListener(PluginRepositoryListener listener) {
        pluginRepositoryListeners.add(listener);
    }

    @Override
    public void removePluginRepositoryListener(PluginRepositoryListener listener) {
        pluginRepositoryListeners.remove(listener);
    }
}
