package org.jboss.pressgang.ccms.contentspec.entities;

import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.enums.RelationshipType;

/**
 * A class to specify a relationship between a topic and a level.
 */
public class ProcessRelationship extends TargetRelationship {
    public ProcessRelationship(final SpecTopic topic, final SpecTopic secondaryTopic, final RelationshipType type) {
        super(topic, secondaryTopic, type);
    }

    public ProcessRelationship(final SpecTopic topic, final SpecTopic secondaryTopic, final RelationshipType type, final String title) {
        super(topic, secondaryTopic, type, title);
    }

    @Override
    public SpecTopic getSecondaryRelationship() {
        return (SpecTopic) super.getSecondaryRelationship();
    }
}
