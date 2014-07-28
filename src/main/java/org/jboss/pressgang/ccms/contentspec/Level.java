/*
  Copyright 2011-2014 Red Hat

  This file is part of PresGang CCMS.

  PresGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PresGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PresGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.contentspec;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jboss.pressgang.ccms.contentspec.enums.LevelType;
import org.jboss.pressgang.ccms.contentspec.utils.ContentSpecUtilities;
import org.jboss.pressgang.ccms.utils.common.DocBookUtilities;

/**
 * A Class that represents a Level inside of a Content Specification. A Level can either be a Chapter, Section or Appendix. A
 * Level can have children Levels and Content Specifications within it.
 *
 * @author lnewson
 */
public class Level extends SpecNodeWithRelationships {
    /**
     * A list of the topics that are stored directly within the level.
     */
    protected final List<SpecTopic> topics = new ArrayList<SpecTopic>();

    /**
     * A list of the child levels that are stored directly within the level.
     */
    protected final List<Level> levels = new ArrayList<Level>();

    /**
     * A list of the CommonContent topics that are stored directly within the level.
     */
    protected final List<CommonContent> commonContents = new ArrayList<CommonContent>();

    /**
     * A List of all the nodes stored directly within the level.
     */
    protected final LinkedList<Node> nodes = new LinkedList<Node>();
    protected final LevelType type;
    protected String translatedTitle;
    protected String externalTargetId;
    protected InfoTopic infoTopic;

    /**
     * Constructor.
     *
     * @param title      The title of the Level.
     * @param type       The type that the Level is (Chapter, Section, etc...).
     * @param specLine   The Content Specification Line that is used to create the Level.
     * @param lineNumber The Line Number of Level in the Content Specification.
     */
    public Level(final String title, final int lineNumber, final String specLine, final LevelType type) {
        super(lineNumber, specLine);
        this.type = type;
        this.title = title;
    }

    /**
     * Constructor.
     *
     * @param title The title of the Level.
     * @param type  The type that the Level is (Chapter, Section, etc...).
     */
    public Level(final String title, final LevelType type) {
        this.type = type;
        this.title = title;
    }

    // Start of the basic getter/setter methods for this Level.


    /**
     * Gets the translated title of the Level.
     *
     * @return The translated title of the Level.
     */
    public String getTranslatedTitle() {
        return translatedTitle;
    }

    /**
     * Sets the translated title for the Level.
     *
     * @param translatedTitle The translated title for the Level.
     */
    public void setTranslatedTitle(final String translatedTitle) {
        this.translatedTitle = translatedTitle;
    }

    /**
     * Gets the parent of the Level.
     *
     * @return The parent of the level.
     */
    @Override
    public Level getParent() {
        return (Level) parent;
    }

    /**
     * Sets the parent for the level.
     *
     * @param parent A Level that will act as the parent to this level.
     */
    protected void setParent(final Level parent) {
        super.setParent(parent);
    }

    /**
     * Gets a List of all the Content Specification Topics for the level.
     * <p/>
     * Note: The topics may not be in order.
     *
     * @return A List of Content Specification Topics that exist within the level.
     */
    public List<SpecTopic> getSpecTopics() {
        final List<SpecTopic> retValue = new ArrayList<SpecTopic>();
        retValue.addAll(topics);
        return retValue;
    }

    /**
     * Adds a Content Specification Topic to the Level. If the Topic already has a parent, then it is removed from that parent
     * and added to this level.
     *
     * @param specTopic The Content Specification Topic to be added to the level.
     */
    public void appendSpecTopic(final SpecTopic specTopic) {
        topics.add(specTopic);
        nodes.add(specTopic);
        if (specTopic.getParent() != null && specTopic.getParent() instanceof Level) {
            ((Level) specTopic.getParent()).removeSpecTopic(specTopic);
        }
        specTopic.setParent(this);
    }

    /**
     * Removes a Content Specification Topic from the level and removes the level as the topics parent.
     *
     * @param specTopic The Content Specification Topic to be removed from the level.
     */
    public void removeSpecTopic(final SpecTopic specTopic) {
        topics.remove(specTopic);
        nodes.remove(specTopic);
        specTopic.setParent(null);
    }

