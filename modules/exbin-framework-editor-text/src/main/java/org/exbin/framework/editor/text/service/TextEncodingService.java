/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.exbin.framework.editor.text.service;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Text encoding panel API.
 *
 * @version 0.2.1 2019/07/08
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public interface TextEncodingService {

    /**
     * Returns current encodings used in application frame.
     *
     * @return font
     */
    @Nonnull
    List<String> getEncodings();

    /**
     * Gets selected encoding.
     *
     * @return selected encoding
     */
    @Nonnull
    String getSelectedEncoding();

    /**
     * Sets current encodings used in application frame.
     *
     * @param encodings list of encodings
     */
    void setEncodings(List<String> encodings);

    /**
     * Sets selected encoding.
     *
     * @param encoding encoding
     */
    void setSelectedEncoding(String encoding);

}