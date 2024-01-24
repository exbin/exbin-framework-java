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
package org.exbin.framework.editor.wave.preferences;

import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.preferences.api.Preferences;
import org.exbin.framework.editor.wave.options.AudioDevicesOptions;

/**
 * Audio devices preferences.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AudioDevicesPreferences implements AudioDevicesOptions {

    public static final String PREFERENCES_AUDIO_DEVICES = "";

    private final Preferences preferences;

    public AudioDevicesPreferences(Preferences preferences) {
        this.preferences = preferences;
    }
}
