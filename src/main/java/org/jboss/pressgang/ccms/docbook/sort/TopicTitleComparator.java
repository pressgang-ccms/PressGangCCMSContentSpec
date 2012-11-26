package org.jboss.pressgang.ccms.docbook.sort;

import java.util.Comparator;

import org.jboss.pressgang.ccms.contentspec.wrapper.TopicWrapper;

public class TopicTitleComparator implements Comparator<TopicWrapper>
{
	public int compare(final TopicWrapper o1, final TopicWrapper o2)
	{
		if (o1 == null && o2 == null)
			return 0;
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;
		
		if (o1.getTopicTitle() == null && o2.getTopicTitle() == null)
			return 0;
		if (o1.getTopicTitle() == null)
			return -1;
		if (o2.getTopicTitle() == null)
			return 1;
		
		return o1.getTopicTitle().compareTo(o2.getTopicTitle());
	}
}
