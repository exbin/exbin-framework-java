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
package org.exbin.framework.operation.api;

/**
 * Operation capable of reporting progress.
 *
 * @author ExBin Project (https://exbin.org)
 */
public interface OperationProgressStateListener {

    /**
     * Reports progress state of the operation in range of 0 to 100 or -1 if
     * state is unknown or currenclty undetemined.
     *
     * @param progress progress state of the operation or -1 if unknown
     */
    void operationInProgress(int progress);
}