    /**
     * Gets a List of all the child levels in this level.
     * <p/>
     * Note: The topics may not be in order.
     *
     * @return A List of child levels.
     */
    public List<Level> getChildLevels() {
        return new ArrayList<Level>(levels);
    }

    /**
     * Adds a Child Element to the Level. If the Child Element already has a parent, then it is removed from that parent and
     * added to this level.
     *
     * @param child A Child element to be added to the Level.
     */
    public void appendChild(final Node child) {
        if (child instanceof Level) {
            // Append the level
            levels.add((Level) child);
            nodes.add(child);
            if (child.getParent() != null) {
                child.removeParent();
            }
            child.setParent(this);
        } else if (child instanceof SpecTopic) {
            appendSpecTopic((SpecTopic) child);
        } else if (child instanceof CommonContent) {
            // Append the common content
            commonContents.add((CommonContent) child);
            nodes.add(child);
            if (child.getParent() != null) {
                child.removeParent();
            }
            child.setParent(this);
        } else {
            nodes.add(child);
            if (child.getParent() != null) {
                child.removeParent();
            }
            child.setParent(this);
        }
    }

    /**
     * Removes a Child element from the level and removes the level as the Child's parent.
     *
     * @param child The Child element to be removed from the level.
     */
    public void removeChild(final Node child) {
        if (child instanceof Level) {
            levels.remove(child);
            nodes.remove(child);
            child.setParent(null);
        } else if (child instanceof SpecTopic) {
            removeSpecTopic((SpecTopic) child);
        } else if (child instanceof CommonContent) {
            commonContents.remove(child);
            nodes.remove(child);
            child.setParent(null);
        } else {
            nodes.remove(child);
            child.setParent(null);
        }
    }

    /**
     * Gets the number of Content Specification Topics in the Level.
     *
     * @return The number of Content Specification Topics.
     */
    public int getNumberOfSpecTopics() {
        return topics.size();
    }

    /**
     * Gets the number of Child Levels in the Level.
     *
     * @return The number of Child Levels
     */
    public int getNumberOfChildLevels() {
        return levels.size();
    }

    /**
     * Gets the number of Common Content topics in the Level.
     *
     * @return The number of Common Content topics
     */
    public int getNumberOfCommonContents() {
        return commonContents.size();
    }

    /**
     * Inserts a node before the another node in the level.
     *
     * @param newNode The node to be inserted.
     * @param oldNode The node that the new node should be inserted in front of.
     * @return True if the node was inserted correctly otherwise false.
     */
    public boolean insertBefore(final Node newNode, final Node oldNode) {
        if (oldNode == null || newNode == null) {
            return false;
        }

        int index = nodes.indexOf(oldNode);
        if (index != -1) {
            // Remove the parent from the new node if one exists
            if (newNode.getParent() != null) {
                newNode.removeParent();
            }
            newNode.setParent(this);
            // Add the node to the relevant list
            if (newNode instanceof Level) {
                levels.add((Level) newNode);
            } else if (newNode instanceof SpecTopic) {
                topics.add((SpecTopic) newNode);
            }
            // Insert the node
            if (index == 0) {
                nodes.addFirst(newNode);
            } else {
                nodes.add(index - 1, newNode);
            }
            return true;
        } else {
            return false;
        }
    }

    public SpecNode getFirstSpecNode() {
        for (final Node node : nodes) {
            if (node instanceof SpecNode) {
                return (SpecNode) node;
            }
        }

        return null;
    }

    /**
     * Get the type of level.
     *
     * @return A LevelType that represents the type of level.
     */
    public LevelType getLevelType() {
        return type;
    }

    /**
     * Get the External Target ID for the level if one exists.
     *
     * @return A String that represents an External Target ID if one exists otherwise null.
     */
    public String getExternalTargetId() {
        return externalTargetId;
    }

