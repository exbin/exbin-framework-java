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
package org.exbin.framework.api;

import org.exbin.xbup.plugin.LookAndFeelApplier;
import java.awt.Image;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.xbup.plugin.XBModuleHandler;

/**
 * Interface for application module management.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface XBApplication extends XBModuleHandler {

    /**
     * Gets application bundle.
     *
     * @return the appBundle
     */
    @Nonnull
    ResourceBundle getAppBundle();

    /**
     * Gets application preferences.
     *
     * @return the appPreferences
     */
    @Nonnull
    Preferences getAppPreferences();

    /**
     * Gets modules repository.
     *
     * @return the moduleRepository
     */
    @Nonnull
    @Override
    XBApplicationModuleRepository getModuleRepository();

    /**
     * Gets application icon.
     *
     * @return application icon image
     */
    @Nonnull
    Optional<Image> getApplicationIcon();

    /**
     * Registers locale and class loader which should be used to load resources
     * for it.
     *
     * @param locale language locale
     * @param classLoader class loader
     */
    void registerLanguagePlugin(Locale locale, ClassLoader classLoader);

    /**
     * Returns set of registered locales.
     *
     * @return set of locales
     */
    @Nonnull
    Set<Locale> getLanguageLocales();

    /**
     * Registers look & feel from plugin.
     *
     * @param className className
     * @param applier applier method
     */
    void registerLafPlugin(String className, LookAndFeelApplier applier);

    /**
     * Apply given look and feel.
     *
     * @param laf look and feel class name
     */
    void applyLookAndFeel(String laf);
}
