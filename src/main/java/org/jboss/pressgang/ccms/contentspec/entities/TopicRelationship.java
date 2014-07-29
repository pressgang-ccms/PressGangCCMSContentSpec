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
