package org.jboss.pressgang.ccms.contentspec.wrapper;

public interface TranslatedTopicStringWrapper extends EntityWrapper<TranslatedTopicStringWrapper> {
    String getOriginalString();
    String getTranslatedString();
    Boolean isFuzzy();
}
