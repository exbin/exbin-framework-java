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
package org.exbin.framework.text.font.settings;

import java.awt.Font;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.context.api.ActiveContextProvider;
import org.exbin.framework.text.font.ContextFont;
import org.exbin.framework.text.font.TextFontState;

/**
 * Text editor font context inference.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class TextFontContextInference implements TextFontInference {

    protected ActiveContextProvider contextProvider;

    public TextFontContextInference(ActiveContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Nonnull
    @Override
    public Optional<Font> getCurrentFont() {
        ContextFont contextFont = contextProvider.getActiveState(ContextFont.class);
        if (contextFont instanceof TextFontState) {
            return Optional.of(((TextFontState) contextFont).getCurrentFont());
        }

        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<Font> getDefaultFont() {
        ContextFont contextFont = contextProvider.getActiveState(ContextFont.class);
        if (contextFont instanceof TextFontState) {
            return Optional.of(((TextFontState) contextFont).getDefaultFont());
        }

        return Optional.empty();
    }

    /* TODO
        if (contextProvider != null) {
            ContextFont contextFont = contextProvider.getActiveState(ContextFont.class);
            if (contextFont instanceof TextFontState) {
                TextFontSettingsApplier applier = new TextFontSettingsApplier();
                applier.applySettings(contextFont, settingsOptionsProvider);
                contextProvider.notifyStateChange(ContextFont.class, TextFontState.ChangeType.FONT);
            }
        } */
}
