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
package org.exbin.framework.language.api;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;

/**
 * Language modifier.
 *
 * @author ExBin Project (https://exbin.org)
 */
public interface LanguageModifier {

    /**
     * Enhances action title to indicate action which is opening dialog.
     *
     * @param actionTitle action title
     * @return enhanced action title
     */
    @Nonnull
    String getActionWithDialogText(String actionTitle);

    /**
     * Enhances action title to indicate action which is opening dialog.
     *
     * @param bundle resource bundle
     * @param key resource key
     * @return enhanced action title
     */
    @Nonnull
    String getActionWithDialogText(ResourceBundle bundle, String key);
}
