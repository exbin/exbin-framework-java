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
package org.exbin.framework.editor.xbup.action;

import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.AbstractAction;
import org.exbin.framework.editor.xbup.panel.XBDocTreeTransferHandler;
import org.exbin.framework.gui.utils.ClipboardUtils;

/**
 * Copy item to clipboard action.
 *
 * @version 0.2.0 2016/03/06
 * @author ExBin Project (http://exbin.org)
 */
@ParametersAreNonnullByDefault
public class CopyItemAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
//        Clipboard clipboard = ClipboardUtils.getClipboard();
//        XBDocTreeTransferHandler.XBTSelection selection = new XBDocTreeTransferHandler.XBTSelection(getSelectedItem());
//        clipboard.setContents(selection, selection);
    }
}
