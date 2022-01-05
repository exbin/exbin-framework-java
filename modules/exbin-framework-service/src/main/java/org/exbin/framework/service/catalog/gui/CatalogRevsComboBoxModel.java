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
package org.exbin.framework.service.catalog.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import org.exbin.xbup.core.catalog.base.XBCRev;

/**
 * Catalog Revisions Combo Box Model.
 *
 * @version 0.1.22 2013/05/24
 * @author ExBin Project (http://exbin.org)
 */
public class CatalogRevsComboBoxModel extends AbstractListModel<XBCRev> implements ComboBoxModel<XBCRev> {

    private List<XBCRev> revs;
    private XBCRev selectedRev;

    public CatalogRevsComboBoxModel() {
        revs = new ArrayList<>();
    }

    @Override
    public int getSize() {
        return revs.size();
    }

    @Override
    public XBCRev getElementAt(int index) {
        return revs.get(index);
    }

    public List<XBCRev> getRevs() {
        return revs;
    }

    public void setRevs(List<XBCRev> revs) {
        this.revs = revs;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        setSelectedRev((XBCRev) anItem);
    }

    @Override
    public Object getSelectedItem() {
        return getSelectedRev();
    }

    public XBCRev getSelectedRev() {
        return selectedRev;
    }

    public void setSelectedRev(XBCRev selectedRev) {
        this.selectedRev = selectedRev;
    }

    public void fireDataChanged() {
        fireContentsChanged(this, 0, revs.size() > 0 ? revs.size() - 1 : 0);
    }
}
