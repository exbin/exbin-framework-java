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
package org.exbin.framework.help.api;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JButton;
import org.exbin.framework.Module;
import org.exbin.framework.ModuleUtils;

/**
 * Interface for help module.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface HelpModuleApi extends Module {

    public static String MODULE_ID = ModuleUtils.getModuleIdByApi(HelpModuleApi.class);

    /**
     * Opens help page on given position.
     *
     * @param helpLink help link
     */
    void openHelp(HelpLink helpLink);

    /**
     * Create button with help symbol.
     *
     * @return help button
     */
    @Nonnull
    JButton createHelpButton();

    /**
     * Returns true if opening handler is available.
     *
     * @return true if available
     */
    boolean hasOpeningHandler();

    /**
     * Returns help opening handler.
     *
     * @return help opening handler
     */
    @Nonnull
    Optional<HelpOpeningHandler> getHelpOpeningHandler();

    /**
     * Sets help opening handler.
     *
     * @param helpOpeningHandler help opening handler
     */
    void setHelpOpeningHandler(@Nullable HelpOpeningHandler helpOpeningHandler);
}
