package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.BaseTopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TopicSourceURLWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TranslatedTopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface TranslatedTopicProvider {
    TranslatedTopicWrapper getTranslatedTopic(final int id);
    TranslatedTopicWrapper getTranslatedTopic(final int id, final Integer revision);
    CollectionWrapper<TagWrapper> getTranslatedTopicTags(int id, final Integer revision);
    CollectionWrapper<PropertyTagWrapper> getTranslatedTopicProperties(int id, Integer revision);
    CollectionWrapper<TopicWrapper> getTranslatedTopicOutgoingRelationships(int id, Integer revision);
    CollectionWrapper<TopicWrapper> getTranslatedTopicIncomingRelationships(int id, Integer revision);
    CollectionWrapper<TopicSourceURLWrapper> getTranslatedTopicSourceUrls(int id, Integer revision, BaseTopicWrapper<?> parent);
}
