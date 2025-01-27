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
package org.exbin.framework.utils;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Some simple static methods usable for version.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class VersionUtils {

    private VersionUtils() {
    }

    /**
     * Compares version string to another version string.
     * <p>
     * Compares segments of the version separated by dot character. Numerical
     * segments are compared for value, string segments by character order.
     *
     * @param version compared version
     * @param thanVersion base version
     * @return true when version is greater
     */
    public static boolean isGreaterThan(String version, String thanVersion) {
        int versionSegment = 0;
        int thanVersionSegment = 0;

        while (versionSegment < version.length()) {
            int versionSplit = version.indexOf(".", versionSegment);
            if (versionSplit == -1) {
                versionSplit = version.length();
            }
            int thanVersionSplit = thanVersion.indexOf(".", thanVersionSegment);
            if (thanVersionSplit == -1) {
                thanVersionSplit = thanVersion.length();
            }

            String segment = version.substring(versionSegment, versionSplit);
            String thanSegment = thanVersion.substring(thanVersionSegment, thanVersionSplit);

            if (thanSegment.endsWith("-SNAPSHOT")) {
                thanSegment = thanSegment.substring(0, thanSegment.length() - 9);
                if (thanSegment.equals(segment)) {
                    return true;
                }
            }

            if (segment.endsWith("-SNAPSHOT")) {
                segment = segment.substring(0, segment.length() - 9);
                if (thanSegment.equals(segment)) {
                    return false;
                }
            }

            try {
                int num = Integer.parseInt(segment);
                int thanNum = Integer.parseInt(thanSegment);

                if (num != thanNum) {
                    return num > thanNum;
                }
            } catch (NumberFormatException ex) {
                int compareTo = segment.compareToIgnoreCase(thanSegment);
                if (compareTo == -1) {
                    return false;
                } else if (compareTo == 1) {
                    return true;
                }
            }

            versionSegment = versionSplit + 1;
            thanVersionSegment = thanVersionSplit + 1;
            
            if (thanVersionSegment >= thanVersion.length() && versionSegment < version.length()) {
                return true;
            }
        }

        return false;
    }
}