    /**
     * Set the External Target ID for the level.
     *
     * @param externalTargetId The External Target ID to associate with the level.
     */
    public void setExternalTargetId(final String externalTargetId) {
        this.externalTargetId = externalTargetId;
    }

    /**
     * Appends a Comment node to the Level.
     *
     * @param comment The Comment Node to be appended.
     */
    public void appendComment(final Comment comment) {
        appendChild(comment);
    }

    /**
     * Creates and appends a Comment node to the Level.
     *
     * @param comment The Comment to be appended to the level.
     */
    public void appendComment(final String comment) {
        appendComment(new Comment(comment));
    }

    /**
     * Removes a Comment Node from the Level.
     *
     * @param comment The Comment node to be removed.
     */
    public void removeComment(final Comment comment) {
        removeChild(comment);
    }

    public InfoTopic getInfoTopic() {
        return infoTopic;
    }

    public void setInfoTopic(final InfoTopic infoTopic) {
        if (this.infoTopic != null) {
            infoTopic.setLevel(null);
        }

        this.infoTopic = infoTopic;
        if (infoTopic != null) {
            infoTopic.setLevel(this);
        }
    }

    /**
     * Gets a ordered linked list of the child nodes within the level.
     *
     * @return The ordered list of child nodes for the level.
     */
    public List<Node> getChildNodes() {
        return Collections.unmodifiableList(nodes);
    }

    /**
     * Gets the total number of Children nodes for the level and its child levels.
     *
     * @return The total number of child nodes for the level and child levels.
     */
    protected Integer getTotalNumberOfChildren() {
        Integer numChildrenNodes = 0;
        for (Level childLevel : levels) {
            numChildrenNodes += childLevel.getTotalNumberOfChildren();
        }
        return nodes.size() + numChildrenNodes;
    }

