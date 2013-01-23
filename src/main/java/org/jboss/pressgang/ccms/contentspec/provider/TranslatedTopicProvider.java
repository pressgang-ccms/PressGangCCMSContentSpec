package org.jboss.pressgang.ccms.contentspec.provider;

import java.util.List;

import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagInTopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TopicSourceURLWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TranslatedTopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface TranslatedTopicProvider {
    TranslatedTopicWrapper getTranslatedTopic(final int id);

    TranslatedTopicWrapper getTranslatedTopic(final int id, final Integer revision);

    CollectionWrapper<TagWrapper> getTranslatedTopicTags(int id, final Integer revision);

    CollectionWrapper<PropertyTagInTopicWrapper> getTranslatedTopicProperties(int id, Integer revision);

    CollectionWrapper<TranslatedTopicWrapper> getTranslatedTopicOutgoingRelationships(int id, Integer revision);

    CollectionWrapper<TranslatedTopicWrapper> getTranslatedTopicIncomingRelationships(int id, Integer revision);

    CollectionWrapper<TopicSourceURLWrapper> getTranslatedTopicSourceUrls(int id, Integer revision);

    CollectionWrapper<TranslatedTopicWrapper> getTranslatedTopicRevisions(int id, Integer revision);

    TranslatedTopicWrapper createTranslatedTopic(TranslatedTopicWrapper translatedTopic) throws Exception;

    CollectionWrapper<TranslatedTopicWrapper> createTranslatedTopics(
            CollectionWrapper<TranslatedTopicWrapper> translatedTopics) throws Exception;

    TranslatedTopicWrapper updateTranslatedTopic(TranslatedTopicWrapper translatedTopic) throws Exception;

    CollectionWrapper<TranslatedTopicWrapper> updateTranslatedTopics(
            CollectionWrapper<TranslatedTopicWrapper> translatedTopics) throws Exception;

    boolean deleteTranslatedTopic(Integer id) throws Exception;

    boolean deleteTranslatedTopics(List<Integer> ids) throws Exception;

    TranslatedTopicWrapper newTranslatedTopic();

    CollectionWrapper<TranslatedTopicWrapper> newTranslatedTopicCollection();
}
