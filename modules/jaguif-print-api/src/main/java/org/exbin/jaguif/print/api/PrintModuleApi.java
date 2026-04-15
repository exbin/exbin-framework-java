/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.print.api;

import java.util.ResourceBundle;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.Module;
import org.exbin.jaguif.ModuleUtils;

/**
 * Interface for framework search module.
 */
@ParametersAreNonnullByDefault
public interface PrintModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(PrintModuleApi.class);
    public static final String SEARCH_MENU_GROUP_ID = MODULE_ID + ".searchMenuGroup";

    @Nonnull
    ResourceBundle getResourceBundle();
}
