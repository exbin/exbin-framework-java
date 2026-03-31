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
package org.exbin.framework.language;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.language.api.IconSetProvider;

/**
 * Language resource bundle.
 * <p>
 * Resource bundle which looks for language resources first and main resources
 * as fallback.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class LanguageResourceBundle extends ResourceBundle {

    protected final ResourceBundle mainResourceBundle;
    protected final ResourceBundle languageResourceBundle;
    protected final ClassLoader classLoader;
    protected IconSetProvider iconSetProvider = null;
    protected final String prefix;

    public LanguageResourceBundle(String baseName, ResourceBundle languageResourceBundle, ClassLoader classLoader) {
        this.prefix = baseName.replace("/", ".") + ".";
        mainResourceBundle = ResourceBundle.getBundle(baseName, Locale.ROOT, classLoader);
        this.languageResourceBundle = languageResourceBundle;
        this.classLoader = classLoader;
    }

    public void setIconSet(@Nullable IconSetProvider iconSetProvider) {
        this.iconSetProvider = iconSetProvider;
    }

    @Nullable
    @Override
    protected Object handleGetObject(String key) {
        Object object = iconSetProvider != null ? iconSetProvider.getIconKey(prefix + key) : null;
        if (object == null) {
            object = languageResourceBundle.getObject(key);
        }
        if (object == null) {
            object = mainResourceBundle.getObject(key);
        }

        return object;
    }

    @Nonnull
    @Override
    public Enumeration<String> getKeys() {
        Set<String> keys = new HashSet<>();
        keys.addAll(Collections.list(languageResourceBundle.getKeys()));
        keys.addAll(Collections.list(mainResourceBundle.getKeys()));
        return Collections.enumeration(keys);
    }
}
