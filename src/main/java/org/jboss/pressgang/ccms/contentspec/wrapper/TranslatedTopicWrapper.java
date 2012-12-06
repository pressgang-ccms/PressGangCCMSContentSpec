package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface TranslatedTopicWrapper extends BaseTopicWrapper<TranslatedTopicWrapper> {
    Integer getTranslationPercentage();
    CollectionWrapper<TranslatedTopicStringWrapper> getTranslatedStrings();
}
