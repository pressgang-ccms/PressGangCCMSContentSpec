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

package org.jboss.pressgang.ccms.contentspec;

import org.jboss.pressgang.ccms.contentspec.enums.LevelType;

/**
 * A class that is used to represent a Section in a book or Content Specification. It can contain other Levels (Sections or
 * Topics) or Nodes (Comments).
 *
 * @author lnewson
 */
public class Section extends Level {

    /**
     * Constructor
     *
     * @param title      The title of the Section.
     * @param specLine   The Content Specification Line that is used to create the Section.
     * @param lineNumber The Line Number of Section in the Content Specification.
     */
    public Section(final String title, final int lineNumber, final String specLine) {
        super(title, lineNumber, specLine, LevelType.SECTION);
    }

    /**
     * Constructor
     *
     * @param title The title of the Section.
     */
    public Section(final String title) {
        super(title, LevelType.SECTION);
    }
}
