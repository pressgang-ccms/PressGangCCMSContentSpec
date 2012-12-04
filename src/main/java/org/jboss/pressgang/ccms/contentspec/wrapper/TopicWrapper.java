package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.util.List;

public interface TopicWrapper extends BaseTopicWrapper<TopicWrapper> {

    @Override
    List<TopicWrapper> getOutgoingRelationships();
    List<TranslatedTopicWrapper> getTranslatedTopics();
    String getEditorURL();
}
