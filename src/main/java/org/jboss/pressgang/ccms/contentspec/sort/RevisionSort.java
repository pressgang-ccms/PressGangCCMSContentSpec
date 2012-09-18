package org.jboss.pressgang.ccms.contentspec.sort;

import java.util.Comparator;

/**
 * A comparator to compare the various revisions of the a
 * topic or content spec so that the are in ascending order.
 * 
 * @author lnewson
 *
 */
public class RevisionSort implements Comparator<Object[]>
{

	public int compare(final Object[] o1, final Object[] o2)
	{
		// Check for null values
		if (o1 == null && o2 == null)
			return 0;
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;
				
		final Number rev1 = (Number)o1[0];
		final Number rev2 = (Number)o2[0];
		
		if (rev1 == null && rev2 == null)
			return 0;
		if (rev1 == null)
			return -1;
		if (rev2 == null)
			return 1;
		
		return ((Integer) rev1).compareTo((Integer) rev2);
	}

}
