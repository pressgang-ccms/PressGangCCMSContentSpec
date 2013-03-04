package org.jboss.pressgang.ccms.contentspec;

/**
 * An object to store the contents of a Topic in a Content Specification. It stores the topics name, sequential step number, database ID,
 * unique processed ID,
 * description, an array of urls that relate to the topic and a list of tags. 
 *
 * @author lnewson
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.contentspec.entities.ProcessRelationship;
import org.jboss.pressgang.ccms.contentspec.entities.Relationship;
import org.jboss.pressgang.ccms.contentspec.entities.TargetRelationship;
import org.jboss.pressgang.ccms.contentspec.entities.TopicRelationship;
import org.jboss.pressgang.ccms.contentspec.enums.RelationshipType;
import org.jboss.pressgang.ccms.contentspec.wrapper.base.BaseTopicWrapper;
import org.jboss.pressgang.ccms.utils.common.StringUtilities;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.w3c.dom.Document;

public class SpecTopic extends SpecNode {
    private String id;
    private Integer DBId = null;
    private String type;
    private List<TopicRelationship> topicRelationships = new ArrayList<TopicRelationship>();
    private List<TargetRelationship> topicTargetRelationships = new ArrayList<TargetRelationship>();
    private List<TargetRelationship> levelRelationships = new ArrayList<TargetRelationship>();
    private List<Relationship> relationships = new LinkedList<Relationship>();
    private String targetId = null;
    private String title = null;
    private String duplicateId = null;
    private BaseTopicWrapper<?> topic = null;
    private Document xmlDocument = null;
    private Integer revision = null;

    /**
     * Constructor
     *
     * @param id         The ID for the Content Specification Topic (N, N<ID>, C<ID>, etc...)
     * @param title      The title of the Content Specification Topic.
     * @param lineNumber The post processed Line Number of the topic.
     * @param specLine   The Content Specification Line that is used to create the Topic.
     * @param type       The Topic Type for this topic (Concept, Task, etc...).
     */
    public SpecTopic(final String id, final String title, final int lineNumber, final String specLine, final String type) {
        super(lineNumber, specLine);
        if (id.matches(CSConstants.EXISTING_TOPIC_ID_REGEX)) {
            DBId = Integer.parseInt(id);
        }
        this.id = id;
        this.type = type;
        this.title = title;
    }

    /**
     * Constructor
     *
     * @param title      The title of the Content Specification Topic.
     * @param lineNumber The post processed Line Number of the topic.
     * @param specLine   The Content Specification Line that is used to create the Topic.
     * @param type       The Topic Type for this topic (Concept, Task, etc...).
     */
    public SpecTopic(final String title, final int lineNumber, final String specLine, final String type) {
        super(lineNumber, specLine);
        this.title = title;
        this.type = type;
    }

    /**
     * Constructor
     *
     * @param DBId  The Database ID of a Topic that will be used to create a Content Specification Topic.
     * @param title The Title of the Content Specification Topic.
     */
    public SpecTopic(int DBId, String title) {
        super();
        this.id = Integer.toString(DBId);
        this.DBId = DBId;
        this.title = title;
    }

    // Start of the basic getter/setter methods for this Topic.

    /**
     * Get the underlying topic that this Spec Topic represents.
     *
     * @return The underlying topic if it has been set otherwise null.
     */
    public BaseTopicWrapper<?> getTopic() {
        return topic;
    }

    /**
     * Set the underlying topic that this spec topic represents.
     *
     * @param topic The underlying topic.
     */
    public void setTopic(final BaseTopicWrapper<?> topic) {
        this.topic = topic;
    }

    /**
     * Set the ID for the Content Specification Topic.
     *
     * @param id The Content Specification Topic ID.
     */
    public void setId(final String id) {
        // Set the DBId as well if it isn't a new id
        if (id.matches(CSConstants.EXISTING_TOPIC_ID_REGEX)) {
            DBId = Integer.parseInt(id);
        }
        this.id = id;
    }

    /**
     * Get the ID for the Content Specification Topic.
     *
     * @return The Topic ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the Database ID for the Topic.
     *
     * @param id The Database ID for the Topic.
     */
    public void setDBId(Integer id) {
        DBId = id;
    }

    /**
     * Get the database ID for the Content Specification Topic.
     *
     * @return The Topics database ID.
     */
    public Integer getDBId() {
        return DBId;
    }

    /**
     * Get the revision number of the topic that the Spec Topic represents.
     *
     * @return The revision number for the underlying topic or null if the Spec Topic represents the latest copy.
     */
    public Integer getRevision() {
        return revision;
    }

    /**
     * Set the revision number for the underlying topic that the Spec Topic represents.
     *
     * @param revision The underlying topic revision number or null if its the latest revision.
     */
    public void setRevision(final Integer revision) {
        this.revision = revision;
    }

    /**
     * Gets the title of the topic.
     *
     * @return The topics Title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title for the topic.
     *
     * @param title The title for the topic.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set the Topic Type for the Content Specification Topic.
     *
     * @param type The Topic Type (Concept, Task, etc...).
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the Content Specification Topic Type
     *
     * @return The Topics Type.
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the Target ID for the Content Specification Topic if one exists.
     *
     * @return The Target ID or null if none exist.
     */
    public String getTargetId() {
        return targetId;
    }

    /**
     * Set the Target ID for the Content Specification Topic.
     *
     * @param targetId The Target ID for the Topic.
     */
    public void setTargetId(final String targetId) {
        this.targetId = targetId;
    }

    /**
     * Checks if the target id is only an internally used id, used for processes
     *
     * @return True if the target id is an internal id, otherwise false.
     */
    public boolean isTargetIdAnInternalId() {
        return targetId == null ? false : getTargetId().matches("^T-" + getUniqueId() + "0[0-9]+$");
    }

    /**
     * Add a relationship to the topic.
     *
     * @param topic The topic that is to be related to.
     * @param type  The type of the relationship.
     */
    public void addRelationshipToTopic(final SpecTopic topic, final RelationshipType type) {
        final TopicRelationship relationship = new TopicRelationship(this, topic, type);
        topicRelationships.add(relationship);
        relationships.add(relationship);
    }

    /**
     * Add a relationship to the topic.
     *
     * @param topic The topic that is to be related to.
     * @param type  The type of the relationship.
     * @param title The title of the topic to be related to.
     */
    public void addRelationshipToTopic(final SpecTopic topic, final RelationshipType type, final String title) {
        final TopicRelationship relationship = new TopicRelationship(this, topic, type, title);
        topicRelationships.add(relationship);
        relationships.add(relationship);
    }

    /**
     * Add a relationship to the topic.
     *
     * @param topic The topic that is to be related to.
     * @param type  The type of the relationship.
     */
    public void addRelationshipToTarget(final SpecTopic topic, final RelationshipType type) {
        final TargetRelationship relationship = new TargetRelationship(this, topic, type);
        topicTargetRelationships.add(relationship);
        relationships.add(relationship);
    }

    /**
     * Add a relationship to the topic.
     *
     * @param topic The topic that is to be related to.
     * @param type  The type of the relationship.
     * @param title The title of the topic to be related to.
     */
    public void addRelationshipToTarget(final SpecTopic topic, final RelationshipType type, final String title) {
        final TargetRelationship relationship = new TargetRelationship(this, topic, type, title);
        topicTargetRelationships.add(relationship);
        relationships.add(relationship);
    }

    /**
     * Add a relationship to the topic.
     *
     * @param topic The topic that is to be related to.
     * @param type  The type of the relationship.
     */
    public void addRelationshipToTarget(final Level level, final RelationshipType type) {
        final TargetRelationship relationship = new TargetRelationship(this, level, type);
        levelRelationships.add(relationship);
        relationships.add(relationship);
    }

    /**
     * Add a relationship to the topic.
     *
     * @param topic The topic that is to be related to.
     * @param type  The type of the relationship.
     * @param title The title of the topic to be related to.
     */
    public void addRelationshipToTarget(final Level level, final RelationshipType type, final String title) {
        final TargetRelationship relationship = new TargetRelationship(this, level, type, title);
        levelRelationships.add(relationship);
        relationships.add(relationship);
    }

    /**
     * Add a relationship to the topic.
     *
     * @param topic The topic that is to be related to.
     * @param type  The type of the relationship.
     */
    public void addRelationshipToProcessTopic(final SpecTopic topic, final RelationshipType type) {
        final ProcessRelationship relationship = new ProcessRelationship(this, topic, type);
        topicTargetRelationships.add(relationship);
        relationships.add(relationship);
    }

    /**
     * Add a relationship to the topic.
     *
     * @param topic The topic that is to be related to.
     * @param type  The type of the relationship.
     * @param title The title of the topic to be related to.
     */
    public void addRelationshipToProcessTopic(final SpecTopic topic, final RelationshipType type, final String title) {
        final ProcessRelationship relationship = new ProcessRelationship(this, topic, type, title);
        topicTargetRelationships.add(relationship);
        relationships.add(relationship);
    }

    // End of the basic getter/setter methods for this Topic.

    /**
     * Gets a list of relationships for the Topic.
     */
    public List<Relationship> getRelationships() {
        return Collections.unmodifiableList(relationships);
    }

    /**
     * Gets a list of previous relationships for the Topic.
     */
    public List<Relationship> getPreviousRelationship() {
        final List<Relationship> prevRelationships = new LinkedList<Relationship>();
        for (final Relationship r : relationships) {
            if (r.getType() == RelationshipType.PREVIOUS) {
                prevRelationships.add(r);
            }
        }
        return prevRelationships;
    }

    /**
     * Gets a list of next relationships for the Topic.
     */
    public List<Relationship> getNextRelationships() {
        final List<Relationship> nextRelationships = new LinkedList<Relationship>();
        for (final Relationship r : relationships) {
            if (r.getType() == RelationshipType.NEXT) {
                nextRelationships.add(r);
            }
        }
        return nextRelationships;
    }

    /**
     * Gets a list of prerequisite relationships for the topic.
     */
    public List<Relationship> getPrerequisiteRelationships() {
        final List<Relationship> prerequisiteRelationships = new LinkedList<Relationship>();
        for (final Relationship r : relationships) {
            if (r.getType() == RelationshipType.PREREQUISITE) {
                prerequisiteRelationships.add(r);
            }
        }
        return prerequisiteRelationships;
    }

    /**
     * Gets a list of related relationships for the topic.
     */
    public List<Relationship> getRelatedRelationships() {
        final List<Relationship> relatedRelationships = new LinkedList<Relationship>();
        for (final Relationship r : relationships) {
            if (r.getType() == RelationshipType.REFER_TO) {
                relatedRelationships.add(r);
            }
        }
        return relatedRelationships;
    }

    /**
     * Gets a list of link-list relationships for the topic.
     */
    public List<Relationship> getLinkListRelationships() {
        final List<Relationship> linkListRelationships = new LinkedList<Relationship>();
        for (final Relationship r : relationships) {
            if (r.getType() == RelationshipType.LINKLIST) {
                linkListRelationships.add(r);
            }
        }
        return linkListRelationships;
    }

    @Override
    public Level getParent() {
        return (Level) parent;
    }

    /**
     * Sets the parent for the Content Specification Topic.
     *
     * @param parent The Level that is the parent of this topic.
     */
    protected void setParent(Level parent) {
        super.setParent(parent);
    }

    /**
     * Checks to see if the topic is a new topic based on its ID.
     *
     * @return True if the topic is a new Topic otherwise false.
     */
    public boolean isTopicANewTopic() {
        return id.matches(CSConstants.NEW_TOPIC_ID_REGEX);
    }

    /**
     * Checks to see if the topic is an existing topic based on its ID.
     *
     * @return True if the topic is a existing Topic otherwise false.
     */
    public boolean isTopicAnExistingTopic() {
        return id.matches(CSConstants.EXISTING_TOPIC_ID_REGEX);
    }

    /**
     * Checks to see if the topic is a cloned topic based on its ID.
     *
     * @return True if the topic is a cloned Topic otherwise false.
     */
    public boolean isTopicAClonedTopic() {
        return id.matches(CSConstants.CLONED_TOPIC_ID_REGEX);
    }

    /**
     * Checks to see if the topic is a duplicated topic based on its ID.
     *
     * @return True if the topic is a duplicated Topic otherwise false.
     */
    public boolean isTopicADuplicateTopic() {
        return id.matches(CSConstants.DUPLICATE_TOPIC_ID_REGEX);
    }

    /**
     * Checks to see if the topic is a Duplicated Cloned topic based on its ID.
     *
     * @return True if the topic is a Duplicated Cloned Topic otherwise false.
     */
    public boolean isTopicAClonedDuplicateTopic() {
        return id.matches(CSConstants.CLONED_DUPLICATE_TOPIC_ID_REGEX);
    }

    /**
     * Gets the list of Topic to Topic relationships.
     *
     * @return An ArrayList of TopicRelationship's or an empty array if none are found.
     */
    public List<TopicRelationship> getTopicRelationships() {
        ArrayList<TopicRelationship> relationships = new ArrayList<TopicRelationship>(topicRelationships);
        for (final TargetRelationship relationship : topicTargetRelationships) {
            relationships.add(new TopicRelationship(relationship.getTopic(), (SpecTopic) relationship.getSecondaryRelationship(),
                    relationship.getType()));
        }
        return relationships;
    }

    /**
     * Gets the list of Target relationships.
     *
     * @return A List of TargetRelationship's or an empty array if none are found.
     */
    public List<TargetRelationship> getTargetRelationships() {
        final List<TargetRelationship> relationships = new ArrayList<TargetRelationship>(levelRelationships);
        relationships.addAll(topicTargetRelationships);
        return relationships;
    }

    /**
     * Gets the list of Topic Relationships for this topic whose type is "RELATED".
     *
     * @return A list of related topic relationships
     */
    public List<TopicRelationship> getRelatedTopicRelationships() {
        final ArrayList<TopicRelationship> relationships = new ArrayList<TopicRelationship>();
        /* Check the topic to topic relationships for related relationships */
        for (final TopicRelationship relationship : topicRelationships) {
            if (relationship.getType() == RelationshipType.REFER_TO) {
                relationships.add(relationship);
            }
        }
        /* Check the topic to target relationships for related relationships */
        for (final TargetRelationship relationship : topicTargetRelationships) {
            if (relationship.getType() == RelationshipType.REFER_TO) {
                relationships.add(new TopicRelationship(relationship.getTopic(), (SpecTopic) relationship.getSecondaryRelationship(),
                        relationship.getType()));
            }
        }
        return relationships;
    }

    /**
     * Gets the list of Level Relationships for this topic whose type is "RELATED".
     *
     * @return A list of related level relationships
     */
    public List<TargetRelationship> getRelatedLevelRelationships() {
        final ArrayList<TargetRelationship> relationships = new ArrayList<TargetRelationship>();
        for (final TargetRelationship relationship : levelRelationships) {
            if (relationship.getType() == RelationshipType.REFER_TO) {
                relationships.add(relationship);
            }
        }
        return relationships;
    }

    /**
     * Gets the list of Topic Relationships for this topic whose type is "PREREQUISITE".
     *
     * @return A list of prerequisite topic relationships
     */
    public List<TopicRelationship> getPrerequisiteTopicRelationships() {
        final ArrayList<TopicRelationship> relationships = new ArrayList<TopicRelationship>();
        for (final TopicRelationship relationship : topicRelationships) {
            if (relationship.getType() == RelationshipType.PREREQUISITE) {
                relationships.add(relationship);
            }
        }
        for (final TargetRelationship relationship : topicTargetRelationships) {
            if (relationship.getType() == RelationshipType.PREREQUISITE) {
                relationships.add(new TopicRelationship(relationship.getTopic(), (SpecTopic) relationship.getSecondaryRelationship(),
                        relationship.getType()));
            }
        }
        return relationships;
    }

    /**
     * Gets the list of Level Relationships for this topic whose type is "PREREQUISITE".
     *
     * @return A list of prerequisite level relationships
     */
    public List<TargetRelationship> getPrerequisiteLevelRelationships() {
        final ArrayList<TargetRelationship> relationships = new ArrayList<TargetRelationship>();
        for (final TargetRelationship relationship : levelRelationships) {
            if (relationship.getType() == RelationshipType.PREREQUISITE) {
                relationships.add(relationship);
            }
        }
        return relationships;
    }

    /**
     * Gets the list of Topic Relationships for this topic whose type is "LINKLIST".
     *
     * @return A list of link list topic relationships
     */
    public List<TopicRelationship> getLinkListTopicRelationships() {
        final ArrayList<TopicRelationship> relationships = new ArrayList<TopicRelationship>();
        for (final TopicRelationship relationship : topicRelationships) {
            if (relationship.getType() == RelationshipType.LINKLIST) {
                relationships.add(relationship);
            }
        }
        for (final TargetRelationship relationship : topicTargetRelationships) {
            if (relationship.getType() == RelationshipType.LINKLIST) {
                relationships.add(new TopicRelationship(relationship.getTopic(), (SpecTopic) relationship.getSecondaryRelationship(),
                        relationship.getType()));
            }
        }
        return relationships;
    }

    /**
     * Gets the list of Level Relationships for this topic whose type is "LINKLIST".
     *
     * @return A list of link list level relationships
     */
    public List<TargetRelationship> getLinkListLevelRelationships() {
        final ArrayList<TargetRelationship> relationships = new ArrayList<TargetRelationship>();
        for (final TargetRelationship relationship : levelRelationships) {
            if (relationship.getType() == RelationshipType.LINKLIST) {
                relationships.add(relationship);
            }
        }
        return relationships;
    }

    /**
     * Gets the list of Topic Relationships for this topic whose type is "NEXT".
     *
     * @return A list of next topic relationships
     */
    public List<TopicRelationship> getNextTopicRelationships() {
        ArrayList<TopicRelationship> relationships = new ArrayList<TopicRelationship>();
        for (TopicRelationship relationship : topicRelationships) {
            if (relationship.getType() == RelationshipType.NEXT) {
                relationships.add(relationship);
            }
        }
        for (TargetRelationship relationship : topicTargetRelationships) {
            if (relationship.getType() == RelationshipType.NEXT) {
                relationships.add(new TopicRelationship(relationship.getTopic(), (SpecTopic) relationship.getSecondaryRelationship(),
                        relationship.getType()));
            }
        }
        return relationships;
    }

    /**
     * Gets the list of Topic Relationships for this topic whose type is "PREVIOUS".
     *
     * @return A list of previous topic relationships
     */
    public List<TopicRelationship> getPrevTopicRelationships() {
        ArrayList<TopicRelationship> relationships = new ArrayList<TopicRelationship>();
        for (TopicRelationship relationship : topicRelationships) {
            if (relationship.getType() == RelationshipType.PREVIOUS) {
                relationships.add(relationship);
            }
        }
        for (TargetRelationship relationship : topicTargetRelationships) {
            if (relationship.getType() == RelationshipType.PREVIOUS) {
                relationships.add(new TopicRelationship(relationship.getTopic(), (SpecTopic) relationship.getSecondaryRelationship(),
                        relationship.getType()));
            }
        }
        return relationships;
    }

    @Override
    public Integer getStep() {
        if (getParent() == null) return null;
        Integer previousNode = 0;

        // Get the position of the level in its parents nodes
        Integer nodePos = getParent().nodes.indexOf(this);

        // If the level isn't the first node then get the previous nodes step
        if (nodePos > 0) {
            Node node = getParent().nodes.get(nodePos - 1);
            previousNode = node.getStep();
            // If the add node is a level then add the number of nodes it contains
            if (node instanceof Level) {
                previousNode = (previousNode == null ? 0 : previousNode) + ((Level) node).getTotalNumberOfChildren();
            }
            // The node is the first item so use the parent levels step
        } else {
            previousNode = getParent().getStep();
        }
        // Make sure the previous nodes step isn't 0
        previousNode = previousNode == null ? 0 : previousNode;

        // Add one since we got the previous nodes step
        return previousNode + 1;
    }

    @Override
    public String getText() {
        final StringBuilder output = new StringBuilder();
        if (isTopicANewTopic()) {
            final String options = getOptionsString();
            output.append((title == null ? "" : title) + " [" + id + ", " + type + (options.equals("") ? "" : (", " + options)) + "]");
        } else {
            final String options = getOptionsString();
            output.append((title == null ? "" : title) + " [" + id + (revision == null ? "" : (", rev: " + revision)) + (options.equals(
                    "") ? "" : (", " + options)) + "]");
        }

        if (targetId != null && !((parent instanceof Process) && isTargetIdAnInternalId())) {
            output.append(" [" + targetId + "]");
        }

        final int indentationSize = parent != null ? getColumn() : 0;
        final StringBuilder spacer = new StringBuilder();
        for (int i = 1; i < indentationSize; i++) {
            spacer.append(SPACER);
        }
        spacer.append(SPACER);

        if (!getRelatedRelationships().isEmpty()) {
            boolean useLongSyntax = printRelationshipsWithLongSyntax(getRelatedRelationships());
            output.append(generateRelationshipText(RelationshipType.REFER_TO, !useLongSyntax, spacer.toString()));
        }

        if (!getPrerequisiteRelationships().isEmpty()) {
            boolean useLongSyntax = printRelationshipsWithLongSyntax(getPrerequisiteRelationships());
            output.append(generateRelationshipText(RelationshipType.PREREQUISITE, !useLongSyntax, spacer.toString()));
        }

        if (!getLinkListRelationships().isEmpty()) {
            boolean useLongSyntax = printRelationshipsWithLongSyntax(getLinkListRelationships());
            output.append(generateRelationshipText(RelationshipType.LINKLIST, !useLongSyntax, spacer.toString()));
        }

        setText(output.toString());
        return text;
    }

    /**
     * Checks to see if a list of relationships should be printed using the long syntax.
     *
     * @param relationships The list of relationships to be checked.
     * @return True if the relationships should be printed using the long syntax.
     */
    private boolean printRelationshipsWithLongSyntax(final List<Relationship> relationships) {
        for (final Relationship relationship : relationships) {
            if (relationship.getRelationshipTitle() != null && !relationship.getRelationshipTitle().trim().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates the relationship text to be added to a topic's text.
     *
     * @param relationshipType The type of relationship to generate the text for.
     * @param shortSyntax      If the short relationship syntax should be used.
     * @param spacer           The spacer that should be added to the start of every new line.
     * @return The generated relationship text.
     */
    protected String generateRelationshipText(final RelationshipType relationshipType, boolean shortSyntax, final String spacer) {
        final StringBuilder retValue;
        final List<Relationship> relationships;
        // Create the relationship heading
        if (relationshipType == RelationshipType.REFER_TO) {
            if (shortSyntax) {
                retValue = new StringBuilder(" [R: ");
            } else {
                retValue = new StringBuilder("\n" + spacer + "[Refer-to:");
            }
            relationships = getRelatedRelationships();
        } else if (relationshipType == RelationshipType.PREREQUISITE) {
            if (shortSyntax) {
                retValue = new StringBuilder(" [P: ");
            } else {
                retValue = new StringBuilder("\n" + spacer + "[Prerequisite:");
            }
            relationships = getPrerequisiteRelationships();
        } else if (relationshipType == RelationshipType.LINKLIST) {
            if (shortSyntax) {
                retValue = new StringBuilder(" [L: ");
            } else {
                retValue = new StringBuilder("\n" + spacer + "[Link-List:");
            }
            relationships = getLinkListRelationships();
        } else {
            throw new IllegalArgumentException("Unable to create a text based formation for the " + relationshipType.toString() + " " +
                    "relationship type.");
        }

        // Create the list of relationships
        if (shortSyntax) {
            final List<String> relatedIds = new ArrayList<String>();
            for (final Relationship related : relationships) {
                relatedIds.add(related.getSecondaryRelationshipTopicId());
            }
            retValue.append(StringUtilities.buildString(relatedIds.toArray(new String[relatedIds.size()]), ", "));
        } else {
            boolean first = true;
            for (final Relationship related : relationships) {
                if (first) {
                    retValue.append("\n");
                    first = false;
                } else {
                    retValue.append(",\n");
                }
                retValue.append(spacer);
                retValue.append(SPACER);
                retValue.append(related.getRelationshipTitle());
                retValue.append(" [");
                retValue.append(related.getSecondaryRelationshipTopicId());
                retValue.append("]");
            }
        }

        retValue.append("]");
        return retValue.toString();
    }

    @Override
    public String toString() {
        final StringBuilder spacer = new StringBuilder();
        final int indentationSize = parent != null ? getColumn() : 0;
        for (int i = 1; i < indentationSize; i++) {
            spacer.append(SPACER);
        }
        return spacer + getText() + "\n";
    }

    @Override
    protected void removeParent() {
        getParent().removeChild(this);
        setParent(null);
    }

    /**
     * Finds the closest node in the contents of a level
     *
     * @param topic The node we need to find the closest match for
     * @return
     */
    public SpecTopic getClosestTopic(final SpecTopic topic, final boolean checkParentNode) {
        /*
         * Check this topic to see if it is the topic we are looking for
         */
        if (this == topic || this.getId().equals(topic.getId())) return this;

        /*
         * If we still haven't found the closest node then check this nodes parents.
         */
        if (getParent() != null) return getParent().getClosestTopic(topic, checkParentNode);

        return null;
    }

    public SpecTopic getClosestTopicByDBId(final Integer DBId, final boolean checkParentNode) {
        /*
         * Check this topic to see if it is the topic we are looking for
         */
        if (this.DBId == DBId) return this;

        /*
         * If we still haven't found the closest node then check this nodes parents.
         */
        if (getParent() != null) return getParent().getClosestTopicByDBId(DBId, checkParentNode);

        return null;
    }

    @Override
    public String getUniqueLinkId(final boolean useFixedUrls) {
        final String topicXRefId;
        if (useFixedUrls) topicXRefId = topic.getXRefPropertyOrId(CommonConstants.FIXED_URL_PROP_TAG_ID);
        else {
            topicXRefId = topic.getXRefId();
        }

        return topicXRefId + (duplicateId == null ? "" : ("-" + duplicateId));
    }

    public String getDuplicateId() {
        return duplicateId;
    }

    public void setDuplicateId(final String duplicateId) {
        this.duplicateId = duplicateId;
    }

    public Document getXmlDocument() {
        return xmlDocument;
    }

    public void setXmlDocument(final Document xmlDocument) {
        this.xmlDocument = xmlDocument;
    }
}
