package org.jboss.pressgang.ccms.contentspec.wrapper.base;

import java.util.List;

import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagInTopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TopicSourceURLWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.UpdateableCollectionWrapper;
import org.jboss.pressgang.ccms.zanata.ZanataDetails;

public interface BaseTopicWrapper<T extends BaseTopicWrapper<T>> extends EntityWrapper<T> {
    Integer getTopicId();

    Integer getTopicRevision();

    /**
     * Get the Topics Title.
     *
     * @return The topic title.
     */
    String getTitle();

    void setTitle(String title);

    /**
     * Get the Topics XML Content.
     *
     * @return The topics XML.
     */
    String getXml();

    void setXml(String xml);

    String getLocale();

    void setLocale(String locale);

    String getHtml();

    void setHtml(String html);

    CollectionWrapper<TagWrapper> getTags();

    void setTags(CollectionWrapper<TagWrapper> tags);

    CollectionWrapper<T> getOutgoingRelationships();

    void setOutgoingRelationships(CollectionWrapper<T> outgoingTopics);

    CollectionWrapper<T> getIncomingRelationships();

    void setIncomingRelationships(CollectionWrapper<T> incomingTopics);

    UpdateableCollectionWrapper<PropertyTagInTopicWrapper> getProperties();

    void setProperties(UpdateableCollectionWrapper<PropertyTagInTopicWrapper> properties);

    CollectionWrapper<TopicSourceURLWrapper> getSourceURLs();

    void setSourceURLs(CollectionWrapper<TopicSourceURLWrapper> sourceURLs);

    List<TagWrapper> getTagsInCategories(final List<Integer> categoryIds);

    boolean hasTag(final int tagId);

    PropertyTagInTopicWrapper getProperty(final int propertyId);

    String getBugzillaBuildId();

    String getEditorURL(final ZanataDetails zanataDetails);

    String getPressGangURL();

    String getInternalURL();

    String getErrorXRefId();

    String getXRefId();

    String getXRefPropertyOrId(final int propertyId);
}
