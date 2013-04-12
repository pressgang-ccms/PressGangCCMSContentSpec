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
