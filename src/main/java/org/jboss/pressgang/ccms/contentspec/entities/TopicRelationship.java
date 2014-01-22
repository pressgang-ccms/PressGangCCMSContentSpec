package org.jboss.pressgang.ccms.contentspec.entities;

import org.jboss.pressgang.ccms.contentspec.SpecNodeWithRelationships;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.enums.RelationshipType;


/*
 * A class to specify a relationship between two topics.
 */
public class TopicRelationship extends Relationship {
    private final SpecNodeWithRelationships primaryRelationship;
    private final SpecTopic secondaryRelationship;

    public TopicRelationship(final SpecNodeWithRelationships primaryNode, final SpecTopic secondaryTopic, final RelationshipType type) {
        super(primaryNode.getUniqueId(), secondaryTopic.getId(), type);
        primaryRelationship = primaryNode;
        secondaryRelationship = secondaryTopic;
    }

    public TopicRelationship(final SpecNodeWithRelationships primaryNode, final SpecTopic secondaryTopic, final RelationshipType type,
            final String title) {
        super(primaryNode.getUniqueId(), secondaryTopic.getId(), type, title);
        primaryRelationship = primaryNode;
        secondaryRelationship = secondaryTopic;
    }

    public SpecTopic getSecondaryRelationship() {
        return secondaryRelationship;
    }

    public SpecNodeWithRelationships getPrimaryRelationship() {
        return primaryRelationship;
    }
}
