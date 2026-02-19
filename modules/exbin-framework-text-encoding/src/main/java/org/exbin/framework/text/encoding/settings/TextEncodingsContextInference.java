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
package org.exbin.framework.text.encoding.settings;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.context.api.ActiveContextProvider;
import org.exbin.framework.text.encoding.CharsetListEncodingState;
import org.exbin.framework.text.encoding.ContextEncoding;

/**
 * Text editor encodings context inference.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextEncodingsContextInference implements TextEncodingsInference {

    protected ActiveContextProvider contextProvider;

    public TextEncodingsContextInference(ActiveContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Nonnull
    @Override
    public List<String> getEncodings() {
        ContextEncoding contextEncoding = contextProvider.getActiveState(ContextEncoding.class);
        CharsetListEncodingState state = (CharsetListEncodingState) contextEncoding;
        return state.getEncodings();
    }

/*    @Override
    public void setEncodings(List<String> encodings) {
        ContextEncoding contextEncoding = contextProvider.getActiveState(ContextEncoding.class);
        if (contextEncoding instanceof CharsetListEncodingState) {
            TextEncodingListSettingsApplier applier = new TextEncodingListSettingsApplier();
            // applier.applySettings(contextEncoding, settingsOptionsProvider);
            contextProvider.notifyStateChange(ContextEncoding.class, CharsetListEncodingState.ChangeType.ENCODING_LIST);
        }
    } */
}
