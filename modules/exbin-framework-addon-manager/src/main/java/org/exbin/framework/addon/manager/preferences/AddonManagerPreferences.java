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
package org.exbin.framework.addon.manager.preferences;

import javax.annotation.Nonnull;
import org.exbin.framework.preferences.api.Preferences;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Addon manager preferences.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonManagerPreferences {

    public static final String PREFERENCES_ACTIVATED_VERSION = "addonManager.activatedVersion";

    private final Preferences preferences;

    public AddonManagerPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    @Nonnull
    public String getActivatedVersion() {
        return preferences.get(PREFERENCES_ACTIVATED_VERSION, "0.3.0-SNAPSHOT");
    }

    public void setActivatedVersion(String activatedVersion) {
        preferences.put(PREFERENCES_ACTIVATED_VERSION, activatedVersion);
    }
}
