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
package org.exbin.framework.gui.update.options;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.gui.options.api.OptionsData;
import org.exbin.framework.gui.update.preferences.CheckForUpdatePreferences;

/**
 * Check for update options.
 *
 * @version 0.2.1 2019/07/20
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CheckForUpdateOptions implements OptionsData {

    private boolean checkForUpdate;

    public boolean isCheckForUpdate() {
        return checkForUpdate;
    }

    public void setCheckForUpdate(boolean checkForUpdate) {
        this.checkForUpdate = checkForUpdate;
    }

    public void loadFromPreferences(CheckForUpdatePreferences preferences) {
        checkForUpdate = preferences.isShouldCheckForUpdate();
    }

    public void saveToPreferences(CheckForUpdatePreferences preferences) {
        preferences.setShouldCheckForUpdate(checkForUpdate);
    }
}
