package org.jboss.pressgang.ccms.contentspec.sort;

import java.util.Comparator;

/**
 * A comparator to compare numbers that could be null.
 * 
 * Currently Supports: Integer, Long, Float & Double.
 * 
 * @author lnewson
 *
 */
public class NullNumberSort<T extends Number> implements Comparator<T>
{

	public int compare(final T o1, final T o2)
	{
		// Check for null values
		if (o1 == null && o2 == null)
			return 0;
		if (o1 == null)
			return 1;
		if (o2 == null)
			return -1;
		
		if (o1 instanceof Integer)
		{
		    return ((Integer)o1).compareTo((Integer) o2);
		}
		else if (o1 instanceof Long)
        {
            return ((Long)o1).compareTo((Long) o2);
        }
		else if (o1 instanceof Double)
        {
            return ((Double)o1).compareTo((Double) o2);
        }
		else if (o1 instanceof Float)
        {
            return ((Float)o1).compareTo((Float) o2);
        }
		
		return 0;
	}

}
