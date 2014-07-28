/*
  Copyright 2011-2014 Red Hat

  This file is part of PresGang CCMS.

  PresGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PresGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PresGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.contentspec;

import org.jboss.pressgang.ccms.contentspec.enums.LevelType;

/**
 * A class that is used to represent a Chapter in a book or Content Specification. It can contain other Levels (Sections or
 * Topics) or Nodes (Comments).
 *
 * @author lnewson
 */
public class Preface extends Level {
    /**
     * Constructor
     *
     * @param title      The title of the Chapter.
     * @param specLine   The Content Specification Line that is used to create the Chapter.
     * @param lineNumber The Line Number of Chapter in the Content Specification.
     */
    public Preface(final String title, final int lineNumber, final String specLine) {
        super(title, lineNumber, specLine, LevelType.PREFACE);
    }

    /**
     * Constructor
     *
     * @param title The title of the Chapter.
     */
    public Preface(final String title) {
        super(title, LevelType.PREFACE);
    }

}
