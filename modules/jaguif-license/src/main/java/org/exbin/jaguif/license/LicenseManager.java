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
package org.exbin.jaguif.license;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.exbin.jaguif.license.api.LicenseManagement;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.license.api.LicenseRecord;

/**
 * License management.
 */
@ParametersAreNonnullByDefault
public class LicenseManager implements LicenseManagement {

    protected final Map<String, LicenseRecord> licenseRecords = new HashMap<>();

    @Override
    public void addRecord(LicenseRecord licenseRecord) {
        licenseRecords.put(licenseRecord.getId(), licenseRecord);
    }

    @Nonnull
    @Override
    public List<LicenseRecord> getRecords() {
        List<LicenseRecord> records = new ArrayList<>();
        records.addAll(licenseRecords.values());
        return records;
    }

    @Override
    public void addUsedLicense(String licenseId, String moduleId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addUsedLicense(String licenseId, String usageDescription, String moduleId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
