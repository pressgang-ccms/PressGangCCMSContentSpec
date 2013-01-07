package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.TranslatedTopicStringWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface TranslatedTopicStringProvider {
    CollectionWrapper<TranslatedTopicStringWrapper> getTranslatedTopicStringRevisions(int id, Integer revision);

    TranslatedTopicStringWrapper newTranslatedTopicString();

    CollectionWrapper<TranslatedTopicStringWrapper> newTranslatedTopicStringCollection();
}
