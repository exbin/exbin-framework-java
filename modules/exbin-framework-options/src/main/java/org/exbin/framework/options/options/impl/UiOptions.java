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
package org.exbin.framework.options.options.impl;

import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * UI options.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface UiOptions {

    @Nonnull
    Locale getLanguageLocale();

    void setLanguageLocale(Locale languageLocale);

    @Nonnull
    String getLookAndFeel();

    void setLookAndFeel(String lookAndFeel);

    @Nonnull
    String getRenderingMode();

    void setRenderingMode(String renderingMode);

    @Nonnull
    String getGuiScaling();

    void setGuiScaling(String guiScaling);

    float getGuiScalingRate();

    void setGuiScalingRate(float guiScalingRate);

    @Nonnull
    String getFontAntialiasing();

    void setFontAntialiasing(String fontAntialiasing);

    boolean isUseScreenMenuBar();

    void setUseScreenMenuBar(boolean useScreenMenuBar);

    @Nonnull
    String getMacOsAppearance();

    void setMacOsAppearance(String macOsAppearance);
}
