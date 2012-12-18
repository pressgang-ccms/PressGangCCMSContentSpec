package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface TranslatedTopicWrapper extends BaseTopicWrapper<TranslatedTopicWrapper> {
    void setTopicId(Integer id);

    void setTopicRevision(Integer revision);

    void tempSetTopicId(Integer id);

    void tempSetTopicRevision(Integer revision);

    Integer getTranslatedTopicId();

    void setTranslatedTopicId(Integer translatedTopicId);

    String getZanataId();

    boolean getContainsFuzzyTranslations();

    Integer getTranslationPercentage();

    void setTranslationPercentage(Integer percentage);

    CollectionWrapper<TranslatedTopicStringWrapper> getTranslatedStrings();

    TopicWrapper getTopic();

    void setTopic(TopicWrapper topic);
}
