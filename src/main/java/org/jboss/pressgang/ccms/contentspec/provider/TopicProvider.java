package org.jboss.pressgang.ccms.contentspec.provider;

import java.util.List;

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

    CollectionWrapper<TopicWrapper> getTopicsWithQuery(String query);

    CollectionWrapper<TagWrapper> getTopicTags(int id, final Integer revision);

    UpdateableCollectionWrapper<PropertyTagWrapper> getTopicProperties(int id, Integer revision);

    CollectionWrapper<TopicWrapper> getTopicOutgoingRelationships(int id, Integer revision);

    CollectionWrapper<TopicWrapper> getTopicIncomingRelationships(int id, Integer revision);

    CollectionWrapper<TopicSourceURLWrapper> getTopicSourceUrls(int id, Integer revision);

    CollectionWrapper<TranslatedTopicWrapper> getTopicTranslations(int id, final Integer revision);

    CollectionWrapper<TopicWrapper> getTopicRevisions(int id, Integer revision);

    TopicWrapper createTopic(TopicWrapper topic) throws Exception;

    TopicWrapper updateTopic(TopicWrapper topic) throws Exception;

    boolean deleteTopic(Integer id) throws Exception;

    CollectionWrapper<TopicWrapper> createTopics(final CollectionWrapper<TopicWrapper> topics) throws Exception;

    CollectionWrapper<TopicWrapper> updateTopics(final CollectionWrapper<TopicWrapper> topics) throws Exception;

    boolean deleteTopics(final List<Integer> topicIds) throws Exception;

    TopicWrapper newTopic();

    CollectionWrapper<TopicWrapper> newTopicCollection();
}
