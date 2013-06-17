package org.jboss.pressgang.ccms.contentspec.entities;

import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.enums.RelationshipType;


/*
 * A class to specify a relationship between two topics.
 */
public class TopicRelationship extends Relationship {
    private final SpecTopic mainRelationship;
    private final SpecTopic secondaryRelationship;

    public TopicRelationship(final SpecTopic mainTopic, final SpecTopic secondaryTopic, final RelationshipType type) {
        super(mainTopic.getId(), secondaryTopic.getId(), type);
        mainRelationship = mainTopic;
        secondaryRelationship = secondaryTopic;
    }

    public TopicRelationship(final SpecTopic mainTopic, final SpecTopic secondaryTopic, final RelationshipType type, final String title) {
        super(mainTopic.getId(), secondaryTopic.getId(), type, title);
        mainRelationship = mainTopic;
        secondaryRelationship = secondaryTopic;
    }

    public SpecTopic getSecondaryRelationship() {
        return secondaryRelationship;
    }

    public SpecTopic getMainRelationship() {
        return mainRelationship;
    }
}
