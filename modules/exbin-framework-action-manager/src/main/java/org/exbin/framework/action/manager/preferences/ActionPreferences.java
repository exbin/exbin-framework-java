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
package org.exbin.framework.action.manager.preferences;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.action.manager.options.ActionOptions;
import org.exbin.framework.preferences.api.Preferences;

/**
 * Appearance options preferences.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ActionPreferences implements ActionOptions {

    public static final String PREFERENCES_ACTION_KEYS = "action.keys";
    public static final String PREFERENCES_ACTION_KEY_PREFIX = "action.key.";
    public static final String PREFERENCES_ACTION_KEY_ID = "id";
    public static final String PREFERENCES_ACTION_KEY_SHORTCUT = "shortcut";

    private final Preferences preferences;

    public ActionPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

}
