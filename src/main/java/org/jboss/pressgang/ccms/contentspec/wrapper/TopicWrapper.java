package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface TopicWrapper extends BaseTopicWrapper<TopicWrapper> {

    @Override
    CollectionWrapper<TopicWrapper> getOutgoingRelationships();
    CollectionWrapper<TranslatedTopicWrapper> getTranslatedTopics();
    String getEditorURL();
}
