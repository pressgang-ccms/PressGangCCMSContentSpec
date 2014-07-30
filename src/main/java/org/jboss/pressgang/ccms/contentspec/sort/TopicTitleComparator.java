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

import org.jboss.pressgang.ccms.wrapper.base.BaseTopicWrapper;

public class TopicTitleComparator implements Comparator<BaseTopicWrapper<?>> {
    public int compare(final BaseTopicWrapper<?> o1, final BaseTopicWrapper<?> o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        if (o1.getTitle() == null && o2.getTitle() == null) return 0;
        if (o1.getTitle() == null) return -1;
        if (o2.getTitle() == null) return 1;

        return o1.getTitle().compareTo(o2.getTitle());
    }
}
