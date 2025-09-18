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
package org.exbin.framework.window.api.controller;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.utils.OkCancelControlComponent;

/**
 * Controller for control panel with support for remove action.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface RemovalControlController {

    /**
     * Invokes control action.
     *
     * @param actionType action type
     */
    void controlActionPerformed(ControlActionType actionType);

    /**
     * Interface for control component.
     */
    @ParametersAreNonnullByDefault
    public interface RemovalControlComponent extends OkCancelControlComponent {

        /**
         * Invokes action button click.
         *
         * @param actionType action type
         */
        void performClick(ControlActionType actionType);

        /**
         * Sets action button enablement.
         *
         * @param actionType action type
         * @param enablement button enablement
         */
        void setActionEnabled(ControlActionType actionType, boolean enablement);
    }

    public static enum ControlActionType {
        OK, CANCEL, REMOVE
    }
}
