package org.jboss.pressgang.ccms.contentspec;

import org.jboss.pressgang.ccms.contentspec.enums.TopicType;
import org.jboss.pressgang.ccms.wrapper.base.BaseTopicWrapper;
import org.w3c.dom.Document;

public interface ITopicNode extends IOptionsNode {
    /**
     * Set the ID for the Content Specification Topic.
     *
     * @param id The Content Specification Topic ID.
     */
    void setId(final String id);
    /**
     * Get the ID for the Content Specification Topic.
     *
     * @return The Topic ID.
     */
    String getId();

    /**
     * Sets the Database ID for the Topic.
     *
     * @param id The Database ID for the Topic.
     */
    void setDBId(Integer id);
    /**
     * Get the database ID for the Content Specification Topic.
     *
     * @return The Topics database ID.
     */
    Integer getDBId();

    /**
     * Get the revision number of the topic that the Spec Topic represents.
     *
     * @return The revision number for the underlying topic or null if the Spec Topic represents the latest copy.
     */
    Integer getRevision();
    /**
     * Set the revision number for the underlying topic that the Spec Topic represents.
     *
     * @param revision The underlying topic revision number or null if its the latest revision.
     */
    void setRevision(final Integer revision);

    /**
     * Set the Unique ID for the Content Specification Topic, as well as cleans the string to be alphanumeric.
     *
     * @param uniqueId The Unique Content Specification Topic ID.
     */
    void setUniqueId(final String uniqueId);
    /**
     * Gets the Content Specification Unique ID for the topic.
     *
     * @return The Unique CS Topic ID.
     */
    String getUniqueId();

    /**
     * Set the Unique ID for the Content Specification Topic, as well as cleans the string to be alphanumeric.
     *
     * @param translationUniqueId The Unique Content Specification Translated Topic ID.
     */
    void setTranslationUniqueId(final String translationUniqueId);

    /**
     * Gets the Content Specification Translation Unique ID for the topic.
     *
     * @return The Unique CS Translated Topic ID.
     */
    String getTranslationUniqueId();

    String getDuplicateId();
    void setDuplicateId(final String duplicateId);

    /**
     * Checks to see if the topic is a new topic based on its ID.
     *
     * @return True if the topic is a new Topic otherwise false.
     */
    boolean isTopicANewTopic();
    /**
     * Checks to see if the topic is an existing topic based on its ID.
     *
     * @return True if the topic is a existing Topic otherwise false.
     */
    boolean isTopicAnExistingTopic();
    /**
     * Checks to see if the topic is a cloned topic based on its ID.
     *
     * @return True if the topic is a cloned Topic otherwise false.
     */
    boolean isTopicAClonedTopic();
    /**
     * Checks to see if the topic is a duplicated topic based on its ID.
     *
     * @return True if the topic is a duplicated Topic otherwise false.
     */
    boolean isTopicADuplicateTopic();
    /**
     * Checks to see if the topic is a Duplicated Cloned topic based on its ID.
     *
     * @return True if the topic is a Duplicated Cloned Topic otherwise false.
     */
    boolean isTopicAClonedDuplicateTopic();

    TopicType getTopicType();
    Integer getStep();

    BaseTopicWrapper<?> getTopic();
    void setTopic(BaseTopicWrapper<?> topic);
    Document getXMLDocument();
    void setXMLDocument(Document document);
    SpecTopic getClosestTopicByDBId(final Integer DBId, final boolean checkParentNode);
    SpecNode getClosestSpecNodeByTargetId(final String targetId, final boolean checkParentNode);
}