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
package org.exbin.framework.basic;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Dynamic class loader.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DynamicClassLoader extends URLClassLoader {

    static {
        registerAsParallelCapable();
    }

    /*
     * Required when this classloader is used as the system classloader.
     * Java 8+
     */
//    public DynamicClassLoader(String name, ClassLoader parent) {
//        super(name, new URL[0], parent);
//    }

    /*
     * Required when this classloader is used as the system classloader.
     */
    public DynamicClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    public DynamicClassLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

    void add(URL url) {
        addURL(url);
    }

    @Nullable
    public static DynamicClassLoader findAncestor(@Nonnull ClassLoader cl) {
        do {
            if (cl instanceof DynamicClassLoader) {
                return (DynamicClassLoader) cl;
            }

            cl = cl.getParent();
        } while (cl != null);

        return null;
    }

    /**
     * Required for Java Agents when this classloader is used as the system
     * classloader.
     */
    @SuppressWarnings("unused")
    private void appendToClassPathForInstrumentation(String jarfile) throws IOException {
        add(Paths.get(jarfile).toRealPath().toUri().toURL());
    }
}
