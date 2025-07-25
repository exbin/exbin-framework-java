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

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

@ParametersAreNonnullByDefault
public class DesktopUtils {

    private static final String ERROR_MESSAGE = "Error attempting to launch web browser";
    private static final String OS_NAME = "os.name";

    private DesktopUtils() {
    }

    /**
     * Invokes URL link opening via system call.
     *
     * @param url link URL
     */
    @SuppressWarnings("unchecked")
    public static void openOsURL(String url) {
        // Inspired by "Bare Bones Browser Launch"
        OsType basicOs = detectBasicOs();
        try {
            switch (basicOs) {
                case MACOSX:
                    Class fileMgr = Class.forName("com.apple.eio.FileManager");
                    Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[]{String.class});
                    openURL.invoke(null, new Object[]{url});
                    break;
                case WINDOWS:
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
                    break;
                default:
                    // Assume Unix or Linux
                    String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
                    String browser = null;
                    for (int count = 0; count < browsers.length && browser == null; count++) {
                        if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]}).waitFor() == 0) {
                            browser = browsers[count];
                        }
                    }
                    if (browser == null) {
                        throw new Exception("Could not find web browser");
                    } else {
                        Runtime.getRuntime().exec(new String[]{browser, url});
                    }
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, ERROR_MESSAGE + ":\n" + e.getLocalizedMessage());
        }
    }

    /**
     * Invokes URL link opening via Desktop utility.
     *
     * @param url link URL
     */
    public static void openDesktopURL(final String url) {
        SwingUtilities.invokeLater(() -> {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        java.net.URI uri = new java.net.URI(url);
                        desktop.browse(uri);
                        return;
                    } catch (IOException | URISyntaxException ex) {
                        Logger.getLogger(DesktopUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            DesktopUtils.openOsURL(url);
        });
    }

    /**
     * Invokes URL link opening via Desktop utility.
     *
     * @param uri link URI
     */
    public static void openDesktopURL(final URI uri) {
        SwingUtilities.invokeLater(() -> {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(uri);
                        return;
                    } catch (IOException ex) {
                        Logger.getLogger(DesktopUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            DesktopUtils.openOsURL(uri.toString());
        });
    }

    /**
     * Invokes URL link opening via Desktop utility.
     *
     * @param url link URL
     */
    public static void openDesktopURL(final URL url) {
        SwingUtilities.invokeLater(() -> {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        java.net.URI uri = url.toURI();
                        desktop.browse(uri);
                        return;
                    } catch (IOException | URISyntaxException ex) {
                        Logger.getLogger(DesktopUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            DesktopUtils.openOsURL(url.toString());
        });
    }

    /**
     * Detects basic operating system.
     *
     * @see sun.awt.OSInfo
     * @return basic operating system type
     */
    @Nonnull
    public static OsType detectBasicOs() {
        String osName = System.getProperty(OS_NAME).toLowerCase();
        if (osName.contains("os x")) {
            return OsType.MACOSX;
        }

        if (osName.contains("windows")) {
            return OsType.WINDOWS;
        }

        if (osName.contains("linux")) {
            return OsType.LINUX;
        }

        if (osName.contains("aix")) {
            return OsType.AIX;
        }

        return OsType.UNKNOWN;
    }

    public enum OsType {
        WINDOWS,
        LINUX,
        MACOSX,
        AIX,
        UNKNOWN
    }
}
