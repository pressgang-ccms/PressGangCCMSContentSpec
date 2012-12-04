package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.util.List;

public interface TranslatedTopicWrapper extends BaseTopicWrapper<TranslatedTopicWrapper> {
    Integer getTranslationPercentage();
    List<TranslatedTopicStringWrapper> getTranslatedStrings();
}
