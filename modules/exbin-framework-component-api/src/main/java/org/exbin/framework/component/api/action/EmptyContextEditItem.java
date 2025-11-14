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
package org.exbin.framework.component.api.action;

import org.exbin.framework.component.api.ContextEditItem;

/**
 * Empty implementation for clipboard handler for visual component / context
 * menu.
 *
 * @author ExBin Project (https://exbin.org)
 */
public class EmptyContextEditItem implements ContextEditItem {

    @Override
    public void performAddItem() {
    }

    @Override
    public void performEditItem() {
    }

    @Override
    public void performDeleteItem() {
    }

    @Override
    public boolean canAddItem() {
        return false;
    }

    @Override
    public boolean canEditItem() {
        return false;
    }

    @Override
    public boolean canDeleteItem() {
        return false;
    }
}
