package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.util.Date;

import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface TopicWrapper extends BaseTopicWrapper<TopicWrapper> {

    String getDescription();

    void setDescription(String description);

    Date getCreated();

    void setCreated(Date created);

    Date getLastModified();

    void setLastModified(Date lastModified);

    CollectionWrapper<TranslatedTopicWrapper> getTranslatedTopics();

    String getEditorURL();
}
