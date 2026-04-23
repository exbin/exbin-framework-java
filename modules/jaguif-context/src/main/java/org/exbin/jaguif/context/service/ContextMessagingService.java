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
package org.exbin.jaguif.context.service;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.jaguif.context.api.ContextStateChangeListener;
import org.exbin.jaguif.context.api.ContextStateUpdateListener;
import org.exbin.jaguif.context.api.StateUpdateType;

/**
 * Context messaging service.
 */
@ParametersAreNonnullByDefault
public class ContextMessagingService {

    protected MessagingThread messagingThread;

    public ContextMessagingService() {
        // TODO Do messaging in background, one at the time
        // TODO Throw out repeated messages
    }

    public void notifyStateChanged(LinkedList<ContextStateChangeListener> changeListeners, @Nullable Object instance) {
        messagingThread = new MessagingThread("contextMessaging");
        messagingThread.notifyStateChanged(changeListeners, instance);
        messagingThread.start();
        try {
            messagingThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(ContextMessagingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void notifyStateUpdated(LinkedList<ContextStateUpdateListener> updateListeners, Object contextInstance, StateUpdateType updateType) {
        messagingThread = new MessagingThread("contextMessaging");
        messagingThread.notifyStateUpdated(updateListeners, contextInstance, updateType);
        messagingThread.start();
        try {
            messagingThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(ContextMessagingService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @ParametersAreNonnullByDefault
    private static class MessagingThread extends Thread {

        private Object contextInstance;
        private StateUpdateType updateType = null;
        private LinkedList<ContextStateChangeListener> changeListeners = null;
        private LinkedList<ContextStateUpdateListener> updateListeners = null;

        private MessagingThread(String threadName) {
            super(threadName);
        }

        private void notifyStateChanged(LinkedList<ContextStateChangeListener> changeListeners, @Nullable Object contextInstance) {
            this.changeListeners = changeListeners;
            this.updateListeners = null;
            this.contextInstance = contextInstance;
            this.updateType = null;
        }

        private void notifyStateUpdated(LinkedList<ContextStateUpdateListener> updateListeners, Object contextInstance, StateUpdateType updateType) {
            this.changeListeners = null;
            this.updateListeners = updateListeners;
            this.contextInstance = contextInstance;
            this.updateType = updateType;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            if (updateType == null) {
                while (!changeListeners.isEmpty()) {
                    if (Thread.interrupted()) {
                        return;
                    }

                    ContextStateChangeListener listener = changeListeners.pop();
                    listener.stateChanged(contextInstance);
                }
            } else {
                while (!updateListeners.isEmpty()) {
                    if (Thread.interrupted()) {
                        return;
                    }

                    ContextStateUpdateListener listener = updateListeners.pop();
                    listener.notifyStateUpdated(contextInstance, updateType);
                }
            }
        }
    }
}
