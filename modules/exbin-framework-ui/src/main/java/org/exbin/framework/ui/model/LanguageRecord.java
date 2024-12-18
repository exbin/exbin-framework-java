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
package org.exbin.framework.ui.model;

import java.util.Locale;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.ImageIcon;

/**
 * Language record.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class LanguageRecord {

    private final Locale locale;
    @Nullable
    private final ImageIcon flag;
    @Nullable
    private final String note;

    public LanguageRecord(Locale locale, @Nullable ImageIcon flag) {
        this(locale, flag, null);
    }

    public LanguageRecord(Locale locale, @Nullable ImageIcon flag, @Nullable String note) {
        this.locale = locale;
        this.flag = flag;
        this.note = note;
    }

    @Nonnull
    public Locale getLocale() {
        return locale;
    }

    @Nonnull
    public Optional<ImageIcon> getFlag() {
        return Optional.ofNullable(flag);
    }

    @Nonnull
    public Optional<String> getNote() {
        return Optional.ofNullable(note);
    }

    /**
     * Returns text representing current language.
     * <p>
     * Returns empty string for ROOT locale.
     *
     * @return representation text
     */
    @Nonnull
    public String getText() {
        if (locale.equals(Locale.ROOT)) {
            return "";
        }

        return locale.getDisplayName(locale);
    }
}
