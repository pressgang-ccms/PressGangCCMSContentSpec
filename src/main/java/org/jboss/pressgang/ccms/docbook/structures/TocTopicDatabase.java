package org.jboss.pressgang.ccms.docbook.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.pressgang.ccms.rest.v1.collections.base.RESTBaseCollectionItemV1;
import org.jboss.pressgang.ccms.rest.v1.collections.base.RESTBaseCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.components.ComponentBaseTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTagV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTranslatedTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.base.RESTBaseTopicV1;
import org.jboss.pressgang.ccms.utils.common.CollectionUtilities;

/**
 * This class represents all the topics that will go into a docbook build, along
 * with some function to retrieve topics based on a set of tags to match or
 * exclude.
 */
public class TocTopicDatabase
{
	private Map<RESTBaseTopicV1<?, ?, ?>, TopicProcessingData> topics = new HashMap<RESTBaseTopicV1<?, ?, ?>, TopicProcessingData>();

	public void addTopic(final RESTBaseTopicV1<?, ?, ?> topic)
	{
		if (!containsTopic(topic))
			topics.put(topic, new TopicProcessingData());
	}
	
	public TopicProcessingData getTopicProcessingData(final RESTBaseTopicV1<?, ?, ?> topic)
	{
		if (containsTopic(topic))
			return topics.get(topic);
		return null;
	}

	public boolean containsTopic(final RESTBaseTopicV1<?, ?, ?> topic)
	{
		return topics.keySet().contains(topic);
	}

	public boolean containsTopic(final Integer topicId)
	{
		return getTopic(topicId) != null;
	}

	public RESTBaseTopicV1<?, ?, ?> getTopic(final Integer topicId)
	{
		for (final RESTBaseTopicV1<?, ?, ?> topic : topics.keySet())
			if (topic instanceof RESTTranslatedTopicV1)
			{
				if (((RESTTranslatedTopicV1) topic).getTopicId().equals(topicId))
					return topic;
			}
			else
			{
				if (topic.getId().equals(topicId))
					return topic;
			}

		return null;
	}

	public boolean containsTopicsWithTag(final RESTTagV1 tag)
	{
		return getMatchingTopicsFromTag(tag).size() != 0;
	}

	public boolean containsTopicsWithTag(final Integer tag)
	{
		return getMatchingTopicsFromInteger(tag).size() != 0;
	}

