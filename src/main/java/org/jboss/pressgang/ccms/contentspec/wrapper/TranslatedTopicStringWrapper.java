package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.base.EntityWrapper;

public interface TranslatedTopicStringWrapper extends EntityWrapper<TranslatedTopicStringWrapper> {
    String getOriginalString();

    String getTranslatedString();

    Boolean isFuzzy();
}
