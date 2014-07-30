/*
  Copyright 2011-2014 Red Hat, Inc

  This file is part of PressGang CCMS.

  PressGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PressGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PressGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.contentspec.sort;

import java.util.Comparator;

import org.jboss.pressgang.ccms.contentspec.structures.RevNumber;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RevisionNodeSort implements Comparator<Element> {

    @Override
    public int compare(Element revision1, Element revision2) {
        if (revision1 == null && revision2 == null) {
            return 0;
        }

        if (revision1 == revision2) {
            return 0;
        }

        if (revision1 == null) {
            return -1;
        }

        if (revision2 == null) {
            return 1;
        }

        final RevNumber revnumber1 = getRevnumber(revision1);
        final RevNumber revnumber2 = getRevnumber(revision2);

        if (revnumber1 == null && revnumber2 == null) {
            return 0;
        }

        if (revnumber1 == revnumber2) {
            return 0;
        }

        if (revnumber1 == null) {
            return -1;
        }

        if (revnumber2 == null) {
            return 1;
        }

        // Compare the revision
        return revnumber1.compareTo(revnumber2);
    }

    private RevNumber getRevnumber(final Element revision) {
        final NodeList revnumbers = revision.getElementsByTagName("revnumber");
        // There should only be one revnumber attribute
        if (revnumbers.getLength() > 0) {
            return new RevNumber(revnumbers.item(0).getTextContent());
        } else {
            return null;
        }
    }
}
