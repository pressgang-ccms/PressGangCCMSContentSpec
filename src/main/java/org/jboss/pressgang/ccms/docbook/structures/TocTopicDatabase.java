package org.jboss.pressgang.ccms.docbook.structures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.pressgang.ccms.contentspec.wrapper.BaseTopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TopicWrapper;
import org.jboss.pressgang.ccms.utils.common.CollectionUtilities;

/**
 * This class represents all the topics that will go into a docbook build, along with some function to retrieve topics based on
 * a set of tags to match or exclude.
 */
public class TocTopicDatabase {
    private Set<BaseTopicWrapper<?>> topics = new HashSet<BaseTopicWrapper<?>>();

    public void addTopic(final TopicWrapper topic) {
        if (!containsTopic(topic))
            topics.add(topic);
    }

    public boolean containsTopic(final TopicWrapper topic) {
        return topics.contains(topic);
    }

    public boolean containsTopic(final Integer topicId) {
        return getTopic(topicId) != null;
    }

    public BaseTopicWrapper<?> getTopic(final Integer topicId) {
        for (final BaseTopicWrapper<?> topic : topics) {
            if (topic.getId().equals(topicId)) {
                return topic;
            }
        }

        return null;
    }

    public boolean containsTopicsWithTag(final TagWrapper tag) {
        return getMatchingTopicsFromTag(tag).size() != 0;
    }

    public boolean containsTopicsWithTag(final Integer tag) {
        return getMatchingTopicsFromInteger(tag).size() != 0;
    }

    public List<TagWrapper> getTagsFromCategories(final List<Integer> categoryIds) {
        final List<TagWrapper> retValue = new ArrayList<TagWrapper>();

        for (final BaseTopicWrapper<?> topic : topics) {
            final List<TagWrapper> topicTags = topic.getTagsInCategories(categoryIds);
            CollectionUtilities.addAllThatDontExist(topicTags, retValue);
        }

        return retValue;
    }

