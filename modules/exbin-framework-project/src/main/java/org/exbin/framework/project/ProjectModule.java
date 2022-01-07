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
package org.exbin.framework.project;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.api.XBApplication;
import org.exbin.framework.project.api.ProjectModuleApi;
import org.exbin.xbup.plugin.XBModuleHandler;

/**
 * Implementation of framework project module.
 *
 * @version 0.2.2 2022/01/07
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ProjectModule implements ProjectModuleApi {

    private XBApplication application;

    public ProjectModule() {
    }

    @Override
    public void init(XBModuleHandler application) {
        this.application = (XBApplication) application;
    }

    @Override
    public void unregisterModule(String moduleId) {
    }

}