    /**
     * Checks to see if this level or any of its children contain SpecTopics.
     *
     * @return True if the level or the levels children contain at least one SpecTopic.
     */
    public boolean hasSpecTopics() {
        if (getSpecTopics().size() > 0) {
            return true;
        }

        for (final Level childLevel : levels) {
            if (childLevel.hasSpecTopics()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks to see if this level or any of its children contain CommonContent.
     *
     * @return True if the level or the levels children contain at least one CommonContent.
     */
    public boolean hasCommonContents() {
        if (getNumberOfCommonContents() > 0) {
            return true;
        }

        for (final Level childLevel : levels) {
            if (childLevel.hasCommonContents()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks to see if this level or any of its children contain SpecTopics that represent a revision.
     *
     * @return True if the level or the levels children contain at least one SpecTopic that is a revision.
     */
    public boolean hasRevisionSpecTopics() {
        for (final SpecTopic specTopic : getSpecTopics()) {
            if (specTopic.getRevision() != null) {
                return true;
            }
        }

        for (final Level childLevel : getChildLevels()) {
            if (childLevel.hasRevisionSpecTopics()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Integer getStep() {
        if (getParent() == null) {
            return null;
        } else {
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
    }

    @Override
    public String getText() {
        final String options = getOptionsString();
        final String title = (translatedTitle == null ? (this.title == null ? "" : this.title) : translatedTitle);
        final StringBuilder output = new StringBuilder();
        if (type != LevelType.BASE) {
            output.append(type.getTitle()).append(":");
            if (type != LevelType.INITIAL_CONTENT) {
                if (!isNullOrEmpty(title)) {
                    output.append(" ");
                    output.append(ContentSpecUtilities.escapeTitle(title));
                }
            }
        }
        // Add any options
        if (!options.equals("")) {
            output.append(" [").append(options).append("]");
        }
        
        if (type != LevelType.BASE) {
            if (getInfoTopic() != null) {
                output.append(" ").append(getInfoTopic().getText());
            }
            if (getTargetId() != null) {
                output.append(" [").append(getTargetId()).append("]");
            }
            if (externalTargetId != null) {
                output.append(" [").append(externalTargetId).append("]");
            }
        }

        // Append any relationship text
        output.append(getRelationshipText(getSpacer() + SPACER));

        setText(output.toString());
        return text;
    }

    /**
     * Returns a String Representation of the Level.
     *
     * @return The string representation of the Level.
     */
    @Override
    public String toString() {
        final StringBuilder output = new StringBuilder();
        if (type != LevelType.BASE) {
            output.append(getSpacer());
            output.append(getText());
            output.append("\n");
        }

        for (final Node node : nodes) {
            final String nodeOutput = node.toString();
            output.append(nodeOutput);
        }

        return output.toString();
    }

    @Override
    protected void removeParent() {
        getParent().removeChild(this);
        setParent(null);
    }

    /**
     * Finds the closest node in the contents of a level.
     *
     * @param topic           The node we need to find the closest match for.
     * @param checkParentNode TODO
     * @return TODO
     */
    public SpecTopic getClosestTopic(final SpecTopic topic, final boolean checkParentNode) {
        return getClosestTopic(topic, this, checkParentNode);
    }

    /**
     * Finds the closest node in the contents of a level.
     *
     * @param topic           The node we need to find the closest match for.
     * @param callerNode      TODO
     * @param checkParentNode TODO
     * @return TODO
     */
    public SpecTopic getClosestTopic(final SpecTopic topic, final SpecNode callerNode, final boolean checkParentNode) {
        /*
         * Check this level to see if the topic exists
         */
        final List<SpecTopic> topics = getSpecTopics();
        for (final SpecTopic childTopic : topics) {
            if (childTopic == topic || childTopic.getId().equals(topic.getId())) {
                return childTopic;
            }
        }

        /*
         * If we get to this stage, then the topic wasn't directly at this level. So we should try this levels, child levels
         * first.
         */
        final List<Level> childLevels = getChildLevels();
        for (final Level childLevel : childLevels) {
            if (callerNode == childLevel) {
                continue;
            } else {
                final SpecTopic childLevelTopic = childLevel.getClosestTopic(topic, callerNode, false);
                if (childLevelTopic != null) {
                    return childLevelTopic;
                }
            }
        }

        /*
         * If we still haven't found the closest node then check this nodes parents.
         */
        if (getParent() != null && checkParentNode) {
            return getParent().getClosestTopic(topic, this, checkParentNode);
        }

        return null;
    }

    /**
     * This function checks the levels nodes and child nodes to see if it can match a spec topic for a topic database id.
     *
     * @param DBId            The topic database id
     * @param checkParentNode If the function should check the levels parents as well
     * @return The closest available SpecTopic that matches the DBId otherwise null.
     */
    public SpecTopic getClosestTopicByDBId(final Integer DBId, final boolean checkParentNode) {
        return getClosestTopicByDBId(DBId, this, checkParentNode);
    }

    /**
     * This function checks the levels nodes and child nodes to see if it can match a spec topic for a topic database id.
     *
     * @param DBId            The topic database id
     * @param callerNode      The node that called this function so that it isn't rechecked
     * @param checkParentNode If the function should check the levels parents as well
     * @return The closest available SpecTopic that matches the DBId otherwise null.
     */
    public SpecTopic getClosestTopicByDBId(final Integer DBId, final SpecNode callerNode, final boolean checkParentNode) {
        /*
         * Check this level to see if the topic exists
         */
        final List<SpecTopic> topics = getSpecTopics();
        for (final SpecTopic childTopic : topics) {
            if (childTopic.getDBId().equals(DBId)) {
                return childTopic;
            }
        }

        /*
         * If we get to this stage, then the topic wasn't directly at this level. So we should try this levels, child levels
         * first.
         */
        final List<Level> childLevels = getChildLevels();
        for (final Level childLevel : childLevels) {
            if (childLevel == callerNode) {
                continue;
            } else {
                final SpecTopic childLevelTopic = childLevel.getClosestTopicByDBId(DBId, callerNode, false);
                if (childLevelTopic != null) {
                    return childLevelTopic;
                }
            }
        }

        /*
         * If we still haven't found the closest node then check this nodes parents.
         */
        if (getParent() != null && checkParentNode) {
            return getParent().getClosestTopicByDBId(DBId, this, checkParentNode);
        }

        return null;
    }

    /**
     * This function checks the levels nodes and child nodes to see if it can match a spec topic for a topic database id.
     *
     * @param targetId            The topic database id
     * @param checkParentNode If the function should check the levels parents as well
     * @return The closest available SpecTopic that matches the DBId otherwise null.
     */
    public SpecNode getClosestSpecNodeByTargetId(final String targetId, final boolean checkParentNode) {
        return getClosestSpecNodeByTargetId(targetId, this, checkParentNode);
    }

    /**
     * This function checks the levels nodes and child nodes to see if it can match a spec topic for a topic database id.
     *
     * @param targetId            The topic database id
     * @param callerNode      The node that called this function so that it isn't rechecked
     * @param checkParentNode If the function should check the levels parents as well
     * @return The closest available SpecTopic that matches the DBId otherwise null.
     */
    public SpecNode getClosestSpecNodeByTargetId(final String targetId, final SpecNode callerNode, final boolean checkParentNode) {
        /*
         * Check this level to see if the topic exists
         */
        final List<Node> children = getChildNodes();
        for (final Node childNode : children) {
            if (childNode instanceof SpecNode) {
                final SpecNode specNode = (SpecNode) childNode;
                if (specNode.getTargetId() != null && specNode.getTargetId().equals(targetId)) {
                    return specNode;
                }
            }
        }

        /*
         * If we get to this stage, then the topic wasn't directly at this level. So we should try this levels, child levels
         * first.
         */
        final List<Level> childLevels = getChildLevels();
        for (final Level childLevel : childLevels) {
            if (childLevel == callerNode) {
                continue;
            } else {
                final SpecNode childLevelNode = childLevel.getClosestSpecNodeByTargetId(targetId, callerNode, false);
                if (childLevelNode != null) {
                    return childLevelNode;
                }
            }
        }

        /*
         * If we still haven't found the closest node then check this nodes parents.
         */
        if (getParent() != null && checkParentNode) {
            return getParent().getClosestSpecNodeByTargetId(targetId, this, checkParentNode);
        }

        return null;
    }

    /**
     * Checks to see if a SpecTopic exists within this level or its children.
     *
     * @param topic The topic to see if it exists
     * @return True if the topic exists within this level or its children otherwise false.
     */
    public boolean isSpecTopicInLevel(final SpecTopic topic) {
        final SpecTopic foundTopic = getClosestTopic(topic, false);
        return foundTopic != null;
    }

    /**
     * Checks to see if a SpecTopic exists within this level or its children.
     *
     * @param topicId The ID of the topic to see if it exists.
     * @return True if the topic exists within this level or its children otherwise false.
     */
    public boolean isSpecTopicInLevelByTopicID(final Integer topicId) {
        final SpecTopic foundTopic = getClosestTopicByDBId(topicId, false);
        return foundTopic != null;
    }

    /**
     * Checks to see if a SpecNode exists within this level or its children.
     *
     * @param targetId The Target ID of the level/topic to see if it exists.
     * @return True if the level/topic exists within this level or its children otherwise false.
     */
    public boolean isSpecNodeInLevelByTargetID(final String targetId) {
        final SpecNode foundTopic = getClosestSpecNodeByTargetId(targetId, false);
        return foundTopic != null;
    }

    @Override
    public String getUniqueLinkId(final boolean useFixedUrls) {
        // Get the xref id
        final String levelXRefId;
        if (useFixedUrls && getFixedUrl() != null) {
            levelXRefId = getFixedUrl();
        } else {
            final String prefix = ContentSpecUtilities.getLevelPrefix(this);
            final String escapedTitle = DocBookUtilities.escapeTitle(title);

            if (useFixedUrls && !(isNullOrEmpty(escapedTitle) || escapedTitle.matches("^\\d+$"))) {
                levelXRefId = prefix + escapedTitle;
            } else {
                levelXRefId = prefix + getLevelType().getTitle().replace(" ", "_") + "ID" + getUniqueId();
            }
        }

        return levelXRefId + (duplicateId == null ? "" : ("-" + duplicateId));
    }
}
