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
package org.exbin.framework.update.options;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.OptionsData;
import org.exbin.framework.update.preferences.CheckForUpdatePreferences;

/**
 * Check for update options.
 *
 * @author ExBin Project (https://exbin.org)
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
