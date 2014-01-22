package org.jboss.pressgang.ccms.contentspec.entities;

import org.jboss.pressgang.ccms.contentspec.enums.RelationshipType;


/**
 * A class to hold a basic relationship until it can be processed at a later stage.
 */
public class Relationship {
    private final String primaryRelationshipId;
    private final String secondaryRelationshipId;
    private final String relationshipTitle;
    private final RelationshipType type;

    public Relationship(final String mainId, final String secondaryId, final RelationshipType type) {
        this(mainId, secondaryId, type, null);
    }

    public Relationship(final String primaryId, final String secondaryId, final RelationshipType type, final String title) {
        primaryRelationshipId = primaryId;
        secondaryRelationshipId = secondaryId;
        this.type = type;
        relationshipTitle = title;
    }

    public String getSecondaryRelationshipId() {
        return secondaryRelationshipId;
    }

    public String getPrimaryRelationshipId() {
        return primaryRelationshipId;
    }

    public RelationshipType getType() {
        return type;
    }

    public String getRelationshipTitle() {
        return relationshipTitle;
    }
}
