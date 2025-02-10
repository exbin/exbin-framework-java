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

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Application information language keys.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ApplicationInfoKeys {

    public static final String APPLICATION_NAME = "Application.name";
    public static final String APPLICATION_DESCRIPTION = "Application.description";
    public static final String APPLICATION_TITLE = "Application.title";
    public static final String APPLICATION_VENDOR = "Application.vendor";
    public static final String APPLICATION_AUTHORS = "Application.authors";
    public static final String APPLICATION_RELEASE = "Application.release";
    public static final String APPLICATION_VERSION = "Application.version";
    public static final String APPLICATION_LICENSE = "Application.license";
    public static final String APPLICATION_LICENSE_FILE = "Application.licenseFile";

    public static final String APPLICATION_ICON = "Application.icon";
    public static final String APPLICATION_ABOUT_IMAGE = "Application.aboutImage";
    public static final String APPLICATION_HOMEPAGE = "Application.homepage";
    public static final String APPLICATION_LANGUAGE_MODIFIER = "Application.languageModifier";

    private ApplicationInfoKeys() {
    }
}
