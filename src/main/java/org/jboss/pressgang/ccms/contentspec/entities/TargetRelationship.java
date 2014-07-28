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

import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.SpecNode;
import org.jboss.pressgang.ccms.contentspec.SpecNodeWithRelationships;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.enums.RelationshipType;

/**
 * A class to specify a relationship between a topic and a level.
 */
public class TargetRelationship extends Relationship {
    private final SpecNodeWithRelationships primaryNode;
    private final SpecNode secondaryNode;

    public TargetRelationship(final SpecNodeWithRelationships primaryNode, final Level level, final RelationshipType type) {
        super(primaryNode.getUniqueId(), level.getTargetId(), type);
        this.primaryNode = primaryNode;
        secondaryNode = level;
    }

    public TargetRelationship(final SpecNodeWithRelationships primaryNode, final Level level, final RelationshipType type, final String title) {
        super(primaryNode.getUniqueId(), level.getTargetId(), type, title);
        this.primaryNode = primaryNode;
        secondaryNode = level;
    }

    public TargetRelationship(final SpecNodeWithRelationships primaryNode, final SpecTopic secondaryTopic, final RelationshipType type) {
        super(primaryNode.getUniqueId(), secondaryTopic.getTargetId(), type);
        this.primaryNode = primaryNode;
        secondaryNode = secondaryTopic;
    }

    public TargetRelationship(final SpecNodeWithRelationships primaryNode, final SpecTopic secondaryTopic, final RelationshipType type,
            final String title) {
        super(primaryNode.getUniqueId(), secondaryTopic.getTargetId(), type, title);
        this.primaryNode = primaryNode;
        secondaryNode = secondaryTopic;
    }

    public SpecNodeWithRelationships getPrimaryRelationship() {
        return primaryNode;
    }

    public SpecNode getSecondaryRelationship() {
        return secondaryNode;
    }
}
