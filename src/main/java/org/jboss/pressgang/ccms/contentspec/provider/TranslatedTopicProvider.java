package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.TranslatedTopicWrapper;

public interface TranslatedTopicProvider {
    TranslatedTopicWrapper getTranslatedTopic(final int id);
    TranslatedTopicWrapper getTranslatedTopic(final int id, final Integer revision);
}
