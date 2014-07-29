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

package org.jboss.pressgang.ccms.contentspec.enums;

public enum BugLinkType {
    NONE("OFF"), JIRA("Jira"), BUGZILLA("Bugzilla"), OTHER("Other");

    private final String title;

    public static BugLinkType getType(final String type) {
        if (type == null) {
            return null;
        } else if (type.equalsIgnoreCase(BUGZILLA.title) || type.equalsIgnoreCase("ON")) {
            return BUGZILLA;
        } else if (type.equalsIgnoreCase(JIRA.title)) {
            return JIRA;
        } else if (type.equalsIgnoreCase(OTHER.title)) {
            return OTHER;
        } else {
            return NONE;
        }
    }

    private BugLinkType(final String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
