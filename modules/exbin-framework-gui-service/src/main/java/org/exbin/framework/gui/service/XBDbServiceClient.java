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
package org.exbin.framework.gui.service;

import java.io.IOException;
import java.net.Socket;
import javax.persistence.EntityManagerFactory;
import org.exbin.xbup.client.XBCatalogServiceClient;
import org.exbin.xbup.core.parser.basic.XBTListener;
import org.exbin.xbup.core.parser.basic.convert.XBTDefaultMatchingProvider;
import org.exbin.xbup.core.remote.XBCallHandler;

/**
 * Fake XBService client using localhost database.
 *
 * @version 0.2.0 2016/05/19
 * @author ExBin Project (http://exbin.org)
 */
public class XBDbServiceClient implements XBCatalogServiceClient {

    private final EntityManagerFactory entityManagerFactory;
    private XBTDefaultMatchingProvider source;
    private XBTListener target;

    /**
     * Performs login to the server.
     *
     * @param user user
     * @param password password
     * @return state
     * @throws java.io.IOException input/output exception
     */
    @Override
    public int login(String user, char[] password) throws IOException {
        return 0;
    }

    public XBDbServiceClient(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public String getVersion() {
        return "0.2.0";
    }

    public void stop() {
    }

    @Override
    public void close() {
    }

    @Override
    public boolean ping() {
        return true;
    }

    @Override
    public String getHost() {
        return "localhost";
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public String getLocalAddress() {
        return "localhost";
    }

    @Override
    public String getHostAddress() {
        return "localhost";
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public Socket getSocket() {
        return null;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    @Override
    public XBCallHandler procedureCall() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
