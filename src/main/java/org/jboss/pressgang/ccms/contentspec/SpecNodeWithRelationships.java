/*
  Copyright 2011-2014 Red Hat

  This file is part of PressGang CCMS.

  PressGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PressGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PressGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.contentspec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jboss.pressgang.ccms.contentspec.entities.Relationship;
import org.jboss.pressgang.ccms.contentspec.entities.TargetRelationship;
import org.jboss.pressgang.ccms.contentspec.entities.TopicRelationship;
import org.jboss.pressgang.ccms.contentspec.enums.RelationshipType;
import org.jboss.pressgang.ccms.contentspec.utils.ContentSpecUtilities;
import org.jboss.pressgang.ccms.utils.common.StringUtilities;

public abstract class SpecNodeWithRelationships extends SpecNode {
    protected List<TopicRelationship> topicRelationships = new ArrayList<TopicRelationship>();
    protected List<TargetRelationship> topicTargetRelationships = new ArrayList<TargetRelationship>();
    protected List<TargetRelationship> levelRelationships = new ArrayList<TargetRelationship>();
    protected List<Relationship> relationships = new LinkedList<Relationship>();

    protected SpecNodeWithRelationships(final int lineNumber, final String text) {
        super(lineNumber, text);
    }

    protected SpecNodeWithRelationships(final String text) {
        super(text);
    }

    protected SpecNodeWithRelationships() {
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
     * Add a relationship to the target level.
     *
     * @param level The target level that is to be related to.
     * @param type  The type of the relationship.
     */
    public void addRelationshipToTarget(final Level level, final RelationshipType type) {
        final TargetRelationship relationship = new TargetRelationship(this, level, type);
        levelRelationships.add(relationship);
        relationships.add(relationship);
    }

    /**
     * Add a relationship to the target level.
     *
     * @param level The target level that is to be related to.
     * @param type  The type of the relationship.
     * @param title The title of the target level to be related to.
     */
    public void addRelationshipToTarget(final Level level, final RelationshipType type, final String title) {
        final TargetRelationship relationship = new TargetRelationship(this, level, type, title);
        levelRelationships.add(relationship);
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
    public List<Relationship> getPreviousRelationships() {
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

    protected String getRelationshipText(final String spacer) {
        final StringBuilder output = new StringBuilder();
        if (!getPrerequisiteRelationships().isEmpty()) {
            boolean useLongSyntax = printRelationshipsWithLongSyntax(getPrerequisiteRelationships());
            output.append(generateRelationshipText(RelationshipType.PREREQUISITE, !useLongSyntax, spacer));
        }

        if (!getRelatedRelationships().isEmpty()) {
            boolean useLongSyntax = printRelationshipsWithLongSyntax(getRelatedRelationships());
            output.append(generateRelationshipText(RelationshipType.REFER_TO, !useLongSyntax, spacer));
        }

        if (!getLinkListRelationships().isEmpty()) {
            boolean useLongSyntax = printRelationshipsWithLongSyntax(getLinkListRelationships());
            output.append(generateRelationshipText(RelationshipType.LINKLIST, !useLongSyntax, spacer));
        }

        return output.toString();
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
                relatedIds.add(related.getSecondaryRelationshipId());
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
                if (related.getRelationshipTitle() != null && !related.getRelationshipTitle().trim().isEmpty()) {
                    retValue.append(ContentSpecUtilities.escapeRelationshipTitle(related.getRelationshipTitle())).append(" ");
                }
                retValue.append("[");
                retValue.append(related.getSecondaryRelationshipId());
                retValue.append("]");
            }
        }

        retValue.append("]");
        return retValue.toString();
    }
}
