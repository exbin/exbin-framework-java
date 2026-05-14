/*
 * Copyright (C) ExBin Project, https://exbin.org
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
package org.exbin.jaguif.addon.manager.api;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Addons management cart controller interface.
 */
@ParametersAreNonnullByDefault
public interface AddonsManagementCartController extends AddonsManagementContext {

    /**
     * Checks whether specific module operation is present in cart.
     *
     * @param moduleId module identifier
     * @param variant cart operation variant
     * @return true if present
     */
    boolean isInCart(String moduleId, CartOperationVariant variant);

    /**
     * Adds operation to the cart.
     *
     * @param operation cart operation
     */
    void addCartOperation(CartOperation operation);

    /**
     * Returns list of cart operations.
     *
     * @return cart operations
     */
    @Nonnull
    List<CartOperation> getCartOperations();
}
