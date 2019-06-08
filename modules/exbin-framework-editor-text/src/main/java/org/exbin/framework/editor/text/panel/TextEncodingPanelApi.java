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
package org.exbin.framework.editor.text.panel;

import java.util.List;

/**
 * XB Text Editor Module.
 *
 * @version 0.1.22 2013/03/17
 * @author ExBin Project (http://exbin.org)
 */
public interface TextEncodingPanelApi {

    /**
     * Returns current encodings used in application frame.
     *
     * @return font
     */
    List<String> getEncodings();

    /**
     * Gets selected encoding.
     *
     * @return selected encoding
     */
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
