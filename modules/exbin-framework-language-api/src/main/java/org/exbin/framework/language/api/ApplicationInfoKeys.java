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

/**
 * Application information language keys.
 *
 * @author ExBin Project (https://exbin.org)
 */
public class ApplicationInfoKeys {

    public static final String APPLICATION_PREFIX = "Application.";
    public static final String APPLICATION_NAME = APPLICATION_PREFIX + "name";
    public static final String APPLICATION_TITLE = APPLICATION_PREFIX + "title";
    public static final String APPLICATION_PRODUCT = APPLICATION_PREFIX + "product";
    public static final String APPLICATION_DESCRIPTION = APPLICATION_PREFIX + "description";
    public static final String APPLICATION_VENDOR = APPLICATION_PREFIX + "vendor";
    public static final String APPLICATION_AUTHORS = APPLICATION_PREFIX + "authors";
    public static final String APPLICATION_LICENSE = APPLICATION_PREFIX + "license";
    public static final String APPLICATION_LICENSE_FILE = APPLICATION_PREFIX + "licenseFile";
    public static final String APPLICATION_ICON = APPLICATION_PREFIX + "icon";
    public static final String APPLICATION_ABOUT_IMAGE = APPLICATION_PREFIX + "aboutImage";
    public static final String APPLICATION_HOMEPAGE = APPLICATION_PREFIX + "homepage";
    public static final String APPLICATION_LANGUAGE_MODIFIER = APPLICATION_PREFIX + "languageModifier";

    private ApplicationInfoKeys() {
    }
}