    public List<BaseTopicWrapper<?>> getMatchingTopicsFromInteger(final List<Integer> matchingTags, final List<Integer> excludeTags,
            final boolean haveOnlyMatchingTags, final boolean landingPagesOnly) {
        assert matchingTags != null : "The matchingTags parameter can not be null";
        assert excludeTags != null : "The excludeTags parameter can not be null";

        final List<BaseTopicWrapper<?>> topicList = new ArrayList<BaseTopicWrapper<?>>();

        for (final BaseTopicWrapper<?> topic : topics) {
            /* landing pages have negative topic ids */
            if (landingPagesOnly && topic.getId() >= 0)
                continue;

            /* check to see if the topic has only the matching tags */
            if (haveOnlyMatchingTags && topic.getTags().size() != matchingTags.size())
                continue;

            /* check for matching tags */
            boolean foundMatchingTag = true;
            for (final Integer matchingTag : matchingTags) {
                if (!topic.hasTag(matchingTag)) {
                    foundMatchingTag = false;
                    break;
                }
            }
            if (!foundMatchingTag)
                continue;

            /* check for excluded tags */
            boolean foundExclusionTag = false;
            for (final Integer excludeTag : excludeTags) {
                if (topic.hasTag(excludeTag)) {
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

    public List<BaseTopicWrapper<?>> getMatchingTopicsFromInteger(final Integer matchingTag, final List<Integer> excludeTags,
            final boolean haveOnlyMatchingTags) {
        return getMatchingTopicsFromInteger(CollectionUtilities.toArrayList(matchingTag), excludeTags, haveOnlyMatchingTags,
                false);
    }

    public List<BaseTopicWrapper<?>> getMatchingTopicsFromInteger(final Integer matchingTag, final Integer excludeTag,
            final boolean haveOnlyMatchingTags) {
        return getMatchingTopicsFromInteger(matchingTag, CollectionUtilities.toArrayList(excludeTag), haveOnlyMatchingTags);
    }

    public List<BaseTopicWrapper<?>> getMatchingTopicsFromInteger(final List<Integer> matchingTags, final Integer excludeTag,
            final boolean haveOnlyMatchingTags) {
        return getMatchingTopicsFromInteger(matchingTags, CollectionUtilities.toArrayList(excludeTag), haveOnlyMatchingTags,
                false);
    }

    public List<BaseTopicWrapper<?>> getMatchingTopicsFromInteger(final List<Integer> matchingTags, final List<Integer> excludeTags) {
        return getMatchingTopicsFromInteger(matchingTags, excludeTags, false, false);
    }

    public List<BaseTopicWrapper<?>> getMatchingTopicsFromInteger(final Integer matchingTag, final List<Integer> excludeTags) {
        return getMatchingTopicsFromInteger(matchingTag, excludeTags, false);
    }

    public List<BaseTopicWrapper<?>> getMatchingTopics(final Integer matchingTag, final Integer excludeTag) {
        return getMatchingTopicsFromInteger(matchingTag, excludeTag, false);
    }

    public List<BaseTopicWrapper<?>> getMatchingTopicsFromInteger(final List<Integer> matchingTags, final Integer excludeTag) {
        return getMatchingTopicsFromInteger(matchingTags, excludeTag, false);
    }

    public List<BaseTopicWrapper<?>> getMatchingTopicsFromInteger(final Integer matchingTag) {
        return getMatchingTopicsFromInteger(matchingTag, new ArrayList<Integer>(), false);
    }

    public List<BaseTopicWrapper<?>> getMatchingTopicsFromInteger(final List<Integer> matchingTags) {
        return getMatchingTopicsFromInteger(matchingTags, new ArrayList<Integer>(), false, false);
    }

    public List<BaseTopicWrapper<?>> getTopics() {
        return CollectionUtilities.toArrayList(topics);
    }

    public List<BaseTopicWrapper<?>> getNonLandingPageTopics() {
        final List<BaseTopicWrapper<?>> retValue = new ArrayList<BaseTopicWrapper<?>>();
        for (final BaseTopicWrapper<?> topic : topics)
            if (topic.getId() >= 0)
                retValue.add(topic);
        return retValue;
    }

    public void setTopics(final List<BaseTopicWrapper<?>> topics) {
        if (topics == null)
            return;

        this.topics = new HashSet<BaseTopicWrapper<?>>(topics);
    }

    public List<BaseTopicWrapper<?>> getMatchingTopicsFromTag(final List<TagWrapper> matchingTags, final List<TagWrapper> excludeTags) {
        return getMatchingTopicsFromInteger(convertTagArrayToIntegerArray(matchingTags),
                convertTagArrayToIntegerArray(excludeTags), false, false);
    }

    public List<BaseTopicWrapper<?>> getMatchingTopicsFromTag(final TagWrapper matchingTag, final List<TagWrapper> excludeTags) {
        if (matchingTag == null)
            return null;

        return getMatchingTopicsFromInteger(matchingTag.getId(), convertTagArrayToIntegerArray(excludeTags), false);
    }

    public List<BaseTopicWrapper<?>> getMatchingTopicsFromTag(final TagWrapper matchingTag, final TagWrapper excludeTag) {
        if (matchingTag == null || excludeTag == null)
            return null;

        return getMatchingTopicsFromInteger(matchingTag.getId(), excludeTag.getId(), false);
    }

    public List<BaseTopicWrapper<?>> getMatchingTopicsFromTag(final List<TagWrapper> matchingTags, final TagWrapper excludeTag) {
        if (excludeTag == null)
            return null;

        return getMatchingTopicsFromInteger(convertTagArrayToIntegerArray(matchingTags), excludeTag.getId(), false);
    }

    public List<BaseTopicWrapper<?>> getMatchingTopicsFromTag(final TagWrapper matchingTag) {
        if (matchingTag == null)
            return null;

        return getMatchingTopicsFromInteger(matchingTag.getId(), new ArrayList<Integer>(), false);
    }

    public List<BaseTopicWrapper<?>> getMatchingTopicsFromTag(final List<TagWrapper> matchingTags) {
        return getMatchingTopicsFromInteger(convertTagArrayToIntegerArray(matchingTags), new ArrayList<Integer>(), false, false);
    }

    private List<Integer> convertTagArrayToIntegerArray(final List<TagWrapper> tags) {
        final List<Integer> retValue = new ArrayList<Integer>();
        for (final TagWrapper tag : tags)
            if (tag != null)
                retValue.add(tag.getId());
        return retValue;
    }
}