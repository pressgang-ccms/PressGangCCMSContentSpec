package org.jboss.pressgang.ccms.contentspec.provider;

import java.util.List;

import org.jboss.pressgang.ccms.contentspec.wrapper.BaseTopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TopicSourceURLWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TranslatedTopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface TopicProvider {
    TopicWrapper getTopic(int id);
    TopicWrapper getTopic(int id, final Integer revision);
    CollectionWrapper<TopicWrapper> getTopics(final List<Integer> ids);
    CollectionWrapper<TagWrapper> getTopicTags(int id, final Integer revision);
    UpdateableCollectionWrapper<PropertyTagWrapper> getTopicProperties(int id, Integer revision);
    CollectionWrapper<TopicWrapper> getTopicOutgoingRelationships(int id, Integer revision);
    CollectionWrapper<TopicWrapper> getTopicIncomingRelationships(int id, Integer revision);
    CollectionWrapper<TopicSourceURLWrapper> getTopicSourceUrls(int id, Integer revision, BaseTopicWrapper<?> parent);
    CollectionWrapper<TranslatedTopicWrapper> getTopicTranslations(int id, final Integer revision);
}
