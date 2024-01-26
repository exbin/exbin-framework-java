/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exbin.framework.basic;

import java.util.Locale;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.swing.ImageIcon;

/**
 * Language provider.
 *
 * @author ExBin Project (http://exbin.org)
 */
public interface LanguageProvider {

    @Nonnull
    Locale getLocale();

    @Nonnull
    Optional<ClassLoader> getClassLoader();

    @Nonnull
    Optional<ImageIcon> getFlag();
}
