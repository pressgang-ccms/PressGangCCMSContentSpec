package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.util.List;

import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;
import org.jboss.pressgang.ccms.zanata.ZanataDetails;

public interface BaseTopicWrapper<T extends BaseTopicWrapper<T>> extends EntityWrapper<T> {
    /**
     * Get the Topics Title.
     * 
     * @return The topic title.
     */
    String getTitle();
    /**
     * Get the Topics XML Content.
     * 
     * @return The topics XML.
     */
    String getXml();
    String getLocale();
    boolean hasTag(final int tagId);
    CollectionWrapper<TagWrapper> getTags();
    CollectionWrapper<T> getOutgoingRelationships();
    CollectionWrapper<T> getIncomingRelationships();
    CollectionWrapper<TagWrapper> getTagsInCategories(final List<Integer> categoryIds);
    CollectionWrapper<PropertyTagWrapper> getProperties();
    CollectionWrapper<TopicSourceURLWrapper> getSourceURLs();
    PropertyTagWrapper getProperty(final int propertyId);
    String getBugzillaBuildId();
    String getEditorURL(final ZanataDetails zanataDetails);
    String getInternalURL();
    String getErrorXRefId();
    String getXRefId();
    String getXRefPropertyOrId(final int propertyId);
    CollectionWrapper<TagWrapper> getAuthors();
}
