/*
  Copyright 2011-2014 Red Hat

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

import org.jboss.pressgang.ccms.contentspec.entities.Revision;

/**
 * A comparator to compare the various revisions of the a
 * topic or content spec so that the are in ascending order.
 *
 * @author lnewson
 */
public class EnversRevisionSort implements Comparator<Revision> {

    public int compare(final Revision o1, Revision o2) {
        // Check for null values
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        final Number rev1 = o1.getRevision();
        final Number rev2 = o2.getRevision();

        if (rev1 == null && rev2 == null) return 0;
        if (rev1 == null) return -1;
        if (rev2 == null) return 1;

        return ((Integer) rev1).compareTo((Integer) rev2);
    }

}
