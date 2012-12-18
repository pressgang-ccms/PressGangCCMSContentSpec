package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.util.Date;

import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;

public interface TopicWrapper extends BaseTopicWrapper<TopicWrapper> {
    void setProperties(UpdateableCollectionWrapper<PropertyTagWrapper> properties);

    void setTags(CollectionWrapper<TagWrapper> tags);

    void setOutgoingRelationships(CollectionWrapper<TopicWrapper> outgoingTopics);

    void setIncomingRelationships(CollectionWrapper<TopicWrapper> incomingTopics);

    void setTitle(String title);

    String getDescription();

    void tempSetDescription(String description);

    void setDescription(String description);

    Date getCreated();

    void setCreated(Date created);

    Date getLastModified();

    void setLastModified(Date lastModified);

    CollectionWrapper<TranslatedTopicWrapper> getTranslatedTopics();

    String getEditorURL();
}
