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

import org.jboss.pressgang.ccms.contentspec.ITopicNode;

public class TopicNodeLineNumberComparator implements Comparator<ITopicNode> {

    @Override
    public int compare(final ITopicNode o1, final ITopicNode o2) {
        // Check for null values
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        final Integer line1 = o1.getLineNumber();
        final Integer line2 = o2.getLineNumber();

        if (line1 == null && line2 == null) return 0;
        if (line1 == null) return -1;
        if (line2 == null) return 1;

        return line1.compareTo(line2);
    }
}
