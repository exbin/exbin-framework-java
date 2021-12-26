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
package org.exbin.framework.api;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Framework application event class.
 *
 * @version 0.2.0 2015/11/16
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class XBApplicationEvent {

    private XBApplication application;
    private XBApplicationEventType eventType;
    private Object parameter;

    @Nonnull
    public XBApplication getApplication() {
        return application;
    }

    public void setApplication(XBApplication application) {
        this.application = application;
    }

    @Nonnull
    public XBApplicationEventType getEventType() {
        return eventType;
    }

    public void setEventType(XBApplicationEventType eventType) {
        this.eventType = eventType;
    }

    public Object getParameter() {
        return parameter;
    }

    public void setParameter(Object parameter) {
        this.parameter = parameter;
    }

    /**
     * Enumeration of event types.
     */
    public enum XBApplicationEventType {
        INIT,
        STARTUP
    }
}
