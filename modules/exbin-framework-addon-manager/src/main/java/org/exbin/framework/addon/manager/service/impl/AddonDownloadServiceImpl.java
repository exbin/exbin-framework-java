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
package org.exbin.framework.addon.manager.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.framework.App;
import org.exbin.framework.addon.manager.operation.DownloadOperation;
import org.exbin.framework.addon.manager.operation.model.DownloadItemRecord;
import org.exbin.framework.addon.manager.service.AddonDownloadService;

/**
 * Addon download service implementation.
 *
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class AddonDownloadServiceImpl implements AddonDownloadService {

    @Nonnull
    @Override
    public DownloadOperation createDownloadsOperation(List<DownloadItemRecord> records) {
        return new DownloadOperation() {

            private ItemChangeListener listener;
            private boolean cancelled = false;
            private long totalDownloadSize = 0;
            private long downloadProgress = 0;
            private int lastProgress = 0;

            @Override
            public void run() {
                // Ask for download sizes
                try {
                    for (int i = 0; i < records.size(); i++) {
                        DownloadItemRecord record = records.get(i);

                        if (cancelled) {
                            return;
                        }
                        long contentLength = getContentLength(record.getUrl());
                        totalDownloadSize += contentLength;
                        record.setSize(contentLength);
                        record.setStatus(DownloadItemRecord.Status.CHECKED);
                        listener.itemChanged(i);
                    }
                } catch (Exception ex) {

                    return;
                }

                // Download
                File targetDirectory = new File(App.getConfigDirectory(), "addon-update");
                for (int i = 0; i < records.size(); i++) {
                    DownloadItemRecord record = records.get(i);
                    if (cancelled) {
                        return;
                    }
                    File targetFile = new File(targetDirectory, record.getFileName());
                    record.setStatus(DownloadItemRecord.Status.INPROGRESS);
                    listener.itemChanged(i);
                    downloadToFile(record, i, targetFile);
                    record.setStatus(DownloadItemRecord.Status.DONE);
                    listener.itemChanged(i);
                }
            }

            public void downloadToFile(DownloadItemRecord record, int recordIndex, File targetFile) {
                URL downloadUrl = record.getUrl();
                HttpURLConnection connection = null;
                try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
                    connection = (HttpURLConnection) downloadUrl.openConnection();
                    connection.setConnectTimeout(30 * 1000);
                    connection.setRequestMethod("GET");
                    connection.connect();
                    try (InputStream inputStream = connection.getInputStream()) {
                        final byte[] buffer = new byte[2048];
                        long remaining = record.getSize();
                        while (remaining > 0) {
                            if (cancelled) {
                                return;
                            }
                            int read = inputStream.read(buffer);
                            if (read < 0) {
                                throw new RuntimeException("Could not receive download size for URL " + downloadUrl);
                            }
                            outputStream.write(buffer, 0, read);
                            remaining -= read;
                            downloadProgress += read;
                            int operationProgress = getOperationProgress();
                            if (operationProgress != lastProgress) {
                                lastProgress = operationProgress;
                                listener.itemChanged(recordIndex);
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Could not receive download size for URL " + downloadUrl, e);
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }

            @Override
            public void setItemChangeListener(DownloadOperation.ItemChangeListener listener) {
                this.listener = listener;
            }

            @Override
            public void cancelOperation() {
                cancelled = true;
            }

            @Override
            public boolean isCancelled() {
                return cancelled;
            }

            @Override
            public int getOperationProgress() {
                return (int) (downloadProgress * 1000 / totalDownloadSize);
            }
        };
    }

    public static long getContentLength(URL downloadURL) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) downloadURL.openConnection();
            connection.setRequestMethod("HEAD");
            return connection.getContentLengthLong();
        } catch (IOException e) {
            throw new RuntimeException("Could not receive download size for URL " + downloadURL, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
