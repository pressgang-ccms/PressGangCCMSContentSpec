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

package org.jboss.pressgang.ccms.contentspec.sort;

import java.util.Comparator;

import org.jboss.pressgang.ccms.contentspec.entities.AuthorInformation;

public class AuthorInformationComparator implements Comparator<AuthorInformation> {

    @Override
    public int compare(final AuthorInformation arg0, final AuthorInformation arg1) {
        if (arg0 == null && arg1 == null) {
            return 0;
        } else if (arg0 == null) {
            return -1;
        } else if (arg1 == null) {
            return 1;
        }

		/* Check the last name isn't null */
        if (arg0.getLastName() == null && arg1.getLastName() == null) {
            return 0;
        } else if (arg0.getLastName() == null) {
            return -1;
        } else if (arg1.getLastName() == null) {
            return 1;
        }
		
		/* Check the First Name isn't null */
        if (arg0.getFirstName() == null && arg1.getFirstName() == null) {
            return 0;
        } else if (arg0.getFirstName() == null) {
            return -1;
        } else if (arg1.getFirstName() == null) {
            return 1;
        }

        final int compareVal = arg0.getLastName().compareToIgnoreCase(arg1.getLastName());
        if (compareVal == 0) {
            return arg0.getFirstName().compareToIgnoreCase(arg1.getFirstName());
        } else {
            return compareVal;
        }
    }

}
