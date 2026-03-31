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
package org.exbin.framework.document.text.settings;

import java.awt.Color;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.context.api.ActiveContextProvider;

/**
 * Text color context inference options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextColorContextInference implements TextColorInference {

    protected ActiveContextProvider contextProvider;

    public TextColorContextInference(ActiveContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override
    public Color[] getCurrentTextColors() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Color[] getDefaultTextColors() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // TODO
    /* if (contextProvider != null) {
            ContextComponent contextComponent = contextProvider.getActiveState(ContextComponent.class);
            if (contextComponent instanceof TextColorState) {
                TextColorSettingsApplier applier = new TextColorSettingsApplier();
                applier.applySettings(contextComponent, settingsOptionsProvider);
                contextProvider.notifyStateChange(ContextComponent.class, TextColorState.ChangeType.TEXT_COLOR);
            }
        } */
}