	public List<RESTTagV1> getTagsFromCategories(final List<Integer> categoryIds)
	{
		final List<RESTTagV1> retValue = new ArrayList<RESTTagV1>();

		for (final RESTBaseTopicV1<?, ?, ?> topic : topics.keySet())
		{
			final List<RESTTagV1> topicTags = ComponentBaseTopicV1.returnTagsInCategoriesByID(topic, categoryIds); 			
			CollectionUtilities.addAllThatDontExist(topicTags, retValue);
		}

		return retValue;
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getMatchingTopicsFromInteger(final List<Integer> matchingTags, final List<Integer> excludeTags, final boolean haveOnlyMatchingTags, final boolean landingPagesOnly)
	{
		assert matchingTags != null : "The matchingTags parameter can not be null";
		assert excludeTags != null : "The excludeTags parameter can not be null";

		final List<RESTBaseTopicV1<?, ?, ?>> topicList = new ArrayList<RESTBaseTopicV1<?, ?, ?>>();

		for (final RESTBaseTopicV1<?, ?, ?> topic : topics.keySet())
		{
			/* landing pages ahev negative topic ids */
			if (landingPagesOnly && topic.getId() >= 0)
				continue;
			
			/* check to see if the topic has only the matching tags */
			if (haveOnlyMatchingTags && topic.getTags().returnItems().size() != matchingTags.size())
				continue;
		
			/* check for matching tags */
			boolean foundMatchingTag = true;
			for (final Integer matchingTag : matchingTags)
			{
				if (!ComponentBaseTopicV1.hasTag(topic, matchingTag))
				{
					foundMatchingTag = false;
					break;
				}
			}
			if (!foundMatchingTag)
				continue;

			/* check for excluded tags */
			boolean foundExclusionTag = false;
			for (final Integer excludeTag : excludeTags)
			{
				if (ComponentBaseTopicV1.hasTag(topic, excludeTag))
				{
					foundExclusionTag = true;
					break;
				}
			}
			if (foundExclusionTag)
				continue;

			topicList.add(topic);
		}
		
		/* post conditions */
		if (landingPagesOnly)
			assert (topicList.size() == 0 || topicList.size() == 1) : "Found 2 or more landing pages, when 0 or 1 was expected";

		return topicList;
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getMatchingTopicsFromInteger(final Integer matchingTag, final List<Integer> excludeTags, final boolean haveOnlyMatchingTags)
	{
		return getMatchingTopicsFromInteger(CollectionUtilities.toArrayList(matchingTag), excludeTags, haveOnlyMatchingTags, false);
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getMatchingTopicsFromInteger(final Integer matchingTag, final Integer excludeTag, final boolean haveOnlyMatchingTags)
	{
		return getMatchingTopicsFromInteger(matchingTag, CollectionUtilities.toArrayList(excludeTag), haveOnlyMatchingTags);
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getMatchingTopicsFromInteger(final List<Integer> matchingTags, final Integer excludeTag, final boolean haveOnlyMatchingTags)
	{
		return getMatchingTopicsFromInteger(matchingTags, CollectionUtilities.toArrayList(excludeTag), haveOnlyMatchingTags, false);
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getMatchingTopicsFromInteger(final List<Integer> matchingTags, final List<Integer> excludeTags)
	{
		return getMatchingTopicsFromInteger(matchingTags, excludeTags, false, false);
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getMatchingTopicsFromInteger(final Integer matchingTag, final List<Integer> excludeTags)
	{
		return getMatchingTopicsFromInteger(matchingTag, excludeTags, false);
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getMatchingTopics(final Integer matchingTag, final Integer excludeTag)
	{
		return getMatchingTopicsFromInteger(matchingTag, excludeTag, false);
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getMatchingTopicsFromInteger(final List<Integer> matchingTags, final Integer excludeTag)
	{
		return getMatchingTopicsFromInteger(matchingTags, excludeTag, false);
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getMatchingTopicsFromInteger(final Integer matchingTag)
	{
		return getMatchingTopicsFromInteger(matchingTag, new ArrayList<Integer>(), false);
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getMatchingTopicsFromInteger(final List<Integer> matchingTags)
	{
		return getMatchingTopicsFromInteger(matchingTags, new ArrayList<Integer>(), false, false);
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getTopics()
	{
		return CollectionUtilities.toArrayList(topics.keySet());
	}
	
	public List<RESTBaseTopicV1<?, ?, ?>> getNonLandingPageTopics()
	{
		final List<RESTBaseTopicV1<?, ?, ?>> retValue = new ArrayList<RESTBaseTopicV1<?, ?, ?>>();
		for (final RESTBaseTopicV1<?, ?, ?> topic : topics.keySet())
			if (topic.getId() >= 0)
				retValue.add(topic);		
		return retValue;
	}

	public void setTopics(final List<RESTBaseTopicV1<?, ?, ?>> topics)
	{
		if (topics == null) return;
		
		this.topics = new HashMap<RESTBaseTopicV1<?, ?, ?>, TopicProcessingData>();
		
		for (final RESTBaseTopicV1<?, ?, ?> topic : topics)
			this.topics.put(topic, new TopicProcessingData());
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getMatchingTopicsFromTag(final List<RESTTagV1> matchingTags, final List<RESTTagV1> excludeTags)
	{
		return getMatchingTopicsFromInteger(convertTagArrayToIntegerArray(matchingTags), convertTagArrayToIntegerArray(excludeTags), false, false);
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getMatchingTopicsFromTag(final RESTTagV1 matchingTag, final List<RESTTagV1> excludeTags)
	{
		if (matchingTag == null)
			return null;

		return getMatchingTopicsFromInteger(matchingTag.getId(), convertTagArrayToIntegerArray(excludeTags), false);
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getMatchingTopicsFromTag(final RESTTagV1 matchingTag, final RESTTagV1 excludeTag)
	{
		if (matchingTag == null || excludeTag == null)
			return null;

		return getMatchingTopicsFromInteger(matchingTag.getId(), excludeTag.getId(), false);
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getMatchingTopicsFromTag(final List<RESTTagV1> matchingTags, final RESTTagV1 excludeTag)
	{
		if (excludeTag == null)
			return null;

		return getMatchingTopicsFromInteger(convertTagArrayToIntegerArray(matchingTags), excludeTag.getId(), false);
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getMatchingTopicsFromTag(final RESTTagV1 matchingTag)
	{
		if (matchingTag == null)
			return null;

		return getMatchingTopicsFromInteger(matchingTag.getId(), new ArrayList<Integer>(), false);
	}

	public List<RESTBaseTopicV1<?, ?, ?>> getMatchingTopicsFromTag(final List<RESTTagV1> matchingTags)
	{
		return getMatchingTopicsFromInteger(convertTagArrayToIntegerArray(matchingTags), new ArrayList<Integer>(), false, false);
	}

	private List<Integer> convertTagArrayToIntegerArray(final List<RESTTagV1> tags)
	{
		final List<Integer> retValue = new ArrayList<Integer>();
		for (final RESTTagV1 tag : tags)
			if (tag != null)
				retValue.add(tag.getId());
		return retValue;
	}
}