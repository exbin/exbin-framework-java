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
package org.exbin.framework.action.manager.settings;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.settings.api.SettingsData;
import org.exbin.framework.options.api.OptionsStorage;

/**
 * Action manager settings options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ActionManagerSettings implements SettingsData {

    public static final String KEY_ACTION_KEYS = "action.keys";
    public static final String KEY_ACTION_KEY_PREFIX = "action.key.";
    public static final String KEY_ACTION_KEY_ID = "id";
    public static final String KEY_ACTION_KEY_SHORTCUT = "shortcut";

    private final OptionsStorage storage;

    public ActionManagerSettings(OptionsStorage storage) {
        this.storage = storage;
    }

    @Override
    public void copyTo(SettingsData settingsData) {
    }
}
