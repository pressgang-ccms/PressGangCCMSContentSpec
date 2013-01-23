package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.util.Date;

import org.jboss.pressgang.ccms.contentspec.wrapper.base.BaseTopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface TranslatedTopicWrapper extends BaseTopicWrapper<TranslatedTopicWrapper> {
    void setTopicId(Integer id);

    void setTopicRevision(Integer revision);

    Integer getTranslatedTopicId();

    void setTranslatedTopicId(Integer translatedTopicId);

    String getZanataId();

    boolean getContainsFuzzyTranslations();

    Integer getTranslationPercentage();

    void setTranslationPercentage(Integer percentage);

    Date getHtmlUpdated();

    void setHtmlUpdated(Date htmlUpdated);

    CollectionWrapper<TranslatedTopicStringWrapper> getTranslatedStrings();

    TopicWrapper getTopic();

    void setTopic(TopicWrapper topic);
}
