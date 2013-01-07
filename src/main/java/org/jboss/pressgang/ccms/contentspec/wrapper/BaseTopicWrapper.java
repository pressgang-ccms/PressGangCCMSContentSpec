package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.util.List;

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

    void tempSetTitle(String title);

    /**
     * Get the Topics XML Content.
     *
     * @return The topics XML.
     */
    String getXml();

    void tempSetXml(String xml);

    void setXml(String xml);

    String getLocale();

    void tempSetLocale(String locale);

    void setLocale(String locale);

    String getHtml();

    void tempSetHtml(String html);

    void setHtml(String html);

    CollectionWrapper<TagWrapper> getTags();

    void tempSetTags(CollectionWrapper<TagWrapper> tags);

    CollectionWrapper<T> getOutgoingRelationships();

    void tempSetOutgoingRelationships(CollectionWrapper<T> outgoingTopics);

    CollectionWrapper<T> getIncomingRelationships();

    void tempSetIncomingRelationships(CollectionWrapper<T> incomingTopics);

    UpdateableCollectionWrapper<PropertyTagWrapper> getProperties();

    void tempSetProperties(UpdateableCollectionWrapper<PropertyTagWrapper> properties);

    CollectionWrapper<TopicSourceURLWrapper> getSourceURLs();

    void tempSetSourceURLs(CollectionWrapper<TopicSourceURLWrapper> sourceURLs);

    void setSourceURLs(CollectionWrapper<TopicSourceURLWrapper> sourceURLs);

    List<TagWrapper> getTagsInCategories(final List<Integer> categoryIds);

    boolean hasTag(final int tagId);

    PropertyTagWrapper getProperty(final int propertyId);

    String getBugzillaBuildId();

    String getEditorURL(final ZanataDetails zanataDetails);

    String getPressGangURL();

    String getInternalURL();

    String getErrorXRefId();

    String getXRefId();

    String getXRefPropertyOrId(final int propertyId);

    List<TagWrapper> getAuthors();
}
