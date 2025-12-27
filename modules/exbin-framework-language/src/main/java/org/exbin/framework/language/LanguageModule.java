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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.language.api.ApplicationInfoKeys;
import org.exbin.framework.language.api.IconSetProvider;
import org.exbin.framework.language.api.LanguageModifier;
import org.exbin.framework.language.api.LanguageModuleApi;
import org.exbin.framework.language.api.LanguageProvider;

/**
 * Language module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class LanguageModule implements LanguageModuleApi {

    private ResourceBundle appBundle;
    private ClassLoader languageClassLoader = null;
    private IconSetProvider iconSetProvider = null;
    private Locale languageLocale = null;
    private LanguageModifier languageModifier = null;
    private final List<LanguageProvider> languagePlugins = new ArrayList<>();
    private final List<IconSetProvider> iconSets = new ArrayList<>();

    public LanguageModule() {
    }

    @Nonnull
    @Override
    public ResourceBundle getAppBundle() {
        if (appBundle == null) {
            throw new IllegalStateException("Language was not initialized");
        }
        return appBundle;
    }

    @Override
    public void setAppBundle(ResourceBundle appBundle) {
        this.appBundle = appBundle;
    }

    @Nonnull
    @Override
    public ResourceBundle getBundle(Class<?> targetClass) {
        if (languageClassLoader == null && iconSetProvider == null) {
            return ResourceBundle.getBundle(getResourceBaseNameBundleByClass(targetClass), getLanguageBundleLocale(), targetClass.getClassLoader());
        } else {
            String bundleName = getResourceBaseNameBundleByClass(targetClass);
            LanguageResourceBundle bundle = new LanguageResourceBundle(bundleName, getResourceBundleForLanguage(bundleName), targetClass.getClassLoader());
            if (iconSetProvider != null) {
                bundle.setIconSet(iconSetProvider);
            }
            return bundle;
        }
    }

    @Nonnull
    @Override
    public ResourceBundle getResourceBundleByBundleName(String bundleName) {
        if (languageClassLoader == null) {
            return ResourceBundle.getBundle(bundleName, getLanguageBundleLocale());
        } else {
            return new LanguageResourceBundle(bundleName, getResourceBundleForLanguage(bundleName), languageClassLoader);
        }
    }

    @Nonnull
    private ResourceBundle getResourceBundleForLanguage(String bundleName) {
        return languageClassLoader == null ? ResourceBundle.getBundle(bundleName, getLanguageBundleLocale()) : ResourceBundle.getBundle(bundleName, getLanguageBundleLocale(), languageClassLoader);
    }

    @Nonnull
    public Optional<ClassLoader> getLanguageClassLoader() {
        return Optional.ofNullable(languageClassLoader);
    }

    public void setLanguageClassLoader(@Nullable ClassLoader languageClassLoader) {
        this.languageClassLoader = languageClassLoader;
    }

    @Nonnull
    public Optional<Locale> getLanguageLocale() {
        return Optional.ofNullable(languageLocale);
    }

    public void setLanguageLocale(@Nullable Locale languageLocale) {
        this.languageLocale = languageLocale;
    }

    @Override
    public void switchToLanguage(@Nullable Locale targetLocale) {
        if (targetLocale == null) {
            setLanguageLocale(null);
            languageClassLoader = null;
            updateModifier();
            return;
        } else if ("en-US".equals(targetLocale.toLanguageTag())) {
            setLanguageLocale(Locale.ROOT);
            languageClassLoader = null;
            updateModifier();
            return;
        }

        if (targetLocale.equals(Locale.ROOT)) {
            // Detect if there is any matching locale for current application locale
            Locale defaultLocale = Locale.getDefault();
            ResourceBundle.Control control = new ResourceBundle.Control() {
            };
            List<Locale> candidateLocales = control.getCandidateLocales("", defaultLocale);
            List<Locale.LanguageRange> priorityList = new ArrayList<>();
            for (Locale locale : candidateLocales) {
                priorityList.add(new Locale.LanguageRange(locale.toLanguageTag()));
            }

            List<Locale> availableLocales = new ArrayList<>();
            for (LanguageProvider languagePlugin : languagePlugins) {
                availableLocales.add(languagePlugin.getLocale());
            }

            List<Locale> matchingLocales = Locale.filter(priorityList, availableLocales);
            if (!matchingLocales.isEmpty()) {
                Locale bestMatch = matchingLocales.get(0);
                for (LanguageProvider languagePlugin : languagePlugins) {
                    if (languagePlugin.getLocale().equals(bestMatch)) {
                        ClassLoader classLoader = languagePlugin.getClassLoader().orElse(null);
                        if (classLoader != null) {
                            setLanguageClassLoader(classLoader);
                            try {
                                // TODO use better method than string to class conversion
                                appBundle = getBundle(Class.forName(appBundle.getBaseBundleName().replaceFirst("/resources/", "/").replaceAll("/", ".")));
                            } catch (ClassNotFoundException ex) {
                                // Unable to load
                                Logger.getLogger(LanguageModule.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        break;
                    }
                }
            }
            return;
        }

        for (LanguageProvider languageProvider : languagePlugins) {
            if (languageProvider.getLocale().equals(targetLocale)) {
                languageClassLoader = languageProvider.getClassLoader().orElse(getClass().getClassLoader());
                setLanguageLocale(targetLocale);
                updateModifier();
                break;
            }
        }
    }

    private void updateModifier() {
        boolean modifierPresent = appBundle.containsKey(ApplicationInfoKeys.APPLICATION_LANGUAGE_MODIFIER);
        if (modifierPresent) {
            try {
                String modifierClassName = appBundle.getString(ApplicationInfoKeys.APPLICATION_LANGUAGE_MODIFIER);
                Class<?> modifierClass = Class.forName(modifierClassName, true, languageClassLoader != null ? languageClassLoader : this.getClass().getClassLoader());
                if (modifierClass != null) {
                    languageModifier = (LanguageModifier) modifierClass.cast(LanguageModifier.class);
                }
            } catch (ClassNotFoundException ex) {
                languageModifier = null;
            }
        } else {
            languageModifier = null;
        }
    }

    /**
     * Returns class name path.
     * <br>
     * Result is canonical name with dots replaced with slashes.
     *
     * @param targetClass target class
     * @return name path
     */
    @Nonnull
    public static String getClassNamePath(Class<?> targetClass) {
        return targetClass.getCanonicalName().replace(".", "/");
    }

    /**
     * Returns resource bundle base name for properties file with path derived
     * from class name.
     *
     * @param targetClass target class
     * @return base name string
     */
    @Nonnull
    public static String getResourceBaseNameBundleByClass(Class<?> targetClass) {
        String classNamePath = getClassNamePath(targetClass);
        int classNamePos = classNamePath.lastIndexOf("/");
        return classNamePath.substring(0, classNamePos + 1) + "resources" + classNamePath.substring(classNamePos);
    }

    @Nonnull
    @Override
    public String getActionWithDialogText(String actionTitle) {
        if (languageModifier != null) {
            return languageModifier.getActionWithDialogText(actionTitle);
        }

        return actionTitle + "...";
    }

    @Nonnull
    @Override
    public String getActionWithDialogText(ResourceBundle bundle, String key) {
        if (languageModifier != null) {
            return languageModifier.getActionWithDialogText(bundle, key);
        }

        return bundle.getString(key) + "...";
    }

    @Override
    public void registerLanguagePlugin(LanguageProvider languageProvider) {
        languagePlugins.add(languageProvider);
    }

    @Nonnull
    @Override
    public List<LanguageProvider> getLanguagePlugins() {
        return languagePlugins;
    }

    @Nonnull
    @Override
    public List<IconSetProvider> getIconSets() {
        return iconSets;
    }

    @Override
    public void registerIconSetProvider(IconSetProvider iconSetProvider) {
        iconSets.add(iconSetProvider);
    }

    @Override
    public void switchToIconSet(String iconSetId) {
        for (IconSetProvider iconSet : iconSets) {
            if (iconSetId.equals(iconSet.getId())) {
                iconSetProvider = iconSet;
                return;
            }
        }

        iconSetProvider = null;
    }

    @Nonnull
    public Locale getLanguageBundleLocale() {
        return languageLocale == null ? Locale.getDefault() : languageLocale;
    }
}
