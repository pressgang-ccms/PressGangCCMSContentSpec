package org.jboss.pressgang.ccms.docbook.sort;

import java.util.Comparator;

import org.jboss.pressgang.ccms.contentspec.wrapper.BaseTopicWrapper;

public class TopicTitleComparator implements Comparator<BaseTopicWrapper<?>>
{
	public int compare(final BaseTopicWrapper<?> o1, final BaseTopicWrapper<?> o2)
	{
		if (o1 == null && o2 == null)
			return 0;
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;
		
		if (o1.getTitle() == null && o2.getTitle() == null)
			return 0;
		if (o1.getTitle() == null)
			return -1;
		if (o2.getTitle() == null)
			return 1;
		
		return o1.getTitle().compareTo(o2.getTitle());
	}
}
