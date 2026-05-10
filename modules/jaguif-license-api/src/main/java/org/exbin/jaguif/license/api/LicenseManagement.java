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
package org.exbin.jaguif.license.api;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Interface of the license management.
 */
@ParametersAreNonnullByDefault
public interface LicenseManagement {

    /**
     * Adds license record.
     *
     * @param licenseRecord license record
     */
    void addRecord(LicenseRecord licenseRecord);

    /**
     * Returns all license records.
     *
     * @return license records
     */
    @Nonnull
    List<LicenseRecord> getRecords();

    /**
     * Adds license usage for the specified module.
     *
     * @param licenseId license id
     * @param moduleId module id
     */
    void addUsedLicense(String licenseId, String moduleId);

    /**
     * Adds license usage for the specified module.
     *
     * @param licenseId license id
     * @param usageDescription usage description
     * @param moduleId module id
     */
    void addUsedLicense(String licenseId, String usageDescription, String moduleId);
}
