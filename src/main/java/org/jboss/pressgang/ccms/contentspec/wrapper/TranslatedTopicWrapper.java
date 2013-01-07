package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.util.Date;

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

    void tempSetTranslationPercentage(Integer percentage);

    void setTranslationPercentage(Integer percentage);

    Date getHtmlUpdated();

    void tempSetHtmlUpdated(Date htmlUpdated);

    void setHtmlUpdated(Date htmlUpdated);

    CollectionWrapper<TranslatedTopicStringWrapper> getTranslatedStrings();

    TopicWrapper getTopic();

    void setTopic(TopicWrapper topic);
}
