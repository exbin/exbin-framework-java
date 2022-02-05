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
package org.exbin.framework.operation;

import org.exbin.framework.api.XBApplication;
import org.exbin.framework.operation.api.OperationModuleApi;
import org.exbin.xbup.plugin.XBModuleHandler;

/**
 * Implementation of XBUP framework operation module.
 *
 * @version 0.2.2 2022/02/05
 * @author ExBin Project (http://exbin.org)
 */
public class OperationModule implements OperationModuleApi {

    private XBApplication application;

    public OperationModule() {
    }

    @Override
    public void init(XBModuleHandler moduleHandler) {
        this.application = (XBApplication) moduleHandler;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }
}
