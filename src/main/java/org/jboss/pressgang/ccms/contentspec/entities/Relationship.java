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
