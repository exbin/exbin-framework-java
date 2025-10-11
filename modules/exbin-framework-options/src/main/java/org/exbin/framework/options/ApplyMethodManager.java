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
package org.exbin.framework.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.options.api.ApplyMethodManagement;
import org.exbin.framework.options.api.ApplyMethod;
import org.exbin.framework.options.api.ApplyMethodRule;
import org.exbin.framework.preferences.api.OptionsStorage;

/**
 * Options apply manager.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class ApplyMethodManager implements ApplyMethodManagement {

    private final Map<Class<?>, List<ApplyMethod<?>>> applyMethods = new HashMap<>();
    private final Map<Class<?>, List<ApplyMethodRule>> applyMethodsRules = new HashMap<>();

    @Override
    public <T> void registerApplyMethod(T instanceClass, ApplyMethod<T> applyMethod) {
        List<ApplyMethod<?>> methods = applyMethods.get(instanceClass);
        if (methods == null) {
            methods = new ArrayList<>();
            applyMethods.put(instanceClass.getClass(), methods);
        }

        methods.add(applyMethod);
    }

    @Override
    public void registerApplyMethodRule(ApplyMethodRule applyMethodRule) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @SuppressWarnings("unchecked")
    public void applyOptions(Object targetObject, OptionsStorage optionsStorage) {
        List<ApplyMethod<?>> methods = applyMethods.get(targetObject.getClass());
        if (methods == null) {
            return;
        }

        for (ApplyMethod applyMethod : methods) {
            applyMethod.applyOptions(targetObject, optionsStorage);
        }
    }
}
