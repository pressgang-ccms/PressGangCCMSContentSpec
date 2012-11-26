package org.jboss.pressgang.ccms.contentspec.wrapper;

import java.util.List;

import org.jboss.pressgang.ccms.zanata.ZanataDetails;

public interface TopicWrapper extends EntityWrapper<TopicWrapper> {
    /**
     * Get the revision number of the Topic.
     * 
     * @return The revision number for the topic.
     */
    Integer getTopicRevision();
    /**
     * Get the Topics Title.
     * 
     * @return The topic title.
     */
    String getTopicTitle();
    /**
     * Get the Topics XML Content.
     * 
     * @return The topics XML.
     */
    String getTopicXml();
    String getLocale();
    boolean hasTag(final int tagId);
    List<TagWrapper> getTags();
    List<TopicWrapper> getOutgoingRelationships();
    List<TagWrapper> getTagsInCategories(final List<Integer> categoryIds);
    PropertyTagWrapper getProperty(final int propertyId);
    String getBugzillaBuildId();
    String getEditorURL(final ZanataDetails zanataDetails);
    String getInternalURL();
    String getErrorXRefId();
    String getXRefId();
    String getXRefPropertyOrId(final int propertyId);
    boolean isTranslation();
}
