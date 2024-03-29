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
package org.exbin.framework.addon.update.api;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Simple structure for application version.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class VersionNumbers {

    private VersionNumbersFormat format;
    private int major;
    private int minor;
    private int release;
    private int patch;

    public VersionNumbers() {
    }

    public void versionFromString(String version) {
        int minorPos = version.indexOf(".");
        major = Integer.parseInt(version.substring(0, minorPos));
        int releasePos = version.indexOf(".", minorPos + 1);
        minor = Integer.parseInt(version.substring(minorPos + 1, releasePos));
        int patchPos = version.indexOf(".", releasePos + 1);
        if (patchPos > 0) {
            format = VersionNumbersFormat.MAJOR_MINOR_RELEASE_PATCH;
            release = Integer.parseInt(version.substring(releasePos + 1, patchPos));
            patch = Integer.parseInt(version.substring(patchPos + 1));
        } else {
            format = VersionNumbersFormat.MAJOR_MINOR_PATCH;
            patch = Integer.parseInt(version.substring(releasePos + 1));
        }
    }

    @Nullable
    public String versionAsString() {
        if (format == null) {
            return null;
        }

        switch (format) {
            case MAJOR_MINOR_PATCH: {
                return major + "." + minor + "." + patch;
            }
            case MAJOR_MINOR_RELEASE_PATCH: {
                return major + "." + minor + "." + release + "." + patch;
            }
            default:
                throw new IllegalStateException("Unexpected format " + format.name());
        }
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getRelease() {
        return release;
    }

    public void setRelease(int release) {
        this.release = release;
    }

    public int getPatch() {
        return patch;
    }

    public void setPatch(int patch) {
        this.patch = patch;
    }

    public boolean isGreaterThan(VersionNumbers updateVersion) {
        if (major > updateVersion.major) {
            return true;
        }
        if (major == updateVersion.major && minor > updateVersion.minor) {
            return true;
        }
        if (minor == updateVersion.minor) {
            switch (format) {
                case MAJOR_MINOR_PATCH: {
                    switch (updateVersion.format) {
                        case MAJOR_MINOR_PATCH: {
                            if (patch > updateVersion.patch) {
                                return true;
                            }
                            break;
                        }

                        case MAJOR_MINOR_RELEASE_PATCH: {
                            if (updateVersion.release == 0 && patch > updateVersion.patch) {
                                return true;
                            }
                            break;
                        }
                        default:
                            throw new IllegalStateException("Unpexpected format type " + updateVersion.format.name());
                    }

                    break;
                }
                case MAJOR_MINOR_RELEASE_PATCH: {
                    switch (updateVersion.format) {
                        case MAJOR_MINOR_PATCH: {
                            if (release > 0) {
                                return true;
                            }
                            if (release == 0 && patch > updateVersion.patch) {
                                return true;
                            }
                            break;
                        }

                        case MAJOR_MINOR_RELEASE_PATCH: {
                            if (release > updateVersion.release) {
                                return true;
                            }
                            if (release == updateVersion.release && patch > updateVersion.patch) {
                                return true;
                            }
                            break;
                        }
                        default:
                            throw new IllegalStateException("Unpexpected format type " + updateVersion.format.name());
                    }

                    break;
                }
                default:
                    throw new IllegalStateException("Unpexpected format type " + format.name());
            }
        }

        return false;
    }

    public static enum VersionNumbersFormat {
        MAJOR_MINOR_PATCH, MAJOR_MINOR_RELEASE_PATCH
    }
}
