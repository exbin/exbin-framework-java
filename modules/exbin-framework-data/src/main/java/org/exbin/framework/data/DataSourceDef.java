/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.framework.data;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Data source definition.
 *
 * @version 0.2.0 2016/03/07
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class DataSourceDef {

    private List<DataFieldDef> fields = new ArrayList<>();

    public DataSourceDef() {
    }

    @Nonnull
    public List<DataFieldDef> getFields() {
        return fields;
    }

    public void setFields(List<DataFieldDef> fields) {
        this.fields = fields;
    }
}