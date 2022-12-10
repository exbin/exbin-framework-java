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
package org.exbin.framework;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Framework utilities.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class XBFrameworkUtils {

    public static final String NULL_FIELD_ERROR = "Field cannot be null";

    private XBFrameworkUtils() {
    }

    @Nonnull
    public static <T> T requireNonNull(@Nullable T object) {
        return Objects.requireNonNull(object, NULL_FIELD_ERROR);
    }

    @Nonnull
    public static <T> T requireNonNull(@Nullable T object, String message) {
        return Objects.requireNonNull(object, message);
    }

    public static void requireNonNull(Object... objects) {
        for (Object object : objects) {
            Objects.requireNonNull(object, NULL_FIELD_ERROR);
        }
    }

    public static void throwInvalidTypeException(Enum<?> enumObject) {
        throw getInvalidTypeException(enumObject);
    }

    @Nonnull
    public static IllegalStateException getInvalidTypeException(Enum<?> enumObject) {
        return new IllegalStateException("Unexpected " + enumObject.getDeclaringClass().getName() + " value " + enumObject.name());
    }
}
