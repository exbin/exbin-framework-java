/*
 * Copyright (C) ExBin Project
 *
 * This application or library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This application or library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along this application.  If not, see <http://www.gnu.org/licenses/>.
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
